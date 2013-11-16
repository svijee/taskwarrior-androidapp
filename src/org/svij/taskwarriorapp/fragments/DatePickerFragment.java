/**
 * taskwarrior for android – a task list manager
 *
 * Copyright (c) 2012 Sujeevan Vijayakumaran
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, * subject to the following conditions:

 * The above copyright notice and this permission notice shall be included in
 * allcopies or substantial portions of the Software.

 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 * http://www.opensource.org/licenses/mit-license.php
 *
 */

package org.svij.taskwarriorapp.fragments;

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
