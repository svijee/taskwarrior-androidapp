package org.svij.taskwarriorapp.ui;

import java.util.Calendar;
import java.util.Date;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;

public class DatePickerFragment extends DialogFragment {
	OnDateSetListener onDateSet;
	private long timestamp;

	public Dialog onCreateDialog(Bundle savedInstanceState) {
		final Calendar cal = Calendar.getInstance();

		if (timestamp != 0) {
			cal.setTime(new Date(timestamp));
		}

		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH);
		int day = cal.get(Calendar.DAY_OF_MONTH);

		return new DatePickerDialog(getActivity(), onDateSet, year, month, day);
	}

	public void setCallBack(OnDateSetListener ondate) {
		onDateSet = ondate;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
}
