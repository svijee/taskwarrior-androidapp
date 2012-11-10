package org.svij.taskwarriorapp;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class MainActivity extends SherlockFragmentActivity {
	TaskDataSource datasource;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setTheme(R.style.Theme_Sherlock_Light_DarkActionBar);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (getSupportFragmentManager().findFragmentById(android.R.id.content) == null) {
			ArrayListFragment list = new ArrayListFragment();
			getSupportFragmentManager().beginTransaction()
					.add(android.R.id.content, list).commit();
		}

	}

	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.task_add:
			Intent intent = new Intent(this, TaskAddActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onResume() {
		try {
			datasource.open();
		} catch (Exception e) {
		} finally {
			super.onResume();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	public static class ArrayListFragment extends SherlockListFragment {
		TaskDataSource datasource;

		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);

			datasource = new TaskDataSource(getActivity());
			datasource.open();

			List<Task> values = datasource.getAllTasks();
			ArrayAdapter<Task> adapter = new ArrayAdapter<Task>(getActivity(),
					android.R.layout.simple_list_item_1, values);
			setListAdapter(adapter);
		}

		@Override
		public void onListItemClick(ListView l, View v, int position, long id) {
			Log.i("FragmentList", "Item clicked: " + id);
		}

		@Override
		public void onResume() {
			try {
				datasource.open();
			} catch (Exception e) {
			} finally {
				super.onResume();
			}
		}

		@Override
		public void onPause() {
			datasource.close();
			super.onPause();
		}
	}
}
