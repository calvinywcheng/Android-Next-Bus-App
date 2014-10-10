package ca.ubc.cpsc210.nextbus;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.text.method.ScrollingMovementMethod;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import ca.ubc.cpsc210.nextbus.model.BusStop;
import ca.ubc.cpsc210.nextbus.model.FavouriteStops;

/**
 * Activity that controls StopListFragment
 */
public class StopListActivity extends FragmentActivity implements StopListFragment.Callbacks {
	private static final String MAP_FRAG = "map_fragment";
	private static final String LIST_FRAG = "list_fragment";

	@TargetApi(11)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_nextbus);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setTitle("Next Bus");
			getActionBar().setSubtitle("Favourite bus stops");
		}

		// Add stop list fragment
		FragmentManager fm = getSupportFragmentManager();
		Fragment fragment = fm.findFragmentById(R.id.stop_list_container);

		if (fragment == null) {
			fragment = new StopListFragment();
			fm.beginTransaction().add(R.id.stop_list_container, fragment, LIST_FRAG).commit();
		}
		
		// Add/remove map fragment depending on availability of container
		MapDisplayFragment mapFragment = (MapDisplayFragment) fm.findFragmentByTag(MAP_FRAG);
		
		if(findViewById(R.id.map_container) != null) {
			if(mapFragment == null) 
				mapFragment = new MapDisplayFragment();	
			fm.beginTransaction().add(R.id.map_container, mapFragment, MAP_FRAG).commit();
			BusStop selectedStop = FavouriteStops.getInstance(this).getSelectedStop();
			mapFragment.setBusStop(selectedStop);
		}
		else {
			// when map fragment is removed, any option menu items it added are removed from action bar
			if(mapFragment != null && !mapFragment.isDetached())
				fm.beginTransaction().remove(mapFragment).commit();
		}
		
		// Allow user to scroll through estimated wait times
		View v = findViewById(R.id.wait_times_container);
		if(v != null) {
			TextView tv = (TextView) v.findViewById(R.id.bus_wait_times); 
			tv.setMovementMethod(new ScrollingMovementMethod());
		}
	}
	
	@Override
	public void onStopSelection(int position) {
		FragmentManager fm = getSupportFragmentManager();
		MapDisplayFragment mapFragment = (MapDisplayFragment) fm.findFragmentByTag(MAP_FRAG);
		FavouriteStops favs = FavouriteStops.getInstance(this);
		favs.setIndexOfSelected(position);
		
		if(mapFragment != null) {
			mapFragment.setBusStop(favs.getSelectedStop());
			mapFragment.update(true);
		}
	}
	
	@Override
	public void updateStopInfo(Bundle args) {
		View v = findViewById(R.id.wait_times_container);
		
		if(v != null) {
			BusStop selectedStop = FavouriteStops.getInstance(this).getSelectedStop();
			TextView title = (TextView) v.findViewById(R.id.bus_wait_times_title);
			title.setText("Next Bus: " + selectedStop.getStopNum());
			TextView tv = (TextView) v.findViewById(R.id.bus_wait_times); 
			tv.setText(args.getString("wait times"));
		}
		else {
			BusWaitTimeDialog stopInfo = new BusWaitTimeDialog();
			stopInfo.setArguments(args);
			stopInfo.show(getSupportFragmentManager(), "stopinfo");
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.map_refresh:
			FragmentManager fm = getSupportFragmentManager();
			StopListFragment slf = (StopListFragment) fm.findFragmentByTag(LIST_FRAG);
			if(slf != null)
				slf.updateBusInfoAtSelectedStop();
			
			MapDisplayFragment mdf = (MapDisplayFragment) fm.findFragmentByTag(MAP_FRAG);
			if(mdf != null)
				mdf.update(false);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
