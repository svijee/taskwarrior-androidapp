package org.svij.taskwarriorapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

public class TaskAddActivity extends Activity {
	private TaskDataSource datasource;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_add);
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_task_add, menu);
        return true;
    }

    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    public void onClick(View view) {
    	datasource = new TaskDataSource(this);
		datasource.open();
		
    	EditText etTaskAdd = (EditText) findViewById(R.id.etTaskAdd);
    	datasource.createTask(etTaskAdd.getText().toString());
    	Intent intent = new Intent(this, MainActivity.class);
    	startActivity(intent);
    }

    @Override
    protected void onPause() {
      datasource.close();
      super.onPause();
    }
}
