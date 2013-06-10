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
import java.util.Date;

import org.svij.taskwarriorapp.db.TaskDataSource;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.SlidingPaneLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class TasksActivity extends SherlockFragmentActivity {
	private static final String PROJECT = "project";
	private ArrayListFragment listFragment;
	private SlidingPaneLayout paneLayout;
	private ListView menuList;
	private String[] taskMenuCommands;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setTheme(R.style.Theme_Sherlock_Light_DarkActionBar);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sidebar);

		TaskDataSource datasource = new TaskDataSource(this);
		datasource.createDataIfNotExist();

		ArrayList<String> menuCommands = new ArrayList<String>();

		menuCommands.add(getResources().getString(R.string.task_next));
		menuCommands.add(getResources().getString(R.string.task_long));
		menuCommands.add(getResources().getString(R.string.task_all));
		menuCommands.add(getResources().getString(R.string.task_wait));

		menuCommands.addAll(datasource.getProjects());
		if (menuCommands.remove(null)) {
			menuCommands.add(getString(R.string.no_project));
		}
		menuCommands.removeAll(Collections.singleton(null));

		taskMenuCommands = menuCommands
				.toArray(new String[menuCommands.size()]);
		paneLayout = (SlidingPaneLayout) findViewById(R.id.drawer_layout);
		menuList = (ListView) findViewById(R.id.left_drawer);

		menuList.setAdapter(new ArrayAdapter<String>(this,
				R.layout.drawer_list_item, taskMenuCommands));
		menuList.setOnItemClickListener(new DrawerItemClickListener());

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

		if (savedInstanceState != null) {
			listFragment = (ArrayListFragment) getSupportFragmentManager()
					.getFragment(savedInstanceState,
							ArrayListFragment.class.getName());
			listFragment.setColumn(savedInstanceState.getString(PROJECT));
		} else {
			listFragment = new ArrayListFragment();
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.content_frame, listFragment).commit();
		}

		getActionBar().setDisplayHomeAsUpEnabled(false);
		getActionBar().setHomeButtonEnabled(false);
		listFragment.setColumn(getResources().getString(R.string.task_next));
	}

	private void setActionBarTitle() {
		int counter = listFragment.getListView().getCount();

		if (counter == 1) {
			getActionBar().setTitle(
					listFragment.getColumn() + " (1 Task)");
		} else if (counter > 1) {
			getActionBar().setTitle(
					listFragment.getColumn()
							+ " ("
							+ listFragment.getListView().getCount()
							+ getResources().getString(
									R.string.title_task_counter) + ")");
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

	private class DrawerItemClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			listFragment.onTaskButtonClick(view);
			String menu_text = ((TextView) view).getText().toString();
			listFragment.setColumn(menu_text);
			listFragment.setListView();
			paneLayout.closePane();
			setActionBarTitle();
			menuList.setItemChecked(position, true);
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

		setActionBarTitle();

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
	}
}
