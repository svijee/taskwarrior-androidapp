package org.svij.taskwarriorapp.data;

import java.util.Calendar;
import java.util.Date;

import android.text.format.DateUtils;

public class Recur {

	public static int getDueState(Date due) {

		Date rightNow = new Date();

		if (due.before(rightNow)) {
			return 3;
		}

		if (DateUtils.isToday(due.getTime())) {
			return 2;
		}

		long imminentDay = Calendar.DAY_OF_MONTH + 7 * 86400;
		if (due.getTime() < imminentDay) {
			return 1;
		}

		return 0;
	}
}
