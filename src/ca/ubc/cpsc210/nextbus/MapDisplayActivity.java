package ca.ubc.cpsc210.nextbus;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import ca.ubc.cpsc210.nextbus.model.BusStop;
import ca.ubc.cpsc210.nextbus.model.FavouriteStops;

/**
 * Activity that controls MapDisplayFragment
 */
public class MapDisplayActivity extends FragmentActivity {
	private MapDisplayFragment fragment;

	@TargetApi(11)
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map_stop);

		fragment = (MapDisplayFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map_stop);

		if (fragment == null) {
			fragment = new MapDisplayFragment();

			getSupportFragmentManager().beginTransaction()
					.add(R.id.map_stop, fragment).commit();
		}

		BusStop selectedStop = FavouriteStops.getInstance(this).getSelectedStop();
		fragment.setBusStop(selectedStop);
		setTitle("Stop #: " + selectedStop.getStopNum());

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
			getActionBar().setSubtitle("Buses serving this stop");
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			NavUtils.navigateUpTo(this,
					new Intent(this, StopListActivity.class));
			overridePendingTransition(R.anim.push_left_in,
					R.anim.push_right_out);
			return true;
		}

		return super.onOptionsItemSelected(item);
	}
}
