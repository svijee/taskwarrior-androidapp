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

import android.content.Intent;
import android.os.Bundle;
import android.view.Display;
import android.view.WindowManager;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.slidingmenu.lib.SlidingMenu;

public class TasksActivity extends SherlockFragmentActivity {
	private static final String PROJECT = "project";
	private ArrayListFragment listFragment;
	private MenuListFragment menuFragment;
	private SlidingMenu menu;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setTheme(R.style.Theme_Sherlock_Light_DarkActionBar);
		super.onCreate(savedInstanceState);

		// Setup the Menu
		menu = new SlidingMenu(this);
		menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		menu.setShadowWidthRes(R.dimen.shadow_width);
		menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		menu.setFadeDegree(0.35f);
		menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
		menu.setMenu(R.layout.menu_frame);
		changeMenuOffset();

		if (savedInstanceState != null) {
			menuFragment = (MenuListFragment) getSupportFragmentManager()
					.getFragment(savedInstanceState,
							MenuListFragment.class.getName());
			menuFragment.setMenu(menu);
			listFragment = (ArrayListFragment) getSupportFragmentManager()
					.getFragment(savedInstanceState,
							ArrayListFragment.class.getName());
			menuFragment.setColumn((savedInstanceState.getString(PROJECT)));
		} else {
			menuFragment = new MenuListFragment();
			menuFragment.setMenu(menu);
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.menu_frame, menuFragment).commit();
			listFragment = new ArrayListFragment();
			getSupportFragmentManager().beginTransaction()
					.add(android.R.id.content, listFragment).commit();

		}

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

	}

	private void changeMenuOffset() {
		WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		@SuppressWarnings("deprecation")
		int behindOffset_dp = display.getWidth() - 350;
		menu.setBehindOffset(behindOffset_dp);
	}

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
		case android.R.id.home:
			menu.toggle();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
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
		getSupportFragmentManager().putFragment(outState,
				ArrayListFragment.class.getName(), listFragment);
		getSupportFragmentManager().putFragment(outState,
				MenuListFragment.class.getName(), menuFragment);
		outState.putString(PROJECT, menuFragment.getColumn());
	}

	@Override
	public void onBackPressed() {
		if (menu.isMenuShowing()) {
			menu.showContent();
		} else {
			super.onBackPressed();
		}
	}

	public SlidingMenu getMenu() {
		return menu;
	}

}
