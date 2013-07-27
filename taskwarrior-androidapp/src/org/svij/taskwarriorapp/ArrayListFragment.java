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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.UUID;

import org.svij.taskwarriorapp.TaskAddActivity.UnsavedDataDialogFragment;
import org.svij.taskwarriorapp.data.Task;
import org.svij.taskwarriorapp.db.TaskBaseAdapter;
import org.svij.taskwarriorapp.db.TaskDataSource;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.NavUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockListFragment;

public class ArrayListFragment extends SherlockListFragment {

	TaskDataSource datasource;
	private long selectedItemId = -1;
	private String column;
	TaskBaseAdapter adapter = null;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setListView();

		ListView listview = getListView();
		listview.setDividerHeight(0);
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				selectedItemId = id + 1;
				adapter.changeTaskRow(position);
			}
		});
	}

	public void setListView() {
		ArrayList<Task> values;
		TaskSorter tasksorter = new TaskSorter("urgency");

		datasource = new TaskDataSource(getActivity());

		if (column == null || column.equals(getString(R.string.task_next))) {
			values = datasource.getPendingTasks();
		} else if (column.equals(getString(R.string.task_long))) {
			values = datasource.getPendingTasks();
			tasksorter = new TaskSorter("long");
		} else if (column.equals(getString(R.string.no_project))) {
			values = datasource.getProjectsTasks("");
		} else if (column.equals(getString(R.string.task_all))) {
			values = datasource.getAllTasks();
		} else if (column.equals(getString(R.string.task_wait))) {
			values = datasource.getWaitingTasks();
		} else if (column.equals(getString(R.string.task_oldest))) {
			values = datasource.getPendingTasks();
			tasksorter = new TaskSorter("oldest");
		} else if (column.equals(getString(R.string.task_newest))) {
			values = datasource.getPendingTasks();
			tasksorter = new TaskSorter("newest");
		} else {
			values = datasource.getProjectsTasks(column);
		}
		Collections.sort(values, tasksorter);
		adapter = new TaskBaseAdapter(getActivity(), R.layout.task_row, values,
				getActivity());
		setListAdapter(adapter);
	}

	public void onTaskButtonClick(View view) {
		switch (view.getId()) {
		case R.id.btnTaskDelete:
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setMessage(R.string.dialog_delete_task)
					.setPositiveButton(R.string.ok,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									deleteTask(getTaskWithId(selectedItemId));
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

			builder.create();
			builder.show();
			break;
		case R.id.btnTaskModify:
			showAddTaskActivity(getTaskWithId(selectedItemId));
			break;
		case R.id.btnTaskAddReminder:
			Task task = datasource.getTask(getTaskWithId(selectedItemId));
			Intent intent = new Intent(Intent.ACTION_EDIT);
			intent.setType("vnd.android.cursor.item/event");
			if (task.getDue() != null) {
				intent.putExtra("beginTime", task.getDue().getTime());
				intent.putExtra("endTime", task.getDue().getTime()
						+ (30 * 60 * 1000));
			} else {
				Calendar cal = new GregorianCalendar();
				intent.putExtra("beginTime", cal.getTime().getTime());
				cal.add(Calendar.MINUTE, 30);
				intent.putExtra("endTime", cal.getTime().getTime());
			}
			intent.putExtra("title", task.getDescription());
			startActivity(intent);
			break;
		case R.id.btnTaskDone:
			doneTask(getTaskWithId(selectedItemId));
			break;
		default:
			break;
		}
	}

	private void showAddTaskActivity(UUID uuid) {
		Intent intent = new Intent(getActivity(), TaskAddActivity.class);
		intent.putExtra("taskID", uuid.toString());
		startActivity(intent);
	}

	private void deleteTask(UUID uuid) {
		datasource.deleteTask(uuid);
		setListView();
		Toast.makeText(
				getActivity(),
				getString(R.string.task_action_delete) + " '"
						+ datasource.getTask(uuid).getDescription() + "'",
				Toast.LENGTH_SHORT).show();
	}

	private void doneTask(UUID uuid) {
		datasource.doneTask(uuid);
		setListView();
		Toast.makeText(
				getActivity(),
				getString(R.string.task_action_done) + " '"
						+ datasource.getTask(uuid).getDescription() + "'",
				Toast.LENGTH_SHORT).show();
	}

	private UUID getTaskWithId(long selectedItemId) {
		return ((Task) getListAdapter().getItem((int) selectedItemId - 1))
				.getUuid();
	}

	@Override
	public void onResume() {
		super.onResume();
		setListView();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	public String getColumn() {
		return column;
	}

	public void setColumn(String column) {
		this.column = column;
	}

	class TaskSorter implements Comparator<Task> {
		private String sortType;

		public TaskSorter(String sortType) {
			this.sortType = sortType;
		}

		@Override
		public int compare(Task task1, Task task2) {
			if (sortType.equals("urgency")) {
				return Float.compare(task2.getUrgency(), task1.getUrgency());
			} else if (sortType.equals("long")) {
				if (task1.getDue() == null && task2.getDue() == null) {
					return 0;
				} else if (task1.getDue() == null) {
					return 1;
				} else if (task2.getDue() == null) {
					return -1;
				}
				return task1.getDue().compareTo(task2.getDue());
			} else if (sortType.equals("oldest")) {
				Date task1date = new Date(task1.getEntry());
				Date task2date = new Date(task2.getEntry());
				if (task1date.before(task2date)) {
					return -1;
				} else if (task1date.after(task2date)) {
					return 1;
				} else {
					return 0;
				}
			} else if (sortType.equals("newest")) {
				Date task1date = new Date(task1.getEntry());
				Date task2date = new Date(task2.getEntry());
				if (task2date.before(task1date)) {
					return -1;
				} else if (task2date.after(task1date)) {
					return 1;
				} else {
					return 0;
				}
			} else {
				return task1.getDue().compareTo(task2.getDue());
			}
		}
	}
}
