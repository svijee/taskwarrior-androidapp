package org.svij.taskwarriorapp;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

public class TaskAddActivity extends FragmentActivity {
	private TaskDataSource datasource;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_task_add);
		getActionBar().setDisplayHomeAsUpEnabled(true);
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
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void onClick(View view) {
		datasource = new TaskDataSource(this);
		datasource.open();

		EditText etTaskAdd = (EditText) findViewById(R.id.etTaskAdd);
		EditText etTaskDate = (EditText) findViewById(R.id.etTaskDate);
		datasource.createTask(etTaskAdd.getText().toString(), etTaskDate.getText().toString());
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
	}

	public void showDatePickerDialog(View view) {
		DatePickerFragment date = new DatePickerFragment();
		date.setCallBack(onDate);
		date.show(getSupportFragmentManager(), "Date Picker");
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
				EditText etTaskDate = (EditText) findViewById(R.id.etTaskDate);
				etTaskDate.setText(date_string);
			} catch (ParseException e) {
				e.printStackTrace();
			}

		}

	};
}
