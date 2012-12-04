package org.svij.taskwarriorapp;

import java.util.ArrayList;
import java.util.List;

import net.simonvt.widget.MenuDrawer;
import net.simonvt.widget.MenuDrawerManager;

import org.svij.taskwarriorapp.data.Task;
import org.svij.taskwarriorapp.db.TaskArrayAdapter;
import org.svij.taskwarriorapp.db.TaskDataSource;
import org.svij.taskwarriorapp.ui.MenuListView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.ActionMode.Callback;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class TasksActivity extends SherlockFragmentActivity {
	TaskDataSource datasource;

	private MenuDrawerManager mMenuDrawer;

	private MenuAdapter mAdapter;
	private MenuListView mList;

	private int mActivePosition = -1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setTheme(R.style.Theme_Sherlock_Light_DarkActionBar);
		super.onCreate(savedInstanceState);

		mMenuDrawer = new MenuDrawerManager(this, MenuDrawer.MENU_DRAG_WINDOW);
		mMenuDrawer.setContentView(R.layout.activity_main);

		if (getSupportFragmentManager().findFragmentById(android.R.id.content) == null) {
			ArrayListFragment list = new ArrayListFragment();
			getSupportFragmentManager().beginTransaction()
					.add(android.R.id.content, list).commit();
		}

		// TODO: Dummy Menu for now. Implement 'real' Menu later with icons.
		List<Object> items = new ArrayList<Object>();
		items.add(new Category("task commands"));
		items.add(new Item("task next"));
		items.add(new Item("task long"));
		items.add(new Category("Projects"));
		items.add(new Item("project1"));
		items.add(new Item("project2"));

		// A custom ListView is needed so the drawer can be notified when it's
		// scrolled. This is to update the position
		// of the arrow indicator.
		mList = new MenuListView(this);
		mAdapter = new MenuAdapter(items);
		mList.setAdapter(mAdapter);
		mList.setOnItemClickListener(mItemClickListener);
		mList.setOnScrollChangedListener(new MenuListView.OnScrollChangedListener() {
			@Override
			public void onScrollChanged() {
				mMenuDrawer.getMenuDrawer().invalidate();
			}
		});

		mMenuDrawer.setMenuView(mList);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

	private AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			mActivePosition = position;
			mMenuDrawer.setActiveView(view, position);
			mMenuDrawer.closeMenu();
		}
	};

	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public void onBackPressed() {
		final int drawerState = mMenuDrawer.getDrawerState();
		if (drawerState == MenuDrawer.STATE_OPEN
				|| drawerState == MenuDrawer.STATE_OPENING) {
			mMenuDrawer.closeMenu();
			return;
		}

		super.onBackPressed();
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.task_add:
			Intent intent = new Intent(this, TaskAddActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			return true;
		case android.R.id.home:
			mMenuDrawer.toggleMenu();
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

		/**
		 *
		 * This class is based on GnuCash Mobiles "AccountsActivity.java" by
		 * Ngewi Fet <ngewif@gmail.com>
		 *
		 * Licensed under the Apache License, Version 2.0 (the "License"); you
		 * may not use this file except in compliance with the License. You may
		 * obtain a copy of the License at
		 *
		 * http://www.apache.org/licenses/LICENSE-2.0
		 *
		 * Unless required by applicable law or agreed to in writing, software
		 * distributed under the License is distributed on an "AS IS" BASIS,
		 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
		 * implied. See the License for the specific language governing
		 * permissions and limitations under the License.
		 */

		TaskDataSource datasource;
		ArrayAdapter<Task> adapter = null;
		@SuppressWarnings("unused")
		private boolean inEditMode = false;
		@SuppressWarnings("unused")
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

				case R.id.context_menu_done_task:
					doneTask(getTaskWithId(selectedItemId));
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

			private void doneTask(long selectedItemId) {
				datasource.doneTask(selectedItemId);
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
			ArrayList<Task> values = datasource.getAllTasks();
			adapter = new TaskArrayAdapter(getActivity(), R.layout.task_row,
					values);
			setListAdapter(adapter);
		}

		@Override
		public void onListItemClick(ListView l, View v, int position, long id) {
			Log.i("FragmentList", "Item clicked: " + id);

			inEditMode = true;
			selectedItemId = id + 1;
			// Start the CAB using the ActionMode.Callback defined above
			Log.i("ListItem clicked: ", Integer.toString(position));
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

	/*
	 * Item, which represents an item in the MenuList
	 */

	private static class Item {

		String mTitle;

		Item(String title) {
			mTitle = title;
		}
	}

	/*
	 * Category, which represents a category in the MenuList
	 */

	private static class Category {

		String mTitle;

		Category(String title) {
			mTitle = title;
		}
	}

	/*
	 * The MenuAdapter
	 */
	public class MenuAdapter extends BaseAdapter {

		private List<Object> mItems;

		MenuAdapter(List<Object> items) {
			mItems = items;
		}

		@Override
		public int getCount() {
			return mItems.size();
		}

		@Override
		public Object getItem(int position) {
			return mItems.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public int getItemViewType(int position) {
			return getItem(position) instanceof Item ? 0 : 1;
		}

		@Override
		public int getViewTypeCount() {
			return 2;
		}

		@Override
		public boolean isEnabled(int position) {
			return getItem(position) instanceof Item;
		}

		@Override
		public boolean areAllItemsEnabled() {
			return false;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			Object item = getItem(position);

			if (item instanceof Category) {
				if (v == null) {
					v = getLayoutInflater().inflate(R.layout.menu_row_category,
							parent, false);
				}

				((TextView) v).setText(((Category) item).mTitle);

			} else {
				if (v == null) {
					v = getLayoutInflater().inflate(R.layout.menu_row_item,
							parent, false);
				}

				TextView tv = (TextView) v;
				tv.setText(((Item) item).mTitle);
			}

			v.setTag(R.id.mdActiveViewPosition, position);

			if (position == mActivePosition) {
				mMenuDrawer.setActiveView(v, position);
			}

			return v;
		}

	}

}
