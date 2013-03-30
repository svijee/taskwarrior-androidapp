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

import org.svij.taskwarriorapp.data.Task;
import org.svij.taskwarriorapp.db.TaskBaseAdapter;
import org.svij.taskwarriorapp.db.TaskDataSource;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockListFragment;


public class ArrayListFragment extends SherlockListFragment {

	TaskDataSource datasource;
	@SuppressWarnings("unused")
	private boolean inEditMode = false;
	private int selectedViewPosition = -1;
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

				selectItem(position);

				adapter.changeTaskRow(position);
			}
		});
	}

	public void setListView() {
		ArrayList<Task> values;

		if (column == null || column.equals(getString(R.string.task_next)) || column.equals(getString(R.string.task_long))) {
			values = datasource.getPendingTasks();
		} else if (column == "no project") {
			values = datasource.getProjectsTasks("");
		} else {
			values = datasource.getProjectsTasks(column);
		}
		adapter = new TaskBaseAdapter(getActivity(), R.layout.task_row, values);
		setListAdapter(adapter);
	}

	@Override
	public void onResume() {
		super.onResume();
		if (datasource != null) {
			datasource.open();
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		if (datasource != null) {
			datasource.close();
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (datasource == null) {
			datasource = new TaskDataSource(getActivity());
			datasource.open();
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (datasource != null) {
			datasource.close();
			datasource = null;
		}
	}

	public void finishEditMode() {
		inEditMode = false;
		deselectPreviousSelectedItem();
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

	public String getColumn() {
		return column;
	}

	public void setColumn(String column) {
		this.column = column;
	}
}
