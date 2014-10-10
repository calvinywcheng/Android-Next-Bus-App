package ca.ubc.cpsc210.nextbus;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

/**
 * A Dialog that allows the user to enter a bus stop number. Used when adding a
 * new bus stop to the list of stops.
 */
public class NewStopDialog extends DialogFragment {
	private String selectedStop;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		final View v = getActivity().getLayoutInflater().inflate(
				R.layout.add_stop, null);
		
		AlertDialog dialog = new AlertDialog.Builder(getActivity())
				.setView(v)
				.setTitle(R.string.add_stop_title)
				.setPositiveButton(android.R.string.ok, new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						EditText et = (EditText) v.findViewById(R.id.edit_stop);
						selectedStop = et.getText().toString();
						sendResult(Activity.RESULT_OK);
					}
				})
				.setNegativeButton(android.R.string.cancel, null)
				.create();
		
		dialog.show();
		dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
		
		return dialog;
	}

	/**
	 * Sends result to target activity when user presses OK button
	 * @param resultCode  the result code
	 */
	private void sendResult(int resultCode) {
		if (getTargetFragment() == null)
			return;

		Intent i = new Intent();
		i.putExtra(StopListFragment.NEW_STOP_ARG, selectedStop);
		getTargetFragment().onActivityResult(getTargetRequestCode(),
				resultCode, i);
	}
}
