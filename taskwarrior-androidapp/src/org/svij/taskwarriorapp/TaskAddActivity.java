package org.svij.taskwarriorapp;

import java.text.DateFormat;
import java.util.GregorianCalendar;

import org.svij.taskwarriorapp.data.Task;
import org.svij.taskwarriorapp.db.TaskDataSource;
import org.svij.taskwarriorapp.ui.DatePickerFragment;

import android.app.DatePickerDialog.OnDateSetListener;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class TaskAddActivity extends SherlockFragmentActivity {
	private TaskDataSource datasource;
	private long taskID = 0;
	private long timestamp;

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

		Bundle extras = getIntent().getExtras();

		if (extras != null) {
			taskID = extras.getLong("taskID");
			datasource = new TaskDataSource(this);
			datasource.open();
			Task task = datasource.getTask(taskID);
			datasource.close();

			TextView etTaskAdd = (TextView) findViewById(R.id.etTaskAdd);
			EditText etProject = (EditText) findViewById(R.id.etProject);
			Spinner spPriority = (Spinner) findViewById(R.id.spPriority);

			etTaskAdd.setText(task.getDescription());
			if (!(task.getDuedate().getTime() == 0)) {
				tvDueDate.setText(DateFormat.getDateInstance(DateFormat.SHORT)
						.format(task.getDuedate()));
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

			if (taskID == 0) {
				datasource.createTask(etTaskAdd.getText().toString(),
						timestamp, "pending", etProject.getText().toString(),
						spPriority.getSelectedItem().toString());
			} else {
				datasource.editTask(taskID, etTaskAdd.getText().toString(),
						timestamp, "pending", etProject.getText().toString(),
						spPriority.getSelectedItem().toString());
			}

			this.finish();
			NavUtils.navigateUpFromSameTask(this);
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
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			GregorianCalendar cal = new GregorianCalendar(year, monthOfYear,
					dayOfMonth);
			timestamp = cal.getTimeInMillis();

			TextView etTaskDate = (TextView) findViewById(R.id.tvDueDate);
			etTaskDate.setText(DateFormat.getDateInstance(DateFormat.SHORT)
					.format(timestamp));

		}
	};
}
