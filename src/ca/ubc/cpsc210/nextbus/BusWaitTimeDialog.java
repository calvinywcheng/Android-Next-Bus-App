package ca.ubc.cpsc210.nextbus;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

/**
 * A dialog that shows bus wait time information for
 * a particular bus stop.  Gives user option of
 * displaying bus locations on map.
 */
public class BusWaitTimeDialog extends DialogFragment {

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setMessage(getArguments().getString("wait times"))
				.setTitle(getArguments().getString("title"))
				.setPositiveButton(R.string.map_stops,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
								Intent mapIntent = new Intent(getActivity(),
										MapDisplayActivity.class);
								Bundle b = new Bundle();
								b.putAll(getArguments());
								mapIntent.putExtras(b);
								startActivity(mapIntent);
								getActivity().overridePendingTransition(
										R.anim.push_right_in,
										R.anim.push_left_out);
							}
						})
				.setNegativeButton(R.string.ok, null);

		return builder.create();
	}
}
