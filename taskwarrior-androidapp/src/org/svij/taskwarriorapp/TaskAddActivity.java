package org.svij.taskwarriorapp;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.svij.taskwarriorapp.data.Task;
import org.svij.taskwarriorapp.db.TaskDataSource;
import org.svij.taskwarriorapp.ui.DatePickerFragment;

import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Intent;
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
			tvDueDate.setText(task.getDuedate());
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
			TextView etTaskDate = (TextView) findViewById(R.id.tvDueDate);
			EditText etProject = (EditText) findViewById(R.id.etProject);
			Spinner spPriority = (Spinner) findViewById(R.id.spPriority);

			if (taskID == 0) {
				datasource.createTask(etTaskAdd.getText().toString(),
						etTaskDate.getText().toString(), "pending", etProject
								.getText().toString(), spPriority
								.getSelectedItem().toString());
			} else {
				datasource.editTask(taskID, etTaskAdd.getText().toString(),
						etTaskDate.getText().toString(), "pending", etProject
								.getText().toString(), spPriority
								.getSelectedItem().toString());
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
			String date_rawstring = String.valueOf(year) + "-"
					+ String.valueOf(monthOfYear + 1) + "-"
					+ String.valueOf(dayOfMonth);

			try {
				DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
				Date date = (Date) formatter.parse(date_rawstring);
				String date_string = formatter.format(date);
				TextView etTaskDate = (TextView) findViewById(R.id.tvDueDate);
				etTaskDate.setText(date_string);
			} catch (ParseException e) {
				e.printStackTrace();
			}

		}

	};
}
