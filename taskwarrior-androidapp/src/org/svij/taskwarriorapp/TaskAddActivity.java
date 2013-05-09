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

package org.svij.taskwarriorapp;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.UUID;

import org.svij.taskwarriorapp.data.Task;
import org.svij.taskwarriorapp.db.TaskDataSource;
import org.svij.taskwarriorapp.ui.DatePickerFragment;
import org.svij.taskwarriorapp.ui.TimePickerFragment;

import android.app.AlertDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.NavUtils;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;

import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class TaskAddActivity extends SherlockFragmentActivity {
	private TaskDataSource datasource;
	private String taskID = "";
	private long timestamp;
	private GregorianCalendar cal = new GregorianCalendar();

	public void onCreate(Bundle savedInstanceState) {
		setTheme(R.style.Theme_Sherlock_Light_DarkActionBar);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_task_add);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		final TextView tvDueDate = (TextView) findViewById(R.id.tvDueDate);
		tvDueDate.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				DatePickerFragment date = new DatePickerFragment();
				date.setCallBack(onDate);
				date.setTimestamp(timestamp);
				date.show(getSupportFragmentManager().beginTransaction(),
						"date_dialog");
			}
		});

		final TextView tvDueTime = (TextView) findViewById(R.id.tvDueTime);
		tvDueTime.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				TimePickerFragment date = new TimePickerFragment();
				date.setCallBack(onTime);
				date.setTimestamp(timestamp);
				date.show(getSupportFragmentManager().beginTransaction(),
						"time_dialog");
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

		TaskDataSource dataSource = new TaskDataSource(this);
		ArrayList<String> projectsAR = dataSource.getProjects();
		projectsAR.removeAll(Collections.singleton(null));
		String[] projects = projectsAR.toArray(new String[projectsAR.size()]);
		final AutoCompleteTextView actvProject = (AutoCompleteTextView) findViewById(R.id.actvProject);

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, projects);
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
				datasource = new TaskDataSource(this);
				Task task = datasource.getTask(UUID.fromString(taskID));

				TextView etTaskAdd = (TextView) findViewById(R.id.etTaskAdd);
				Spinner spPriority = (Spinner) findViewById(R.id.spPriority);
				TextView etTags = (TextView) findViewById(R.id.etTags);

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
				etTags.setText(task.getTags());
			} else {
				String action = intent.getAction();
				if (action.equalsIgnoreCase(Intent.ACTION_SEND)
						&& intent.hasExtra(Intent.EXTRA_TEXT)) {
					String s = intent.getStringExtra(Intent.EXTRA_TEXT);
					TextView etTaskAdd = (TextView) findViewById(R.id.etTaskAdd);
					etTaskAdd.setText(s);
				}
			}
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.activity_task_add, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			UnsavedDataDialogFragment alertDialog = new UnsavedDataDialogFragment();
			alertDialog.show(getSupportFragmentManager(), "dialog");
			return true;
		case R.id.task_add_done:
			datasource = new TaskDataSource(this);

			EditText etTaskAdd = (EditText) findViewById(R.id.etTaskAdd);
			AutoCompleteTextView actvProject = (AutoCompleteTextView) findViewById(R.id.actvProject);
			Spinner spPriority = (Spinner) findViewById(R.id.spPriority);
			EditText etTags = (EditText) findViewById(R.id.etTags);

			if (etTaskAdd.getText().toString().equals("")) {
				Toast toast = Toast.makeText(
						getApplicationContext(),
						getApplicationContext().getString(
								R.string.valid_description), Toast.LENGTH_LONG);
				toast.show();
			} else {
				if (taskID == "") {
					datasource.createTask(etTaskAdd.getText().toString(),
							timestamp, "pending", actvProject.getText()
									.toString(), getPriority(spPriority
									.getSelectedItem().toString()), etTags
									.getText().toString());
				} else {
					datasource
							.editTask(UUID.fromString(taskID), etTaskAdd
									.getText().toString(), timestamp,
									"pending",
									actvProject.getText().toString(),
									getPriority(spPriority.getSelectedItem()
											.toString()), etTags.getText()
											.toString());
				}
				this.finish();
				NavUtils.navigateUpFromSameTask(this);
			}
			return true;
		case R.id.task_add_cancel:
			UnsavedDataDialogFragment alertDialog2 = new UnsavedDataDialogFragment();
			alertDialog2.show(getSupportFragmentManager(), "dialog");
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
		UnsavedDataDialogFragment alertDialog = new UnsavedDataDialogFragment();
		alertDialog.show(getSupportFragmentManager(), "dialog");
	}

	OnDateSetListener onDate = new OnDateSetListener() {

		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			TextView tvDueTime = (TextView) findViewById(R.id.tvDueTime);
			if (TextUtils.isEmpty(tvDueTime.getText().toString())) {
				cal = new GregorianCalendar(year, monthOfYear, dayOfMonth);
			} else {
				cal.set(year, monthOfYear, dayOfMonth);
			}
			timestamp = cal.getTimeInMillis();

			TextView etTaskDate = (TextView) findViewById(R.id.tvDueDate);
			etTaskDate.setText(DateFormat.getDateInstance(DateFormat.SHORT)
					.format(timestamp));

		}
	};

	OnTimeSetListener onTime = new OnTimeSetListener() {

		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
			cal.set(Calendar.MINUTE, minute);
			cal.set(Calendar.SECOND, 0);

			timestamp = cal.getTimeInMillis();

			TextView etTaskTime = (TextView) findViewById(R.id.tvDueTime);
			etTaskTime.setText(DateFormat.getTimeInstance(DateFormat.SHORT)
					.format(timestamp));
			TextView etTaskDate = (TextView) findViewById(R.id.tvDueDate);
			etTaskDate.setText(DateFormat.getDateInstance(DateFormat.SHORT)
					.format(timestamp));
		}
	};

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

}
