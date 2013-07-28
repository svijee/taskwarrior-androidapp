package org.svij.taskwarriorapp.test;

import org.svij.taskwarriorapp.ArrayListFragment;
import org.svij.taskwarriorapp.TaskAddActivity;
import org.svij.taskwarriorapp.TasksActivity;

import android.app.Instrumentation;
import android.test.ActivityInstrumentationTestCase2;
import android.test.InstrumentationTestRunner;
import android.test.TouchUtils;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;

public class TaskActivityTest extends
		ActivityInstrumentationTestCase2<TasksActivity> {
	private TasksActivity taskActivity;
	private TaskAddActivity taskAddActivity;
	private ArrayListFragment alFragment;

	public TaskActivityTest() {
		super(TasksActivity.class);
	}

	protected void setUp() throws Exception {
		super.setUp();

		setActivityInitialTouchMode(false);
		taskActivity = (TasksActivity) getActivity();
		alFragment = (ArrayListFragment) taskActivity
				.getSupportFragmentManager().findFragmentById(
						org.svij.taskwarriorapp.R.id.content_frame);
	}

	protected void tearDown() throws Exception {

	}

	public void testListView() {

		assertNotNull(taskActivity
				.findViewById(org.svij.taskwarriorapp.R.id.task_add));
		assertNotNull(taskActivity
				.findViewById(org.svij.taskwarriorapp.R.id.task_sync));

		ListView lv = alFragment.getListView();

		for (int i = 0; i < lv.getCount(); i++) {
			RelativeLayout view = (RelativeLayout) lv.getChildAt(i);
			assertNotNull(view);
			TouchUtils.clickView(this, view);
		}
	}
}
