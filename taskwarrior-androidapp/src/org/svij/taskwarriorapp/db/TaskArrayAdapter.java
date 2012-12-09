package org.svij.taskwarriorapp.db;

import java.text.DateFormat;
import java.util.ArrayList;
import org.svij.taskwarriorapp.R;
import org.svij.taskwarriorapp.data.Task;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class TaskArrayAdapter extends ArrayAdapter<Task> {
	private ArrayList<Task> entries;
	private Activity activity;

	public TaskArrayAdapter(Activity a, int textViewResourceId,
			ArrayList<Task> entries) {
		super(a, textViewResourceId, entries);
		this.entries = entries;
		this.activity = a;
	}

	public static class ViewHolder {
		public TextView taskDescription;
		public TextView taskProject;
		public TextView taskDueDate;
		public TextView taskPriority;
		public TextView taskStatus;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		ViewHolder holder;
		if (v == null) {
			LayoutInflater vi = (LayoutInflater) activity
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.task_row, null);
			holder = new ViewHolder();
			holder.taskDescription = (TextView) v
					.findViewById(R.id.tvRowTaskDescription);
			holder.taskProject = (TextView) v
					.findViewById(R.id.tvRowTaskProject);
			holder.taskDueDate = (TextView) v
					.findViewById(R.id.tvRowTaskDueDate);
			holder.taskPriority = (TextView) v
					.findViewById(R.id.tvRowTaskPriority);
			holder.taskStatus = (TextView) v.findViewById(R.id.tvRowTaskStatus);

			v.setTag(holder);
		} else
			holder = (ViewHolder) v.getTag();

		final Task task = entries.get(position);
		if (task != null) {
			holder.taskDescription.setText(task.getDescription());
			holder.taskProject.setText(task.getProject());

			if (!(task.getDuedate().getTime() == 0)) {
				holder.taskDueDate.setText(DateFormat.getDateInstance(
						DateFormat.SHORT).format(task.getDuedate()));
			}

			if (!task.getPriority().equals("no priority")) {
				holder.taskPriority.setText(getContext().getString(
						R.string.priority)
						+ ": " + task.getPriority());
			}
			if (task.getStatus().equals("done")) {
				holder.taskStatus.setText(getContext().getString(
						R.string.status)
						+ ": " + task.getStatus());
			}
		}

		return v;
	}

}