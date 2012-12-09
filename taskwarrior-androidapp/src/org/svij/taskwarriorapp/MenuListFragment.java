package org.svij.taskwarriorapp;

import java.util.ArrayList;

import org.svij.taskwarriorapp.data.Task;
import org.svij.taskwarriorapp.db.TaskArrayAdapter;
import org.svij.taskwarriorapp.db.TaskDataSource;

import android.content.Context;
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
	}

	public void setMenuList() {
		MenuListAdapter adapter = new MenuListAdapter(getActivity());

		adapter.add(new MenuItem("task next"));
		adapter.add(new MenuItem("task long"));
		adapter.add(new MenuItem("task all"));

		TaskDataSource datasource = new TaskDataSource(getActivity());

		datasource.open();
		ArrayList<Task> values = datasource.getProjects();
		datasource.close();

		for (Task task : values) {
			if (task.getProject().trim().length() == 0) {
				adapter.add(new MenuItem("no project"));
			} else {
				adapter.add(new MenuItem(task.getProject()));
			}
		}

		setListAdapter(adapter);
	}

	public void setTaskList() {
		ArrayList<Task> values;
		datasource = new TaskDataSource(getActivity());
		datasource.open();

		if (column == null || column == "task next" || column == "task long") {
			values = datasource.getPendingTasks();
		} else if (column == "no project") {
			values = datasource.getProjectsTasks("");
		} else if (column == "task all") {
			values = datasource.getAllTasks();
		} else {
			values = datasource.getProjectsTasks(column);
		}
		adapter = new TaskArrayAdapter(getActivity(), R.layout.task_row, values);
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
