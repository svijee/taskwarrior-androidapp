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
import java.util.Date;
import java.util.Locale;

import org.svij.taskwarriorapp.db.ActionBarAdapter;
import org.svij.taskwarriorapp.db.TaskDataSource;

import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SlidingPaneLayout;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class TasksActivity extends FragmentActivity implements
		OnNavigationListener {
	private static final String PROJECT = "project";
	private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";
	private TaskListFragment taskListFragment;
	private SlidingPaneLayout paneLayout;
	private ActionBar actionBar;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setTheme(android.R.style.Theme_Holo_Light_DarkActionBar);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sidebar);

		TaskDataSource datasource = new TaskDataSource(this);
		datasource.createDataIfNotExist();

		if (savedInstanceState == null) {
			FragmentManager fManager = getSupportFragmentManager();
			FragmentTransaction fTransaction = fManager.beginTransaction();

			taskListFragment = new TaskListFragment();

			fTransaction.replace(R.id.content_frame, taskListFragment);
			fTransaction.commit();

			SharedPreferences prefs = PreferenceManager
					.getDefaultSharedPreferences(this);
			String defaultReport = prefs.getString("settings_date_alignment",
					getResources().getString(R.string.task_next));
			taskListFragment.setColumn(defaultReport);
		} else {
			taskListFragment = (TaskListFragment) getSupportFragmentManager()
					.getFragment(savedInstanceState,
							TaskListFragment.class.getName());
			taskListFragment.setColumn(savedInstanceState.getString(PROJECT));
		}

		paneLayout = (SlidingPaneLayout) findViewById(R.id.drawer_layout);

		paneLayout
				.setPanelSlideListener(new SlidingPaneLayout.PanelSlideListener() {

					@Override
					public void onPanelSlide(View arg0, float arg1) {
						// empty
					}

					@Override
					public void onPanelOpened(View view) {
						switch (view.getId()) {
						case R.id.content_frame:
							getActionBar().setHomeButtonEnabled(false);
							getActionBar().setDisplayHomeAsUpEnabled(false);
							break;
						default:
							break;
						}
					}

					@Override
					public void onPanelClosed(View view) {
						switch (view.getId()) {
						case R.id.content_frame:
							getActionBar().setHomeButtonEnabled(true);
							getActionBar().setDisplayHomeAsUpEnabled(true);
							break;
						default:
							break;
						}
					}
				});
	}

	@Override
	public void onStart() {
		super.onStart();
		actionBar = getActionBar();
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

		String[] menuDropdown = getResources().getStringArray(R.array.reports);
		ArrayList<String> menuCommands = new ArrayList<String>();

		for (String s : menuDropdown) {
			menuCommands.add(s);
		}

		ActionBarAdapter abAdapter = new ActionBarAdapter(this,
				R.layout.ab_main_view, menuCommands,
				getSupportFragmentManager());

		actionBar.setListNavigationCallbacks(abAdapter, this);

		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);
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

		SectionsPagerAdapter adapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
		viewPager.setOffscreenPageLimit(3);
		viewPager.setAdapter(adapter);
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
		outState.putInt(STATE_SELECTED_NAVIGATION_ITEM, getActionBar()
				.getSelectedNavigationIndex());
	}

	public SlidingPaneLayout getPaneLayout() {
		return paneLayout;
	}

	public void setPaneLayout(SlidingPaneLayout paneLayout) {
		this.paneLayout = paneLayout;
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		// Restore the previously serialized current dropdown position.
		if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
			getActionBar().setSelectedNavigationItem(
					savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
		}
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

		switch (item.getItemId()) {
		case android.R.id.home:
			paneLayout.openPane();
			return true;
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
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			if (position == 0) {
				Fragment fragment = new MenuListFragment();
				Bundle args = new Bundle();
				args.putInt("section_number", position + 1);
				fragment.setArguments(args);
				return fragment;
			} else if (position == 1) {
				Fragment fragment = new TaskListFragment();
				Bundle args = new Bundle();
				args.putInt("section_number", position + 1);
				fragment.setArguments(args);
				return fragment;
			}
			return null;
		}

		@Override
		public int getCount() {
			// Show 1 total page.
			return 1;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.pager_title_projects).toUpperCase(l);
			case 1:
				return getString(R.string.pager_title_filter).toUpperCase(l);
			}
			return null;
		}
	}

	@Override
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {

		switch (itemPosition) {
		case 0:
			taskListFragment.setColumn(getString(R.string.task_next));
			break;
		case 1:
			taskListFragment.setColumn(getString(R.string.task_long));
			break;
		case 2:
			taskListFragment.setColumn(getString(R.string.task_all));
			break;
		case 3:
			taskListFragment.setColumn(getString(R.string.task_wait));
			break;
		case 4:
			taskListFragment.setColumn(getString(R.string.task_newest));
			break;
		case 5:
			taskListFragment.setColumn(getString(R.string.task_oldest));
			break;
		}
		taskListFragment.setListView();
		TextView subtitle = (TextView) findViewById(R.id.ab_basemaps_subtitle);
		subtitle.setText(taskListFragment.getListView().getCount() + " Tasks");
		return false;
	}
}
