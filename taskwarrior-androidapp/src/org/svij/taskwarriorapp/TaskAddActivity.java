package org.svij.taskwarriorapp;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.UUID;

import org.svij.taskwarriorapp.data.Task;
import org.svij.taskwarriorapp.db.TaskDataSource;
import org.svij.taskwarriorapp.ui.DatePickerFragment;
import org.svij.taskwarriorapp.ui.TimePickerFragment;

import android.app.DatePickerDialog.OnDateSetListener;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
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

		TextView tvDueDate = (TextView) findViewById(R.id.tvDueDate);
		tvDueDate.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				DatePickerFragment date = new DatePickerFragment();
				date.setCallBack(onDate);
				date.show(getSupportFragmentManager().beginTransaction(),
						"date_dialog");
			}
		});

		TextView tvDueTime = (TextView) findViewById(R.id.tvDueTime);
		tvDueTime.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				TimePickerFragment date = new TimePickerFragment();
				date.setCallBack(onTime);
				date.show(getSupportFragmentManager().beginTransaction(),
						"time_dialog");
			}
		});

		Bundle extras = getIntent().getExtras();

		if (extras != null) {
			taskID = extras.getString("taskID");
			datasource = new TaskDataSource(this);
			datasource.open();
			Task task = datasource.getTask(UUID.fromString(taskID));
			datasource.close();

			TextView etTaskAdd = (TextView) findViewById(R.id.etTaskAdd);
			EditText etProject = (EditText) findViewById(R.id.etProject);
			Spinner spPriority = (Spinner) findViewById(R.id.spPriority);

			etTaskAdd.setText(task.getDescription());
			if (!(task.getDuedate().getTime() == 0)) {
				tvDueDate.setText(DateFormat.getDateInstance(DateFormat.SHORT)
						.format(task.getDuedate()));
				if (!DateFormat.getTimeInstance().format(task.getDuedate())
						.equals("00:00:00")) {
					tvDueTime.setText(DateFormat.getTimeInstance(
							DateFormat.SHORT).format(task.getDuedate()));
				}
				cal.setTime(task.getDuedate());
				timestamp = cal.getTimeInMillis();
			}
			etProject.setText(task.getProject());
			Log.i("PriorityID", ":" + task.getPriorityID());
			spPriority.setSelection(task.getPriorityID());
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
			NavUtils.navigateUpFromSameTask(this);
			return true;
		case R.id.task_add_done:
			datasource = new TaskDataSource(this);
			datasource.open();

			EditText etTaskAdd = (EditText) findViewById(R.id.etTaskAdd);
			EditText etProject = (EditText) findViewById(R.id.etProject);
			Spinner spPriority = (Spinner) findViewById(R.id.spPriority);

			if (etTaskAdd.getText().toString().equals("")) {
				Toast toast = Toast.makeText(
						getApplicationContext(),
						getApplicationContext().getString(
								R.string.valid_description),
						Toast.LENGTH_LONG);
				toast.show();
			} else {
				if (taskID == "") {
					datasource.createTask(etTaskAdd.getText().toString(),
							timestamp, "pending", etProject.getText()
									.toString(), spPriority.getSelectedItem()
									.toString());
				} else {
					datasource.editTask(UUID.fromString(taskID), etTaskAdd
							.getText().toString(), timestamp, "pending",
							etProject.getText().toString(), spPriority
									.getSelectedItem().toString());
				}
				this.finish();
				NavUtils.navigateUpFromSameTask(this);
			}
			return true;
		case R.id.task_add_cancel:
			this.finish();
			NavUtils.navigateUpFromSameTask(this);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onPause() {
		try {
			datasource.close();
		} catch (Exception e) {
		} finally {
			super.onPause();
		}
	}

	OnDateSetListener onDate = new OnDateSetListener() {

		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			TextView tvDueTime = (TextView) findViewById(R.id.tvDueTime);
			if (tvDueTime.getText().toString().equals("")) {
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
		}
	};
}
