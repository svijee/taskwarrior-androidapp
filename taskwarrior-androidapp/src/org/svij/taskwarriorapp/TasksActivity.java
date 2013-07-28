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

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SlidingPaneLayout;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class TasksActivity extends SherlockFragmentActivity implements
		ActionBar.OnNavigationListener {
	private static final String PROJECT = "project";
	private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";
	private ArrayListFragment listFragment;
	private SlidingPaneLayout paneLayout;
	private ActionBar actionBar;

	public SlidingPaneLayout getPaneLayout() {
		return paneLayout;
	}

	public void setPaneLayout(SlidingPaneLayout paneLayout) {
		this.paneLayout = paneLayout;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setTheme(R.style.Theme_Sherlock_Light_DarkActionBar);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sidebar);
		TaskDataSource datasource = new TaskDataSource(this);
		datasource.createDataIfNotExist();

		if (savedInstanceState == null) {
			FragmentManager fragmentManager = getSupportFragmentManager();
			FragmentTransaction fragmentTransaction = fragmentManager
					.beginTransaction();

			listFragment = new ArrayListFragment();

			fragmentTransaction.replace(R.id.content_frame, listFragment);
			fragmentTransaction.commit();

			SharedPreferences prefs = PreferenceManager
					.getDefaultSharedPreferences(this);
			String defaultReport = prefs.getString("settings_date_alignement",
					getResources().getString(R.string.task_next));
			listFragment.setColumn(defaultReport);
		} else {
			listFragment = (ArrayListFragment) getSupportFragmentManager()
					.getFragment(savedInstanceState,
							ArrayListFragment.class.getName());
			listFragment.setColumn(savedInstanceState.getString(PROJECT));
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
							getSupportActionBar().setHomeButtonEnabled(false);
							getSupportActionBar().setDisplayHomeAsUpEnabled(
									false);
							break;
						default:
							break;
						}
					}

					@Override
					public void onPanelClosed(View view) {
						switch (view.getId()) {
						case R.id.content_frame:
							getSupportActionBar().setHomeButtonEnabled(true);
							getSupportActionBar().setDisplayHomeAsUpEnabled(
									true);
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
		actionBar = getSupportActionBar();
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

		String[] menuDropdown = getResources().getStringArray(R.array.reports);
		ArrayList<String> alMenuCommands = new ArrayList<String>();

		for (String s : menuDropdown) {
			alMenuCommands.add(s);
		}

		ActionBarAdapter abAdapter = new ActionBarAdapter(this,
				R.layout.ab_main_view, alMenuCommands,
				getSupportFragmentManager());

		actionBar.setListNavigationCallbacks(abAdapter, this);

		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		// Restore the previously serialized current dropdown position.
		if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
			getSupportActionBar().setSelectedNavigationItem(
					savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
		}
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
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
		listFragment.onTaskButtonClick(view);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	protected void onResume() {
		super.onResume();

		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		long date_long = prefs.getLong("notifications_alarm_time",
				System.currentTimeMillis());

		AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
		Intent i = new Intent(this, NotificationService.class);
		PendingIntent pi = PendingIntent.getService(this, 0, i, 0);

		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(date_long);

		if (!calendar.getTime().before(new Date())) {
			am.cancel(pi);
			am.setInexactRepeating(AlarmManager.RTC_WAKEUP,
					calendar.getTimeInMillis(), calendar.getTimeInMillis()
							+ AlarmManager.INTERVAL_DAY, pi);
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
				ArrayListFragment.class.getName(), listFragment);
		outState.putString(PROJECT, listFragment.getColumn());
		outState.putInt(STATE_SELECTED_NAVIGATION_ITEM, getSupportActionBar()
				.getSelectedNavigationIndex());
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
				Fragment fragment = new ArrayListFragment();
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
			listFragment.setColumn(getString(R.string.task_next));
			break;
		case 1:
			listFragment.setColumn(getString(R.string.task_long));
			break;
		case 2:
			listFragment.setColumn(getString(R.string.task_all));
			break;
		case 3:
			listFragment.setColumn(getString(R.string.task_wait));
			break;
		case 4:
			listFragment.setColumn(getString(R.string.task_newest));
			break;
		case 5:
			listFragment.setColumn(getString(R.string.task_oldest));
			break;
		}
		listFragment.setListView();
		TextView subtitle = (TextView) findViewById(R.id.ab_basemaps_subtitle);
		subtitle.setText(listFragment.getListView().getCount() + " Tasks");
		return false;
	}
}
