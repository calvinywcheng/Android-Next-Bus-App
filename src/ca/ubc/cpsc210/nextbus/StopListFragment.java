package ca.ubc.cpsc210.nextbus;

import java.util.ArrayList;
import java.util.Set;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ListFragment;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import ca.ubc.cpsc210.exception.TranslinkException;
import ca.ubc.cpsc210.nextbus.model.BusStop;
import ca.ubc.cpsc210.nextbus.model.BusWaitTime;
import ca.ubc.cpsc210.nextbus.model.FavouriteStops;
import ca.ubc.cpsc210.nextbus.translink.ITranslinkService;
import ca.ubc.cpsc210.nextbus.translink.TranslinkService;

public class StopListFragment extends ListFragment {

	public static final String NEW_STOP_ARG = "new_stop_arg";
	private static final String NEW_STOP_TAG = "new_stop";
	private static final int REQUEST_STOP = 0;
	private ArrayAdapter<BusStop> adapter;
	private FavouriteStops selectedStops;
	private BusStop selectedStop;
	private ITranslinkService tlService;
	private Callbacks callbacks;
	
	/**
	 * Callbacks to be implemented by parent activity.
	 */
	public static interface Callbacks {
		public void onStopSelection(int position);
		public void updateStopInfo(Bundle args);
	}

	@TargetApi(11)
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);

		selectedStops = FavouriteStops.getInstance(getActivity());
		selectedStop = null;
		adapter = new StopListAdapter(selectedStops.getFavourites());
		setListAdapter(adapter);
		
		tlService = new TranslinkService(getActivity());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		return inflater.inflate(R.layout.stop_list_layout, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		ListView listView = getListView();
		registerForContextMenu(listView);
		listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		listView.setSelector(R.drawable.list_selected_item);
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		callbacks = (Callbacks) activity;
	}
	
	@Override
	public void onDetach() {
		callbacks = null;
		super.onDetach();
	}

	@Override
	public void onPause() {
		super.onPause();
		selectedStops.saveFavourites();
	}
	
	@Override
	public void onDestroy() {
		//tlService.shutdown();
		
		super.onDestroy();
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.fragment_add_stop, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_item_new_stop:
			FragmentManager fm = getActivity().getSupportFragmentManager();
			NewStopDialog dialog = new NewStopDialog();
			dialog.setTargetFragment(this, REQUEST_STOP);
			dialog.show(fm, NEW_STOP_TAG);
			return true;
		case R.id.menu_item_about:
			AlertDialog.Builder dialogBldr = new AlertDialog.Builder(getActivity());
			dialogBldr.setTitle(R.string.about);
			dialogBldr.setMessage(R.string.legend);
			dialogBldr.setNeutralButton(R.string.ok, null);
			dialogBldr.create().show();
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != Activity.RESULT_OK)
			return;

		if (requestCode == REQUEST_STOP) {
			new GetStopInfo().execute(data.getStringExtra(NEW_STOP_ARG));
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(R.string.delete_stop);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		// only one item on menu so far, so no need to check which was selected
		AdapterView.AdapterContextMenuInfo info 
				= (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

		BusStop toRemove 
				= (BusStop) getListView().getItemAtPosition(info.position);
		selectedStops.removeStop(toRemove);
		adapter.notifyDataSetChanged();
		return true;
	}

	/**
	 * Add a bus stop to favourites
	 * @param toAdd  the bus stop to add to favourites
	 */
	public void addStop(BusStop toAdd) {
		selectedStops.addStop(toAdd);
		adapter.notifyDataSetChanged();
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		selectedStop = ((StopListAdapter) getListAdapter()).getItem(position);
		callbacks.onStopSelection(position);
		updateBusInfoAtSelectedStop();
	}
	
	/**
	 * Update bus wait time estimates for selected stop
	 */
	void updateBusInfoAtSelectedStop() {
		if(selectedStop != null)
			new GetBusWaitTimes().execute(selectedStop);	
	}
	
	/**
	 * Helper to create simple AlertDialog that displays a message
	 * @param msg  the message to display
	 * @return  the alert dialog
	 */
	private AlertDialog createSimpleDialog(String msg) {
		AlertDialog.Builder dialogBldr = new AlertDialog.Builder(getActivity());
		dialogBldr.setMessage(msg);
		dialogBldr.setNeutralButton(R.string.ok, null);

		return dialogBldr.create();
	}

	/** 
	 * Asynchronous task to get bus wait time estimates from Translink service.
	 * Displays progress dialog while running in background.  
	 */
	private class GetBusWaitTimes extends
			AsyncTask<BusStop, Void, Void> {
		private ProgressDialog dialog = new ProgressDialog(getActivity());
		private BusStop selectedStop;
		private String exceptionMsg = null;

		@Override
		protected void onPreExecute() {
			dialog.setMessage("Retrieving bus info...");
			dialog.show();
		}

		@Override
		protected Void doInBackground(BusStop... selected) {
			selectedStop = selected[0];

			try {
				tlService.addWaitTimeEstimatesToStop(selectedStop);
			} catch (TranslinkException e) {
				e.printStackTrace();
				exceptionMsg = e.getMessage();
				exceptionMsg += "\n(code: " + e.getCode() + ")";
			}
			
			return null;
		}

		@Override
		protected void onPostExecute(Void dummy) {
			dialog.dismiss();

			if (exceptionMsg == null) {
				Bundle arguments = new Bundle();
				String waitTimes = waitTimesToString(selectedStop.getWaitTimes());
				
				arguments.putString("wait times", waitTimes );
				arguments.putString("title", selectedStop.getLocationDesc()
						+ " next bus...");
				callbacks.updateStopInfo(arguments);
			} else {
				AlertDialog dialog = createSimpleDialog(exceptionMsg);
				dialog.show();
			}
		}
		
		/**
		 * Produces a string representation of at most MAX_ESTIMATES wait time 
		 * estimates, one per line.  Uses BusWaitTime's toString method to
		 * get string representation of each wait time.  List is sorted
		 * from shortest to longest wait time. If no buses are expected,
		 * returns "No buses expected".
		 * 
		 * @param waitTimes  set of wait times
		 * @return string representation of wait times for bus at this stop
		 */
		private String waitTimesToString(Set<BusWaitTime> waitTimes) {
			final int MAX_ESTIMATES = 6;
			final String NONE_EXPECTED = "No buses expected";
			int count = 0;
			String str = "";

			for (BusWaitTime next : waitTimes) {
				str += next.toString() + "\n";
				count++;
				if (count >= MAX_ESTIMATES)
					break;
			}
			
			if( str.equals("") )
				return NONE_EXPECTED;
			else
				return str;
		}
	}

	/** 
	 * Asynchronous task to get bus stop information from Translink service.
	 * Displays progress dialog while running in background.  
	 */
	private class GetStopInfo extends AsyncTask<String, Void, BusStop> {
		private ProgressDialog dialog = new ProgressDialog(getActivity());
		private String selectedStop;
		private String errorMsg;

		@Override
		protected void onPreExecute() {
			dialog.setMessage("Retrieving stop info...");
			dialog.show();
		}

		@Override
		protected BusStop doInBackground(String... selectedStop) {
			BusStop info = null;
			this.selectedStop = selectedStop[0];

			try {
				info = tlService.getBusStop(this.selectedStop);
			} catch (TranslinkException e) {
				e.printStackTrace();
				errorMsg = e.getMessage();
				errorMsg += "\n(code: " + e.getCode() + ")";
				return null;
			}

			return info;
		}

		@Override
		protected void onPostExecute(BusStop result) {
			dialog.dismiss();

			if (result != null) {
				addStop(result);
			} else {
				String msg = "Unable to get information for stop #: " + selectedStop;
				msg += "\n\n" + errorMsg;
				
				AlertDialog dialog = createSimpleDialog(msg);
				dialog.show();
			}
		}
	}
	
	/**
	 * Custom list adapter
	 * 
	 * Adapted from "Android Programming - The Big Nerd Ranch Guide", Ch 9
	 * by Phillips and Hardy 
	 */
	private class StopListAdapter extends ArrayAdapter<BusStop> {
		public StopListAdapter(ArrayList<BusStop> favs) {
			super(getActivity(), 0, favs);
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if(convertView == null) {
				convertView = getActivity().getLayoutInflater()
						.inflate(R.layout.list_item_bus_stop, null);
			}
			
			BusStop info = getItem(position);
			
			TextView stopNum = (TextView) convertView.findViewById(R.id.list_stop_num);
			stopNum.setText(Integer.valueOf(info.getStopNum()).toString());
			
			TextView stopLocn = (TextView) convertView.findViewById(R.id.list_stop_location);
			stopLocn.setText(info.getLocationDesc());
			
			return convertView;
		}
	}
}
