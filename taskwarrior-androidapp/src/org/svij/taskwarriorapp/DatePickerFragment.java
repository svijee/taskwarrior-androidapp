package org.svij.taskwarriorapp;

import java.util.Calendar;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;

public class DatePickerFragment extends DialogFragment {
	OnDateSetListener onDateSet;

	public Dialog onCreateDialog(Bundle savedInstanceState) {
		final Calendar cal = Calendar.getInstance();
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH);
		int day = cal.get(Calendar.DAY_OF_MONTH);

		return new DatePickerDialog(getActivity(), onDateSet, year, month, day);
	}

	public void setCallBack(OnDateSetListener ondate) {
		onDateSet = ondate;
	}
}
