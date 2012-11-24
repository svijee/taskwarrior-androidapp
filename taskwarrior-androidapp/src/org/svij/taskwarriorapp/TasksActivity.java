package org.svij.taskwarriorapp;

import java.util.List;

import org.svij.taskwarriorapp.R;
import org.svij.taskwarriorapp.data.Task;
import org.svij.taskwarriorapp.db.TaskDataSource;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.ActionMode.Callback;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class TasksActivity extends SherlockFragmentActivity {
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
		ArrayAdapter<Task> adapter = null;
		private boolean inEditMode = false;
		private ActionMode actionMode = null;
		private int selectedViewPosition = -1;
		private long selectedItemId = -1;

		private ActionMode.Callback actionModeCallbacks = new Callback() {

			@Override
			public boolean onCreateActionMode(ActionMode mode, Menu menu) {
				MenuInflater inflater = mode.getMenuInflater();
				inflater.inflate(R.menu.context_menu, menu);
				mode.setTitle(getString(R.string.task_selected, 1));
				return true;
			}

			@Override
			public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
				// nothing to see here, move along
				return false;
			}

			@Override
			public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
				switch (item.getItemId()) {
				case R.id.context_menu_edit_task:
					showAddTaskActivity(getTaskWithId(selectedItemId));
					return true;

				case R.id.context_menu_delete_task:
					deleteTask(getTaskWithId(selectedItemId));
					mode.finish();
					return true;

				default:
					return false;
				}
			}

			@Override
			public void onDestroyActionMode(ActionMode mode) {
				finishEditMode();
			}

			private void showAddTaskActivity(long selectedItemId) {
				Intent intent = new Intent(getActivity(), TaskAddActivity.class);
				intent.putExtra("taskID", selectedItemId);
				startActivity(intent);
			}

			private void deleteTask(long selectedItemId) {
				datasource.deleteTask(selectedItemId);
				refreshListView();
			}

			private long getTaskWithId(long selectedItemId) {
				return ((Task) getListAdapter().getItem(
						(int) selectedItemId - 1)).getId();
			}
		};

		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);

			datasource = new TaskDataSource(getActivity());
			datasource.open();

			refreshListView();
		}

		public void refreshListView() {
			List<Task> values = datasource.getAllTasks();
			adapter = new ArrayAdapter<Task>(getActivity(),
					android.R.layout.simple_list_item_1, values);
			setListAdapter(adapter);
		}

		@Override
		public void onListItemClick(ListView l, View v, int position, long id) {
			Log.i("FragmentList", "Item clicked: " + id);

			inEditMode = true;
			selectedItemId = id + 1;
			// Start the CAB using the ActionMode.Callback defined above
			actionMode = getSherlockActivity().startActionMode(
					actionModeCallbacks);

			selectItem(position);
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

		public void finishEditMode() {
			inEditMode = false;
			deselectPreviousSelectedItem();
			actionMode = null;
			selectedItemId = -1;
		}

		private void selectItem(int position) {
			deselectPreviousSelectedItem();
			ListView lv = getListView();
			lv.setItemChecked(position, true);
			View v = lv.getChildAt(position - lv.getFirstVisiblePosition());
			v.setSelected(true);
			v.setBackgroundColor(getResources().getColor(
					R.color.abs__holo_blue_light));
			selectedViewPosition = position;
		}

		private void deselectPreviousSelectedItem() {
			if (selectedViewPosition >= 0) {
				ListView lv = getListView();
				lv.setItemChecked(selectedViewPosition, false);
				View v = getListView().getChildAt(
						selectedViewPosition - lv.getFirstVisiblePosition());
				if (v == null) {
					// if we just deleted a row, then the previous position is
					// invalid
					return;
				}
				v.setBackgroundColor(getResources().getColor(
						android.R.color.transparent));
				v.setSelected(false);
			}
		}

	}
}
