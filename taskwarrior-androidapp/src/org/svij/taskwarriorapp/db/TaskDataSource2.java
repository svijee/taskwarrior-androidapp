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

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
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
		StringBuilder output = new StringBuilder("[");

		output.append("description:" + "\"" + task_description + "\" ");
		output.append("status:" + "\"" + status + "\" ");
		output.append("uuid:" + "\"" + UUID.randomUUID().toString() + "\" ");
		output.append("entry:" + "\"" + System.currentTimeMillis() / 1000 + "\" ");

		if (!TextUtils.isEmpty(project)) {
			output.append("project:" + "\"" + project + "\" ");
		}
		if (!TextUtils.isEmpty(priority)) {
			output.append("priority:" + "\"" + priority + "\" ");
		}
		if (date != 0) {
			output.append("due:" + "\"" + date + "\" ");
		}

		output.append("]");

		
		taskDir.mkdirs();
		File pending 	= new File(taskDir, PENDING_DATA);

		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(pending, true));
			writer.append(output.toString() + "\n");
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
			FileReader fr = new FileReader(pending);
			BufferedReader br = new BufferedReader(fr);

			String strLine;
			
			while ((strLine = br.readLine()) != null) {
				taskPending.add(strLine);
			}
			br.close();
		} catch (Exception e) {
			Toast.makeText(context,
					e.getMessage() + " Unable to read to external storage.",
					Toast.LENGTH_LONG).show();
		}
		
		return taskPending;
	}

	public ArrayList<String> getCompletedLines() {
		
		ArrayList<String> taskCompleted = new ArrayList<String>();
		
		File completed = new File(taskDir, COMPLETED_DATA);
		
		try {
			FileReader fr = new FileReader(completed);
			BufferedReader br = new BufferedReader(fr);
			
			String strLine;
			
			while ((strLine = br.readLine()) != null) {
				taskCompleted.add(strLine);
			}
			br.close();
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
			BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile, true));
			
			String currentLine;
			
			while ((currentLine = reader.readLine()) != null) {
				if (currentLine.trim().contains(uuid.toString())) {
					continue;
				}
				writer.append(currentLine + "\n");
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
		StringBuilder outputString = new StringBuilder();
		
		completedTask.setStatus("completed");

		outputString.append(completedTask.toString() + " end:\"" + System.currentTimeMillis() / 1000 + "\" ");

		if (completedTask.getStart() != null && completedTask.getStart() != 0) {
			outputString.append("start:\"" + completedTask.getStart() + "\" "); 
		}
		if (completedTask.getDue() != null && completedTask.getDue().getTime() != 0) {
			outputString.append("due:\"" + completedTask.getDue().getTime() + "\" ");
		}
		if (completedTask.getUntil() != null && completedTask.getUntil().getTime() != 0) {
			outputString.append("until:\"" + completedTask.getUntil().getTime() + "\" ");
		}
		if (completedTask.getWait() != null && completedTask.getWait().getTime() != 0) {
			outputString.append("wait:\"" + completedTask.getWait().getTime() + "\" ");
		}
		if (completedTask.getRecur() != null && TextUtils.isEmpty(completedTask.getRecur())) {
			outputString.append("recur:\"" + completedTask.getRecur() + "\" ");
		}
		if (completedTask.getMask() != null && TextUtils.isEmpty(completedTask.getMask())) {
			outputString.append("mask:\"" + completedTask.getMask() + "\" ");
		}
		if (completedTask.getImask() != null && TextUtils.isEmpty(completedTask.getImask())) {
			outputString.append("imask:\"" + completedTask.getImask() + "\" ");
		}
		if (completedTask.getParent() != null && TextUtils.isEmpty(completedTask.getParent().toString())) {
			outputString.append("parent:\"" + completedTask.getParent().toString() + "\" ");
		}
//		if (completedTask.getAnnotation() != null) {
//			
//		}
		if (completedTask.getProject() != null && !TextUtils.isEmpty(completedTask.getProject())) {
			outputString.append("project:\"" + completedTask.getProject() + "\" ");
		}
		if (completedTask.getPriority() != null && !TextUtils.isEmpty(completedTask.getPriority())) {
			outputString.append("priority:\"" + completedTask.getPriority() + "\" ");
		}
//		if (completedTask.getTags() != null) {
//			
//		}

		outputString.append("]");

		try {
			
			PrintWriter completedWriter = new PrintWriter(new BufferedWriter(new FileWriter(completedFile, true)));
			
			String output = outputString.toString();
			output = output.replace("/", "\\\\/");
			output = output.replace("\"", "\\\\\"");
			output = output.replace("\b", "\\b");
			output = output.replace("\f", "\\f");
			output = output.replace("\n", "\\n");
			output = output.replace("\r", "\\r");
			output = output.replace("\t", "\\t");
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
			long t0 = System.currentTimeMillis();
			tasks.add(parseTask(data));
			long t1 = System.currentTimeMillis();
			
			double elapsedTimeSeconds = (t1 - t0);
			Log.e("task.add(parseTask(data)) ", elapsedTimeSeconds + " ms");
		}

		return tasks;
	}

	public Task parseTask(String data) {
		Task task = new Task();

		//ArrayList<String[]> helperList = new ArrayList<String[]>();
		HashMap<String, String> helperMap = new HashMap<String, String>();

		long t0 = System.currentTimeMillis();
		Pattern pattern = Pattern.compile("[\\[ ](.+?):\"(.+?)\"");
		Matcher matcher = pattern.matcher(data);

		
		while(matcher.find() == true) {			
			//String[] keyvalue = {matcher.group(1).trim(), matcher.group(2).trim()};
			helperMap.put(matcher.group(1).trim(), matcher.group(2).trim());
		}
		long t1 = System.currentTimeMillis();
		double elapsedTimeSeconds = (t1 - t0);
		Log.e("Adding to helperList ", elapsedTimeSeconds + " ms");
		
		Iterator it = helperMap.keySet().iterator();
		
		while (it.hasNext()) {
			String key = it.next().toString();
			String value = helperMap.get(key).toString();
		
			if (key.equals("description")) {
				value = value.replace("\\/", "/");
				value = value.replace("\\\"", "\"");
				value = value.replace("\\b", "\b");
				value = value.replace("\\f", "\f");
				value = value.replace("\\n", "\n");
				value = value.replace("\\r", "\r");
				value = value.replace("\\t", "\t");
				
				Pattern unicode = Pattern.compile("\\\\u(.{4})");
				Matcher m = unicode.matcher(value);
				StringBuffer sb = new StringBuffer();
				while (m.find()) {
				    int code = Integer.parseInt(m.group(1), 16);
				    m.appendReplacement(sb, new String(Character.toChars(code)));
				}
				m.appendTail(sb);
				task.setDescription(sb.toString());
			} else if  (key.equals("status")) {
				task.setStatus(value);
			} else if (key.equals("entry")) {
				task.setEntry(Long.parseLong(value));
			} else if (key.equals("uuid")) {
				task.setUUID(UUID.fromString(value));
			} else if (key.equals("start")) {
				task.setStart(Long.parseLong(value));
			} else if (key.equals("end")) {
				task.setEnd(Long.parseLong(value));
			} else if (key.equals("due")) {
				task.setDue(new Date(Long.valueOf(value) * 1000));
			} else if (key.equals("until")) {
				task.setUntil(new Date(Long.valueOf(value) * 1000));
			} else if (key.equals("wait")) {
				task.setWait(new Date(Long.valueOf(value) * 1000));
			} else if (key.equals("recur")) {
				task.setRecur(value);
			} else if (key.equals("mask")) {
				task.setMask(value);
			} else if (key.equals("imask")) {
				task.setImask(value);
			} else if (key.equals("parent")) {
				task.setParent(UUID.fromString(value));
//			} else if (key.equals("annotation")) {
//				
//			}
			} else if (key.equals("project")) {
				task.setProject(value);
			} else if (key.equals("tags")) {
				task.setTags(value);
			} else if (key.equals("priority")) {
				task.setPriority(value);
			} else if (key.equals("depends")) {
				task.setDepends(value);
			}			
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
			if (!projects.contains(task.getProject())) {
				projects.add(task.getProject());				
			}
		}
		
		Collections.sort(projects, new Comparator<String>() {
	        @Override
			public int compare(String lhs, String rhs) {
		        if (lhs == null && rhs == null) {
					return 0;
				} else if (lhs == null) {
					return 1;
				} else if (rhs == null) {
					return -1;
				}
		        return lhs.compareToIgnoreCase(rhs);
			}
			
	    });

		return projects;
	}

	public ArrayList<Task> getProjectsTasks(String project) {
		ArrayList<Task> tasks = getPendingTasks();
		ArrayList<Task> projectsTasks = new ArrayList<Task>();
		boolean hasProject = false;

		for(Task task: tasks) {
			if (task.getProject() != null) {
				if (task.getProject().equals(project)) {
					hasProject = true;
					projectsTasks.add(task);
				}
			} else if (task.getProject() == null && hasProject == false) {
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
