/**
 * taskwarrior for android – a task list manager
 *
 * Copyright (c) 2012-2014 Sujeevan Vijayakumaran
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

package org.svij.taskwarriorapp.activities;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.UUID;

import org.svij.taskwarriorapp.R;
import org.svij.taskwarriorapp.data.Task;
import org.svij.taskwarriorapp.db.TaskDatabase;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.text.TextUtils;
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.doomonafireball.betterpickers.calendardatepicker.CalendarDatePickerDialog;
import com.doomonafireball.betterpickers.radialtimepicker.RadialPickerLayout;
import com.doomonafireball.betterpickers.radialtimepicker.RadialTimePickerDialog;
import com.doomonafireball.betterpickers.recurrencepicker.EventRecurrence;
import com.doomonafireball.betterpickers.recurrencepicker.EventRecurrenceFormatter;
import com.doomonafireball.betterpickers.recurrencepicker.RecurrencePickerDialog;

public class TaskAddActivity extends FragmentActivity implements
		CalendarDatePickerDialog.OnDateSetListener,
		RadialTimePickerDialog.OnTimeSetListener,
		RecurrencePickerDialog.OnRecurrenceSetListener {
	private TaskDatabase data;
	private String taskID = "";
	private long timestamp;
	private GregorianCalendar cal = new GregorianCalendar();
	private boolean addingTaskFromOtherApp = false;
	private static final String FRAG_TAG_RECUR_PICKER = "recurrencePickerDialogFragment";
	private String recurRule;
	private EventRecurrence eventRecurrence = new EventRecurrence();

	public void onCreate(Bundle savedInstanceState) {
		setTheme(android.R.style.Theme_Holo_Light_DarkActionBar);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_task_add);

		final TextView tvDueDate = (TextView) findViewById(R.id.tvDueDate);
		tvDueDate.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				FragmentManager fm = getSupportFragmentManager();
				CalendarDatePickerDialog calendarDatePickerDialog = CalendarDatePickerDialog
						.newInstance(TaskAddActivity.this, Calendar
								.getInstance().get(Calendar.YEAR), Calendar
								.getInstance().get(Calendar.MONTH), Calendar
								.getInstance().get(Calendar.DAY_OF_MONTH));
				calendarDatePickerDialog.show(fm, "fragment_date_picker");
			}
		});

		final TextView tvDueTime = (TextView) findViewById(R.id.tvDueTime);
		tvDueTime.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				FragmentManager fm = getSupportFragmentManager();
				RadialTimePickerDialog timePickerDialog = RadialTimePickerDialog
						.newInstance(TaskAddActivity.this, Calendar
								.getInstance().get(Calendar.HOUR_OF_DAY),
								Calendar.getInstance().get(Calendar.MINUTE),
								android.text.format.DateFormat
										.is24HourFormat(TaskAddActivity.this));
				timePickerDialog.show(fm, "fragment_time_picker_name");
			}
		});

		tvDueDate.setOnLongClickListener(new View.OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				if (TextUtils.isEmpty(tvDueTime.getText().toString())) {
					timestamp = 0;
				} else {
					cal.set(Calendar.YEAR,
							Calendar.getInstance().get(Calendar.YEAR));
					cal.set(Calendar.MONTH,
							Calendar.getInstance().get(Calendar.MONTH));
					cal.set(Calendar.DAY_OF_MONTH,
							Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
					timestamp = cal.getTimeInMillis();
				}
				tvDueDate.setText("");
				return true;
			}
		});

		tvDueTime.setOnLongClickListener(new View.OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				if (TextUtils.isEmpty(tvDueDate.getText().toString())) {
					timestamp = 0;
				} else {
					cal.set(Calendar.HOUR_OF_DAY, 0);
					cal.set(Calendar.MINUTE, 0);
					cal.set(Calendar.SECOND, 0);
					timestamp = cal.getTimeInMillis();
				}
				tvDueTime.setText("");
				return true;
			}
		});

		final TextView tvRecurring = (TextView) findViewById(R.id.tvRecurring);

		tvRecurring.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				FragmentManager fm = getSupportFragmentManager();
				Bundle b = new Bundle();
				Time t = new Time();
				t.setToNow();
				b.putLong(RecurrencePickerDialog.BUNDLE_START_TIME_MILLIS,
						t.toMillis(false));
				b.putString(RecurrencePickerDialog.BUNDLE_TIME_ZONE, t.timezone);

				// may be more efficient to serialize and pass in
				// EventRecurrence
				b.putString(RecurrencePickerDialog.BUNDLE_RRULE, recurRule);

				RecurrencePickerDialog rpd = (RecurrencePickerDialog) fm
						.findFragmentByTag(FRAG_TAG_RECUR_PICKER);
				if (rpd != null) {
					rpd.dismiss();
				}
				rpd = new RecurrencePickerDialog();
				rpd.setArguments(b);
				rpd.setOnRecurrenceSetListener(TaskAddActivity.this);
				rpd.show(fm, FRAG_TAG_RECUR_PICKER);
			}
		});

		TaskDatabase dataSource = new TaskDatabase(this);
		ArrayList<String> projects = dataSource.getProjects();
		projects.removeAll(Collections.singleton(null));
		final AutoCompleteTextView actvProject = (AutoCompleteTextView) findViewById(R.id.actvProject);

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1,
				projects.toArray(new String[projects.size()]));
		actvProject.setAdapter(adapter);
		actvProject.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				actvProject.showDropDown();
				return false;
			}
		});

		Intent intent = getIntent();
		Bundle extras = intent.getExtras();

		if (extras != null) {
			taskID = extras.getString("taskID");

			if (taskID != null) {
				data = new TaskDatabase(this);
				Task task = data.getTask(UUID.fromString(taskID));

				TextView etTaskAdd = (TextView) findViewById(R.id.etTaskAdd);
				Spinner spPriority = (Spinner) findViewById(R.id.spPriority);

				etTaskAdd.setText(task.getDescription());
				if (task.getDue() != null && task.getDue().getTime() != 0) {

					tvDueDate.setText(DateFormat.getDateInstance(
							DateFormat.SHORT).format(task.getDue()));
					if (!DateFormat.getTimeInstance().format(task.getDue())
							.equals("00:00:00")) {
						tvDueTime.setText(DateFormat.getTimeInstance(
								DateFormat.SHORT).format(task.getDue()));
					}

					cal.setTime(task.getDue());
					timestamp = cal.getTimeInMillis();

				}
				actvProject.setText(task.getProject());

				Log.i("PriorityID", ":" + task.getPriorityID());
				spPriority.setSelection(task.getPriorityID());

				if (task.getTags() != null) {
					TextView etTags = (TextView) findViewById(R.id.etTags);
					String tagString = "";

					for (String s : task.getTags()) {
						tagString += s + " ";
					}
					etTags.setText(tagString.trim());
				}
			} else {
				String action = intent.getAction();
				if ((action.equalsIgnoreCase(Intent.ACTION_SEND) || action
						.equalsIgnoreCase("com.google.android.gm.action.AUTO_SEND"))
						&& intent.hasExtra(Intent.EXTRA_TEXT)) {
					String s = intent.getStringExtra(Intent.EXTRA_TEXT);
					TextView etTaskAdd = (TextView) findViewById(R.id.etTaskAdd);
					etTaskAdd.setText(s);
					addingTaskFromOtherApp = true;
				}
			}
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_task_add, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			safelyDismissActivity();
			return true;
		case R.id.task_add_done:
			data = new TaskDatabase(this);

			EditText etTaskAdd = (EditText) findViewById(R.id.etTaskAdd);
			AutoCompleteTextView actvProject = (AutoCompleteTextView) findViewById(R.id.actvProject);
			Spinner spPriority = (Spinner) findViewById(R.id.spPriority);
			EditText etTags = (EditText) findViewById(R.id.etTags);

			if (TextUtils.isEmpty(etTaskAdd.getText().toString())) {
				Toast toast = Toast.makeText(
						getApplicationContext(),
						getApplicationContext().getString(
								R.string.valid_description), Toast.LENGTH_LONG);
				toast.show();
			} else {
				if (taskID == null || TextUtils.isEmpty(taskID)) {
					ArrayList<String> tags = new ArrayList<String>(
							Arrays.asList(etTags.getText().toString()
									.split(" ")));

					data.createTask(
							etTaskAdd.getText().toString(),
							timestamp,
							"pending",
							actvProject.getText().toString().trim(),
							getPriority(spPriority.getSelectedItem().toString()),
							tags);
					if (addingTaskFromOtherApp) {
						Toast addedToast = Toast.makeText(this, getResources()
								.getString(R.string.task_added),
								Toast.LENGTH_LONG);
						addedToast.show();
					}
				} else {
					ArrayList<String> tags = new ArrayList<String>(
							Arrays.asList(etTags.getText().toString()
									.split(" ")));

					data.editTask(
							UUID.fromString(taskID),
							etTaskAdd.getText().toString(),
							timestamp,
							"pending",
							actvProject.getText().toString().trim(),
							getPriority(spPriority.getSelectedItem().toString()),
							tags);
				}
				this.finish();
				NavUtils.navigateUpFromSameTask(this);
			}
			return true;
		case R.id.task_add_cancel:
			safelyDismissActivity();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public String getPriority(String priority) {
		Resources res = getResources();
		String[] priorities = res.getStringArray(R.array.priority_list);
		if (priority.equals(priorities[0])) {
			return "";
		} else if (priority.equals(priorities[1])) {
			return "H";
		} else if (priority.equals(priorities[2])) {
			return "M";
		} else if (priority.equals(priorities[3])) {
			return "L";
		} else {
			return "";
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	public void onBackPressed() {
		safelyDismissActivity();
	}

	public static class UnsavedDataDialogFragment extends DialogFragment {
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {

			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setMessage(R.string.dialog_unsaved_data)
					.setPositiveButton(R.string.ok,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									getActivity().finish();
									NavUtils.navigateUpFromSameTask(getActivity());
								}
							})
					.setNegativeButton(R.string.cancel,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									// User cancelled the dialog
									// Dialog is closing
								}
							});

			return builder.create();
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);

		savedInstanceState.putLong("timestamp", timestamp);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);

		if (savedInstanceState != null) {
			timestamp = savedInstanceState.getLong("timestamp");

			if (timestamp != 0) {
				cal.setTimeInMillis(timestamp);

				TextView tvDueTime = (TextView) findViewById(R.id.tvDueTime);
				tvDueTime.setText(DateFormat.getTimeInstance(DateFormat.SHORT)
						.format(timestamp));
				TextView tvDueDate = (TextView) findViewById(R.id.tvDueDate);
				tvDueDate.setText(DateFormat.getDateInstance(DateFormat.SHORT)
						.format(timestamp));
			}
		}
	}

	private void safelyDismissActivity() {
		if (haveUnsavedData()) {
			UnsavedDataDialogFragment alertDialog = new UnsavedDataDialogFragment();
			alertDialog.show(getSupportFragmentManager(), "dialog");
		} else {
			this.finish();
			NavUtils.navigateUpFromSameTask(this);
		}
	}

	private boolean haveUnsavedData() {
		TextView etTaskAdd = (TextView) findViewById(R.id.etTaskAdd);
		TextView etTaskDate = (TextView) findViewById(R.id.tvDueDate);
		TextView etTaskTime = (TextView) findViewById(R.id.tvDueTime);
		TextView actvProject = (TextView) findViewById(R.id.actvProject);
		Spinner spPriority = (Spinner) findViewById(R.id.spPriority);
		TextView etTags = (TextView) findViewById(R.id.etTags);
		return etTaskAdd.getText().length() != 0
				|| etTaskDate.getText().length() != 0
				|| etTaskTime.getText().length() != 0
				|| actvProject.getText().length() != 0
				|| !TextUtils.isEmpty(getPriority(spPriority.getSelectedItem()
						.toString())) || etTags.getText().length() != 0;
	}

	@Override
	public void onDateSet(CalendarDatePickerDialog dialog, int year,
			int monthOfYear, int dayOfMonth) {
		TextView tvDueTime = (TextView) findViewById(R.id.tvDueTime);
		if (TextUtils.isEmpty(tvDueTime.getText().toString())) {
			cal = new GregorianCalendar(year, monthOfYear, dayOfMonth);
		} else {
			cal.set(year, monthOfYear, dayOfMonth);
		}
		timestamp = cal.getTimeInMillis();

		TextView etTaskDate = (TextView) findViewById(R.id.tvDueDate);
		etTaskDate.setText(DateFormat.getDateInstance(DateFormat.SHORT).format(
				timestamp));
	}

	@Override
	public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
		cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
		cal.set(Calendar.MINUTE, minute);
		cal.set(Calendar.SECOND, 0);

		timestamp = cal.getTimeInMillis();

		TextView etTaskTime = (TextView) findViewById(R.id.tvDueTime);
		etTaskTime.setText(DateFormat.getTimeInstance(DateFormat.SHORT).format(
				timestamp));
		TextView etTaskDate = (TextView) findViewById(R.id.tvDueDate);
		etTaskDate.setText(DateFormat.getDateInstance(DateFormat.SHORT).format(
				timestamp));
	}

	@Override
	public void onRecurrenceSet(String recurRule) {
        this.recurRule = recurRule;
        if (this.recurRule != null) {
            eventRecurrence.parse(this.recurRule);
        }
        populateRepeats();
	}

    private void populateRepeats() {
        Resources r = getResources();
        String repeatString = "";
        if (!TextUtils.isEmpty(recurRule)) {
            repeatString = EventRecurrenceFormatter.getRepeatString(this, r, eventRecurrence, true);
        }

        Toast.makeText(this, recurRule + "\n" + repeatString, Toast.LENGTH_LONG).show();
    }
}
