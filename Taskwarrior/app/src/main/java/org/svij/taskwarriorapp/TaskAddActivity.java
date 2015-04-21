package org.svij.taskwarriorapp;

import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.EditText;
import android.widget.Toast;

import org.svij.taskwarriorapp.data.TaskDataSource;

import java.sql.SQLException;


public class TaskAddActivity extends ActionBarActivity {

    TaskDataSource database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_add);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new TaskAddFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_task_add, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_save_task) {
            database = new TaskDataSource(this);
            try {
                database.open();
            } catch(SQLException e) {
                e.printStackTrace();
            }
            Task task = new Task();
            EditText editText = (EditText) findViewById(R.id.et_task_description);
            String etDescription = editText.getText().toString();

            editText = (EditText) findViewById(R.id.et_task_project);
            String etProject = editText.getText().toString();

            if (!TextUtils.isEmpty(etDescription)) {
                task.setDescription(etDescription);
                if (!TextUtils.isEmpty(etProject)) {
                    task.setProject(etProject);
                }
                database.insertTask(task);
                NavUtils.navigateUpFromSameTask(this);
            } else {
                Toast toast = Toast.makeText(this, R.string.valid_description, Toast.LENGTH_LONG);
                toast.show();
            }
        } else if (id == R.id.action_cancel_task) {
            this.finish();
            NavUtils.navigateUpFromSameTask(this);
        }

        return super.onOptionsItemSelected(item);
    }
}
