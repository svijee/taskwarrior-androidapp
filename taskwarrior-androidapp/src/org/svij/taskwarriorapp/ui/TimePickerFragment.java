package org.svij.taskwarriorapp.ui;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class TimePickerFragment extends DialogFragment {
	OnTimeSetListener onTimeSet;
	private long timestamp;

	public Dialog onCreateDialog(Bundle savedInstanceState) {
		final Calendar cal = Calendar.getInstance();

		Date date = new Date(timestamp);
		if (!DateFormat.getTimeInstance().format(date).equals("01:00:00")) {
			cal.setTime(date);
		}

		int hour = cal.get(Calendar.HOUR_OF_DAY);
		int minute = cal.get(Calendar.MINUTE);

		return new TimePickerDialog(getActivity(), onTimeSet, hour, minute,
				true);
	}

	public void setCallBack(OnTimeSetListener onTimeSet) {
		this.onTimeSet = onTimeSet;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
}
