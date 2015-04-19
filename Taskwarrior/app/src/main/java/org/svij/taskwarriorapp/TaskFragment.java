package org.svij.taskwarriorapp;


import android.os.Bundle;
import android.app.Fragment;
import android.app.ListFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import org.svij.taskwarriorapp.data.TaskDataSource;

import java.sql.SQLException;
import java.util.ArrayList;

public class TaskFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private TaskDataSource dataSource;

    public static TaskFragment newInstance() {
        return new TaskFragment();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        dataSource = new TaskDataSource(getActivity());

        try {
            dataSource.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        ArrayList<Task> taskList = dataSource.getAllTasks();
        dataSource.close();

        if (taskList != null) {
            recyclerView = (RecyclerView) getActivity().findViewById(R.id.task_recycler_view);
            layoutManager = new LinearLayoutManager(getActivity());
            recyclerView.setLayoutManager(layoutManager);
            adapter = new TaskAdapter(taskList);
            recyclerView.setAdapter(adapter);
        }
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
