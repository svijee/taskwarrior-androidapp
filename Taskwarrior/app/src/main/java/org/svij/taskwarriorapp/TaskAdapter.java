package org.svij.taskwarriorapp;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by sujee on 05.04.15.
 */
public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {
    private ArrayList<Task> taskList;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        protected TextView tvDescription;
        protected TextView tvProject;
        protected TextView tvDuedate;

        public ViewHolder(View v) {
            super(v);
            tvDescription = (TextView) v.findViewById(R.id.tv_task_description);
            tvProject = (TextView) v.findViewById(R.id.tv_task_project);
            tvDuedate = (TextView) v.findViewById(R.id.tv_task_duedate);
        }
    }

    public TaskAdapter(ArrayList<Task> taskList) {
        this.taskList = taskList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_card_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Task task = taskList.get(position);
        holder.tvDescription.setText(task.getDescription());

        if (!TextUtils.isEmpty(task.getProject())) {
            holder.tvProject.setText("Project: " + task.getProject());
        }
        holder.tvDuedate.setText("DUEDATE");
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }
}
