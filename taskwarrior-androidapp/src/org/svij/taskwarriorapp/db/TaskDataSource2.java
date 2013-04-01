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

package org.svij.taskwarriorapp.db;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.svij.taskwarriorapp.data.Task;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

public class TaskDataSource2 {

	private Context context;
	private File taskDir = new File(Environment.getExternalStorageDirectory()
			.toString() + "/taskwarrior");
	private static String COMPLETED_DATA = "completed.data";
	private static String PENDING_DATA = "pending.data";
	private static String UNDO_DATA = "undo.data";
	
	public TaskDataSource2(Context context) {
		this.context = context;
	}

	public void createTask(String task_description, long date, String status,
			String project, String priority, String tags) {
		String output = "[";

		output += "description:" + "\"" + task_description + "\" ";
		output += "status:" + "\"" + status + "\" ";
		output += "uuid:" + "\"" + UUID.randomUUID().toString() + "\" ";
		output += "entry:" + "\"" + System.currentTimeMillis() / 1000 + "\" ";

		if (!TextUtils.isEmpty(project)) {
			output += "project:" + "\"" + project + "\" ";
		}
		if (!TextUtils.isEmpty(priority)) {
			output += "priority:" + "\"" + priority + "\" ";
		}
		if (date != 0) {
			output += "due:" + "\"" + date + "\" ";
		}

		output += "]";

		
		taskDir.mkdirs();
		File pending 	= new File(taskDir, PENDING_DATA);

		try {
			PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(pending, true)));
			writer.println(output);
			writer.close();
		} catch (Exception e) {
			Toast.makeText(context,
					e.getMessage() + " Unable to write to external storage.",
					Toast.LENGTH_LONG).show();
		}
		
		Toast.makeText(context, "Created task " + "'" + task_description + "'", Toast.LENGTH_LONG).show();
	}

	public ArrayList<String> getPendingLines() {
		ArrayList<String> taskPending = new ArrayList<String>();
		
		File pending = new File(taskDir, PENDING_DATA);
		
		try {
			FileInputStream fstream = new FileInputStream(pending);
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			
			String strLine;
			
			while ((strLine = br.readLine()) != null) {
				taskPending.add(strLine);
			}
			in.close();
		} catch (Exception e) {
			Toast.makeText(context,
					e.getMessage() + " Unable to read to external storage.",
					Toast.LENGTH_LONG).show();
		}
		
		return taskPending;
	}

	public ArrayList<String> getCompletedLines() {
		ArrayList<String> taskCompleted = new ArrayList<String>();
		
		File pending = new File(taskDir, COMPLETED_DATA);
		
		try {
			FileInputStream fstream = new FileInputStream(pending);
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			
			String strLine;
			
			while ((strLine = br.readLine()) != null) {
				taskCompleted.add(strLine);
			}
			in.close();
		} catch (Exception e) {
			Toast.makeText(context,
					e.getMessage() + " Unable to read to external storage.",
					Toast.LENGTH_LONG).show();
		}
		
		return taskCompleted;
	}

	public boolean checkIfExternalStorageIsWritable() {
		String state = Environment.getExternalStorageState();

		if (Environment.MEDIA_MOUNTED.equals(state)) {
			return true;
		} else {
			return false;
		}
	}

	public void editTask(UUID uuid, String task_description, long date,
			String status, String project, String priority, String tags) {
//		ContentValues values = new ContentValues();
//		values.put(SQLiteHelper.COLUMN_UUID, uuid.toString());
//		values.put(SQLiteHelper.COLUMN_DESCRIPTION, task_description);
//		values.put(SQLiteHelper.COLUMN_DUEDATE, date);
//		values.put(SQLiteHelper.COLUMN_STATUS, status);
//		values.put(SQLiteHelper.COLUMN_PROJECT, project);
//		values.put(SQLiteHelper.COLUMN_PRIORITY, priority);
//		values.put(SQLiteHelper.COLUMN_TAGS, tags);
//		database.update(SQLiteHelper.TABLE_TASKS, values,
//				SQLiteHelper.COLUMN_UUID + " = '" + uuid.toString() + "'", null);
//		values = null;
	}

	public void deleteTask(UUID uuid) {
//		Log.i("Deleted:", "Task with uuid: " + uuid.toString());
//		ContentValues values = new ContentValues();
//		values.put(SQLiteHelper.COLUMN_STATUS, "deleted");
//		database.update(SQLiteHelper.TABLE_TASKS, values,
//				SQLiteHelper.COLUMN_UUID + " = '" + uuid.toString() + "'", null);
	}

	public void doneTask(UUID uuid) {

		File pendingFile = new File(taskDir, PENDING_DATA);
		File tempFile = new File(taskDir, "temp_pending.data");
		ArrayList<Task> pendingTasks = getPendingTasks();

		try {
			BufferedReader reader = new BufferedReader(new FileReader(pendingFile));
			PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(tempFile, true)));
			
			String currentLine;
			
			while ((currentLine = reader.readLine()) != null) {
				if (currentLine.trim().contains(uuid.toString())) {
					Log.i("bla", "bla");
					continue;
				}
				writer.println(currentLine);
			}
			
			tempFile.renameTo(pendingFile);
			
			reader.close();
			writer.close();

		} catch (Exception e) {
			Toast.makeText(context,
					e.getMessage() + " Unable to write to external storage.",
					Toast.LENGTH_LONG).show();
		}

		Task completedTask = new Task();

		for (Task task: pendingTasks) {
			if (task.getUuid().compareTo(uuid) == 0) {
				completedTask = task;
			}
		}

		File completedFile = new File(taskDir, COMPLETED_DATA);
		try {
			completedTask.setStatus("completed");
			PrintWriter completedWriter = new PrintWriter(new BufferedWriter(new FileWriter(completedFile, true)));
			String outputString = completedTask.toString() + " end:\"" + System.currentTimeMillis() / 1000 + "\" ";
			
			if (completedTask.getDuedate().getTime() != 0) {
				outputString += "due:\"" + completedTask.getDuedate() + "\" ";
			}
			if (TextUtils.isEmpty(completedTask.getPriority())) {
				outputString += "priority:\"" + completedTask.getPriority() + "\" ";
			}
			if (TextUtils.isEmpty(completedTask.getProject())) {
				outputString += "project:\"" + completedTask.getProject() + "\" ";
			}
//			if (completedTask.getTags() != null) {
//				
//			}
			outputString += "]";
			completedWriter.println(outputString);
			completedWriter.close();
		} catch (Exception e) {
			Toast.makeText(context,
					e.getMessage() + " Unable to write to external storage.",
					Toast.LENGTH_LONG).show();
		}
		
		
		
		Log.i("Done:", "Task with id: " + uuid.toString());
		Log.i("DoneTime:", ":" + System.currentTimeMillis() / 1000);
	}

	public ArrayList<Task> getAllTasks() {
		ArrayList<Task> tasks = new ArrayList<Task>();
		ArrayList<String> allTasks = getPendingLines();
		allTasks.addAll(getCompletedLines());

		for (String data: allTasks) {
			Task task = parseTask(data);
			tasks.add(task);
		}

		return tasks;
	}
	
	public Task parseTask(String data) {
		Task task = new Task();
		
		Pattern pattern;
		Matcher matcher;

		try {
			pattern = Pattern.compile("description:\"(.+?)\"");
			matcher = pattern.matcher(data);
			matcher.find();
			task.setDescription(matcher.group(1));
			
			pattern = Pattern.compile("status:\"(.+?)\"");
			matcher = pattern.matcher(data);
			matcher.find();
			task.setStatus(matcher.group(1));
			
			pattern = Pattern.compile("entry:\"(.+?)\"");
			matcher = pattern.matcher(data);
			matcher.find();
			task.setEntry(Long.valueOf(matcher.group(1)));
			
			pattern = Pattern.compile("uuid:\"(.+?)\"");
			matcher = pattern.matcher(data);
			matcher.find();
			task.setId(UUID.fromString(matcher.group(1)));			
		} catch (Exception e) {
			Toast.makeText(context,
					e.getMessage() + " Error while parsing pending.data.",
					Toast.LENGTH_LONG).show();
		}
		
		try {
			pattern = Pattern.compile("due:\"(.+?)\"");
			matcher = pattern.matcher(data);
			matcher.find();
			task.setDuedate(new Date(Long.valueOf(matcher.group(1))));
		} catch (Exception e) {
			task.setDuedate(new Date(0));
		}
		
		try {
			pattern = Pattern.compile("project:\"(.+?)\"");
			matcher = pattern.matcher(data);
			matcher.find();
			task.setProject(matcher.group(1));
		} catch (Exception e) {
			task.setProject("");
		}
		
		try {
			pattern = Pattern.compile("priority:\"(.+?)\"");
			matcher = pattern.matcher(data);
			matcher.find();
			task.setPriority(matcher.group(1));
		} catch (Exception e) {
			task.setPriority("");
		}
		
		task.urgency_c();

		return task;
	}
	

	public ArrayList<Task> getPendingTasks() {
		ArrayList<Task> tasks = new ArrayList<Task>();
		ArrayList<String> allTasks = getPendingLines();

		for (String data: allTasks) {
			Task task = parseTask(data);
			tasks.add(task);
		}

		return tasks;
	}

	public ArrayList<String> getProjects() {
		ArrayList<Task> tasks = getPendingTasks();
		ArrayList<String> projects = new ArrayList<String>();
		
		for(Task task: tasks) {
			projects.add(task.getProject());
		}
		
		return projects;
	}

	public ArrayList<Task> getProjectsTasks(String project) {
		ArrayList<Task> tasks = getPendingTasks();
		ArrayList<Task> projectsTasks = new ArrayList<Task>();
		
		for(Task task: tasks) {
			if (task.getProject().equals(project)) {
				projectsTasks.add(task);				
			}
		}
		
		return projectsTasks;
	}

	public Task getTask(UUID uuid) {
		ArrayList<Task> allTasks = getAllTasks();
		
		for (Task task: allTasks) {
			if (task.getUuid().equals(uuid)) {
				return task;
			}
		}

		//Should never happen.
		Task task_not_found = new Task();
		task_not_found.setDescription("Task not found");
		return task_not_found;
	}
	
	public void createDataIfNotExist() {
		File pending	= new File(taskDir, PENDING_DATA);
		File completed	= new File(taskDir, COMPLETED_DATA);
		File undo		= new File(taskDir, UNDO_DATA);
		
		try {
			if (!pending.exists()) {
				pending.createNewFile();
			}
			if (!completed.exists()) {
				completed.createNewFile();
			}
			if (!undo.exists()) {
				undo.createNewFile();
			}
		} catch (Exception e) {
			e.printStackTrace();	
		}
	}
}
