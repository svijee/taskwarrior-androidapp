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
import java.util.List;

import org.svij.taskwarriorapp.db.TaskDataSource;
import org.svij.taskwarriorapp.ui.MenuListView;

import net.simonvt.menudrawer.MenuDrawer;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class TasksActivity extends SherlockFragmentActivity {
	private static final String PROJECT = "project";
	private ArrayListFragment listFragment;
	private MenuDrawer menu;
	private MenuAdapter mAdapter;
	private MenuListView mList;
	private int mActivePosition = -1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setTheme(R.style.Theme_Sherlock_Light_DarkActionBar);
		super.onCreate(savedInstanceState);
		TaskDataSource datasource = new TaskDataSource(this);
		datasource.createDataIfNotExist();

		if (savedInstanceState != null) {
			listFragment = (ArrayListFragment) getSupportFragmentManager()
					.getFragment(savedInstanceState,
							ArrayListFragment.class.getName());
		} else {
			listFragment = new ArrayListFragment();
			getSupportFragmentManager().beginTransaction()
					.replace(android.R.id.content, listFragment).commit();
		}

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		setMenu();
	}

	private void setMenu() {

		menu = MenuDrawer.attach(this, MenuDrawer.MENU_DRAG_WINDOW);
		menu.setContentView(R.layout.activity_main);

		List<Object> items = new ArrayList<Object>();
		items.add(new Category("task commands"));
		items.add(new Item(getString(R.string.task_next)));
		items.add(new Item(getString(R.string.task_long)));
		items.add(new Item(getString(R.string.task_all)));
		items.add(new Category("Projects"));

		TaskDataSource datasource = new TaskDataSource(this);

		ArrayList<String> values = datasource.getProjects();

		for (String project : values) {
			if (TextUtils.isEmpty(project)) {
				items.add(new Item(getString(R.string.no_project)));
			} else {
				items.add(new Item(project));
			}
		}

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
				menu.getMenuView().invalidate();
			}
		});

		menu.setMenuView(mList);
	}

	private AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			mActivePosition = position;
			menu.setActiveView(view, position);
			listFragment.setColumn(((TextView) view).getText().toString());
			listFragment.setListView();
			menu.closeMenu();
		}
	};

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
		case R.id.menu_settings:
			Intent intentSettings = new Intent(this, SettingsActivity.class);
			startActivity(intentSettings);
			return true;
		case android.R.id.home:
			menu.toggleMenu();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void onTaskButtonClick(View view) {
		listFragment.onTaskButtonClick(view);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		super.onSaveInstanceState(outState);
		getSupportFragmentManager().putFragment(outState,
				ArrayListFragment.class.getName(), listFragment);
		outState.putString(PROJECT, listFragment.getColumn());
	}

	@Override
	public void onBackPressed() {
		final int drawerState = menu.getDrawerState();
		if (drawerState == MenuDrawer.STATE_OPEN
				|| drawerState == MenuDrawer.STATE_OPENING) {
			menu.closeMenu();
			return;
		}

		super.onBackPressed();
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
				menu.setActiveView(v, position);
			}

			return v;
		}
	}
}
