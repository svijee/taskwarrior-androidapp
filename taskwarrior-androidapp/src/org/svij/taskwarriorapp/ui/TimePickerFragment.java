package org.svij.taskwarriorapp.ui;

import java.util.Calendar;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class TimePickerFragment extends DialogFragment {
	OnTimeSetListener onTimeSet;

	public Dialog onCreateDialog(Bundle savedInstanceState) {
		final Calendar cal = Calendar.getInstance();

		int hour = cal.get(Calendar.HOUR_OF_DAY);
		int minute = cal.get(Calendar.MINUTE);

		return new TimePickerDialog(getActivity(), onTimeSet, hour, minute,
				true);
	}

	public void setCallBack(OnTimeSetListener onTimeSet) {
		this.onTimeSet = onTimeSet;
	}
}
