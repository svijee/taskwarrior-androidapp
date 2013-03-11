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
import java.util.Comparator;

import org.svij.taskwarriorapp.data.Task;
import org.svij.taskwarriorapp.db.TaskArrayAdapter;
import org.svij.taskwarriorapp.db.TaskDataSource;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockListFragment;
import com.slidingmenu.lib.SlidingMenu;

public class MenuListFragment extends SherlockListFragment {
	ArrayListFragment listFragment;
	TaskDataSource datasource;
	String column;
	ArrayAdapter<Task> adapter = null;
	SlidingMenu menu;

	public MenuListFragment() {
		super();
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.sidebar, null);
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setMenuList();
	}

	private class MenuItem {
		public String tag;

		public MenuItem(String tag) {
			this.tag = tag;
		}
	}

	public class MenuListAdapter extends ArrayAdapter<MenuItem> {

		public MenuListAdapter(Context context) {
			super(context, 0);
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(getContext()).inflate(
						R.layout.menu_row, null);
			}

			TextView title = (TextView) convertView
					.findViewById(R.id.menu_row_title);
			title.setText(getItem(position).tag);

			return convertView;
		}
	}

	@Override
	public void onListItemClick(ListView lv, View view, int position, long id) {
		column = (((TextView) view).getText().toString());
		setTaskList();
		menu.toggle();
		ArrayListFragment listFragment = (ArrayListFragment) getActivity()
				.getSupportFragmentManager().findFragmentById(
						android.R.id.content);
		if (listFragment.getActionMode() != null) {
			listFragment.getActionMode().finish();
		}
	}

	public void setMenuList() {
		MenuListAdapter adapter = new MenuListAdapter(getActivity());

		adapter.add(new MenuItem(getString(R.string.task_next)));
		adapter.add(new MenuItem(getString(R.string.task_long)));
		adapter.add(new MenuItem(getString(R.string.task_all)));

		TaskDataSource datasource = new TaskDataSource(getActivity());

		datasource.open();
		ArrayList<Task> values = datasource.getProjects();
		datasource.close();

		for (Task task : values) {
			if (task.getProject().trim().length() == 0) {
				adapter.add(new MenuItem(getString(R.string.no_project)));
			} else {
				adapter.add(new MenuItem(task.getProject()));
			}
		}

		setListAdapter(adapter);
	}

	public void setTaskList() {
		ArrayList<Task> values;
		TaskSorter tasksorter = new TaskSorter("urgency");

		datasource = new TaskDataSource(getActivity());
		datasource.open();

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
		adapter = new TaskArrayAdapter(getActivity(), R.layout.task_row, values);
		adapter.sort(tasksorter);
		adapter.notifyDataSetChanged();
		ArrayListFragment listFragment = (ArrayListFragment) getActivity()
				.getSupportFragmentManager().findFragmentById(
						android.R.id.content);
		listFragment.setListAdapter(adapter);
		datasource.close();
	}

	public String getColumn() {
		return column;
	}

	public void setColumn(String column) {
		this.column = column;
	}

	public SlidingMenu getMenu() {
		return menu;
	}

	public void setMenu(SlidingMenu menu) {
		this.menu = menu;
	}
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
