package org.svij.taskwarriorapp;


import android.os.Bundle;
import android.app.Fragment;
import android.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

public class TaskFragment extends ListFragment {

    public static TaskFragment newInstance() {
        TaskFragment fragment = new TaskFragment();
        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ArrayList<Task> taskList = new ArrayList<>();
        Task task1 = new Task();
        task1.setDescription("Task 1");
        Task task2 = new Task();
        task2.setDescription("Task 2");
        Task task3 = new Task();
        task3.setDescription("Task 3");
        taskList.add(task1);
        taskList.add(task2);
        taskList.add(task3);

        setListAdapter(new ArrayAdapter<Task>(getActivity(),
                android.R.layout.simple_list_item_1, android.R.id.text1, taskList));

    }

    public TaskFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_task_list, container, false);
    }


}
