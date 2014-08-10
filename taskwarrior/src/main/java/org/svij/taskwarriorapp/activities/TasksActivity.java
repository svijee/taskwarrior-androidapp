/**
 * taskwarrior for android – a task list manager
 *
 * Copyright (c) 2012-2014 Sujeevan Vijayakumaran
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

package org.svij.taskwarriorapp.activities;

import java.util.Calendar;
import java.util.Date;

import org.svij.taskwarriorapp.R;
import org.svij.taskwarriorapp.db.TaskDatabase;
import org.svij.taskwarriorapp.fragments.MenuListFragment;
import org.svij.taskwarriorapp.fragments.TaskListFragment;
import org.svij.taskwarriorapp.services.NotificationService;

import android.app.ActionBar.OnNavigationListener;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class TasksActivity extends FragmentActivity implements
		OnNavigationListener {
	private static final String PROJECT = "project";
	private TaskListFragment taskListFragment;
	private MenuListFragment menuListFragment;
	private DrawerLayout drawerLayout;
	private ListView drawerList;
	private ActionBarDrawerToggle drawerToggle;
	private String column;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setTheme(android.R.style.Theme_Holo_Light_DarkActionBar);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sidebar);

		TaskDatabase data = new TaskDatabase(this);
		data.createDataIfNotExist();

		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		column = prefs.getString("settings_date_alignment",
				getResources().getString(R.string.task_next));

		if (savedInstanceState == null) {
			FragmentManager fManager = getSupportFragmentManager();
			FragmentTransaction fTransaction = fManager.beginTransaction();

			taskListFragment = new TaskListFragment();
			menuListFragment = new MenuListFragment();

			fTransaction.replace(R.id.content_frame, taskListFragment);
			fTransaction.replace(R.id.right_drawer, menuListFragment);
			fTransaction.commit();

			taskListFragment.setColumn(column);
		} else {
			taskListFragment = (TaskListFragment) getSupportFragmentManager()
					.getFragment(savedInstanceState,
							TaskListFragment.class.getName());
			taskListFragment.setColumn(savedInstanceState.getString(PROJECT));
		}

		drawerList = (ListView) findViewById(R.id.left_drawer);
		drawerList.setAdapter(new ArrayAdapter<String>(this,
				R.layout.drawer_list_item, getResources().getStringArray(
						R.array.reports)));
		drawerList.setOnItemClickListener(new DrawerItemClickListener());

		drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		drawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
				GravityCompat.START);

		drawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
				R.drawable.ic_drawer, R.string.drawer_open,
				R.string.drawer_close) {

			/** Called when a drawer has settled in a completely closed state. */
			public void onDrawerClosed(View view) {
				getActionBar().setTitle(getTitle());
				invalidateOptionsMenu();
			}

			/** Called when a drawer has settled in a completely open state. */
			public void onDrawerOpened(View drawerView) {
				getActionBar().setTitle(getTitle());
				invalidateOptionsMenu();
			}
		};

		drawerLayout.setDrawerListener(drawerToggle);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setDisplayShowTitleEnabled(true);
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	protected void onResume() {
		super.onResume();

		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		long date_long = prefs.getLong("notifications_alarm_time",
				System.currentTimeMillis());

		Intent myIntent = new Intent(this, NotificationService.class);
		AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		PendingIntent pendingIntent = PendingIntent.getService(this, 0,
				myIntent, 0);

		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(date_long);

		alarmManager.cancel(pendingIntent);
		if (!calendar.getTime().before(new Date())) {
			alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
					calendar.getTimeInMillis(), 24 * 60 * 60 * 1000,
					pendingIntent);
		}
		setTitle(column + " (" + taskListFragment.getListView().getCount() +")");
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		getSupportFragmentManager().putFragment(outState,
				TaskListFragment.class.getName(), taskListFragment);
		outState.putString(PROJECT, taskListFragment.getColumn());
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		taskListFragment.setColumn(savedInstanceState.getString(PROJECT));
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.activity_main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		return super.onPrepareOptionsMenu(menu);
	}

	public boolean onOptionsItemSelected(MenuItem item) {

		if (drawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		switch (item.getItemId()) {
		case R.id.task_sync:
			Toast toast = Toast.makeText(this, "Sync will be added soon.",
					Toast.LENGTH_LONG);
			toast.show();
			return true;
		case R.id.task_add:
			Intent intent = new Intent(this, TaskAddActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			return true;
		case R.id.menu_settings:
			Intent intentSettings = new Intent(this, SettingsActivity.class);
			startActivity(intentSettings);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void onTaskButtonClick(View view) {
		taskListFragment.onTaskButtonClick(view);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		drawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {
		return false;
	}

	private class DrawerItemClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			selectItem(position);
		}
	}

	private void selectItem(int position) {
		drawerList.setItemChecked(position, true);
		TextView selectedTextView = (TextView) drawerList.getChildAt(position);
		column = selectedTextView.getText().toString();
		taskListFragment.setColumn(column);
		taskListFragment.setListView();
		drawerLayout.closeDrawers();
		setTitle(column + " (" + taskListFragment.getListView().getCount() +")");
	}
}
