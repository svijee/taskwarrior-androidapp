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
import java.util.Collections;
import java.util.Comparator;
import java.util.UUID;

import org.svij.taskwarriorapp.data.Task;
import org.svij.taskwarriorapp.db.TaskBaseAdapter;
import org.svij.taskwarriorapp.db.TaskDataSource2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockListFragment;

public class ArrayListFragment extends SherlockListFragment {

	TaskDataSource2 datasource;
	private long selectedItemId = -1;
	private String column;
	TaskBaseAdapter adapter = null;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		setListView();

		ListView listview = getListView();
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

		datasource = new TaskDataSource2(getActivity());

		if (column == null || column.equals(getString(R.string.task_next))) {
			values = datasource.getPendingTasks();
		} else if (column.equals(getString(R.string.task_long))) {
			values = datasource.getPendingTasks();
			tasksorter = new TaskSorter("long");
		} else if (column.equals(getString(R.string.no_project))) {
			values = datasource.getProjectsTasks("");
		} else if (column.equals(getString(R.string.task_all))) {
			values = datasource.getAllTasks();
		} else {
			values = datasource.getProjectsTasks(column);
		}
		Collections.sort(values, tasksorter);
		adapter = new TaskBaseAdapter(getActivity(), R.layout.task_row, values);
		setListAdapter(adapter);
	}

	public void onTaskButtonClick(View view) {
		switch (view.getId()) {
		case R.id.btnTaskDelete:
			deleteTask(getTaskWithId(selectedItemId));
			break;
		case R.id.btnTaskModify:
			showAddTaskActivity(getTaskWithId(selectedItemId));
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
			} else {
				if (task1.getDuedate().getTime() == 0) {
					return 1;
				} else {
					return task1.getDuedate().compareTo(task2.getDuedate());
				}
			}
		}
	}
}
