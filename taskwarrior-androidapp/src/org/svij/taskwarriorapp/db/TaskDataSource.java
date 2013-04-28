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
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
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
import android.widget.Toast;

public class TaskDataSource {

	private Context context;
	private File taskDir = new File(Environment.getExternalStorageDirectory()
			.toString() + "/taskwarrior");
	private static String COMPLETED_DATA = "completed.data";
	private static String PENDING_DATA = "pending.data";
	private static String UNDO_DATA = "undo.data";
	private static String TEMP_DATA = "temp.data";

	public TaskDataSource(Context context) {
		this.context = context;
	}

	public void createTask(String description, long due, String status,
			String project, String priority, String tags) {

		Task task = new Task();
		task.setDescription(description);
		task.setStatus(status);
		task.setUUID(UUID.randomUUID());
		task.setEntry(System.currentTimeMillis() / 1000);

		if (!TextUtils.isEmpty(project)) {
			task.setProject(project);
		}
		if (!TextUtils.isEmpty(priority)) {
			task.setPriority(priority);
		}
		if (due != 0) {
			task.setDue(new Date(due));
		}

		taskDir.mkdirs();
		writeTaskToData(task, PENDING_DATA);
	}

	private ArrayList<String> getPendingLines() {
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

	private ArrayList<String> getCompletedLines() {

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

	public void editTask(UUID uuid, String task_description, long due,
			String status, String project, String priority, String tags) {
		Task task = getTask(uuid);

		task.setDescription(task_description);
		task.setDue(new Date(due));
		task.setStatus(status);
		task.setProject(project);
		task.setPriority(priority);
		task.setTags(tags);

		removeTaskFromData(uuid);
		writeTaskToData(task, PENDING_DATA);
	}

	public void deleteTask(UUID uuid) {
		finishTask(uuid, "deleted");
	}

	public void doneTask(UUID uuid) {
		finishTask(uuid, "completed");
	}

	public void finishTask(UUID uuid, String status) {

		ArrayList<Task> pendingTasks = getPendingTasks();
		Task finishedTask = new Task();

		for (Task task : pendingTasks) {
			if (task.getUuid().compareTo(uuid) == 0) {
				finishedTask = task;
			}
		}

		finishedTask.setStatus(status);

		removeTaskFromData(uuid);

		if (status.equals("completed") || status.equals("deleted")) {
			writeTaskToData(finishedTask, COMPLETED_DATA);
		} else {
			writeTaskToData(finishedTask, PENDING_DATA);
		}
	}

	private void removeTaskFromData(UUID uuid) {

		File pendingFile = new File(taskDir, PENDING_DATA);
		File tempFile = new File(taskDir, TEMP_DATA);

		try {
			BufferedReader reader = new BufferedReader(new FileReader(
					pendingFile));
			BufferedWriter writer = new BufferedWriter(new FileWriter(
					tempFile, true));

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
	}

	private void writeTaskToData(Task task, String file) {

		File completedFile = new File(taskDir, file);

		try {

			PrintWriter completedWriter = new PrintWriter(new BufferedWriter(
					new FileWriter(completedFile, true)));

			String output = task.toString();
			output = output.replaceAll("/", "\\/");
			output = output.replaceAll("\b", "\\b");
			output = output.replaceAll("\f", "\\f");
			output = output.replaceAll("\n", "\\n");
			output = output.replaceAll("\r", "\\r");
			output = output.replaceAll("\t", "\\t");
			completedWriter.println(output);
			completedWriter.close();
		} catch (Exception e) {
			Toast.makeText(context,
					e.getMessage() + " Unable to write to external storage.",
					Toast.LENGTH_LONG).show();
		}
	}

	public ArrayList<Task> getAllTasks() {

		ArrayList<Task> tasks = new ArrayList<Task>();
		ArrayList<String> allTasks = getPendingLines();
		allTasks.addAll(getCompletedLines());

		for (String data : allTasks) {
			tasks.add(parseTask(data));
		}

		return tasks;
	}

	public Task parseTask(String data) {

		Task task = new Task();
		HashMap<String, String> helperMap = new HashMap<String, String>();

		Pattern pattern = Pattern.compile("[\\[ ](.+?):\"(.+?)\"");
		Matcher matcher = pattern.matcher(data);

		while (matcher.find() == true) {
			helperMap.put(matcher.group(1).trim(), matcher.group(2).trim());
		}

		Iterator<String> it = helperMap.keySet().iterator();

		while (it.hasNext()) {
			String key = it.next().toString();
			String value = helperMap.get(key).toString();

			if (key.equals("description")) {
				value = value.replace("\\/", "/");
				value = value.replace("\\\"", "\"");
				value = value.replace("\\f", "\f");
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
			} else if (key.equals("status")) {
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
				task.setUntil(new Date(Long.valueOf(value)* 1000));
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
		// } else if (key.equals("annotation")) {
		//
		// }
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

		for (String data : allTasks) {
			Task task = parseTask(data);
			tasks.add(task);
		}

		return tasks;
	}

	public ArrayList<String> getProjects() {
		ArrayList<Task> tasks = getPendingTasks();
		ArrayList<String> projects = new ArrayList<String>();

		for (Task task : tasks) {
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

		for (Task task : tasks) {
			if (task.getProject() != null) {
				if (task.getProject().equals(project)) {
					projectsTasks.add(task);
				}
			} else if (task.getProject() == null && TextUtils.isEmpty(project)) {
				projectsTasks.add(task);
			}
		}

		return projectsTasks;
	}

	public Task getTask(UUID uuid) {
		ArrayList<Task> allTasks = getAllTasks();

		for (Task task : allTasks) {
			if (task.getUuid().equals(uuid)) {
				return task;
			}
		}

		// Should never happen.
		Task task_not_found = new Task();
		task_not_found.setDescription("Task not found");
		return task_not_found;
	}

	public void createDataIfNotExist() {
		File pending = new File(taskDir, PENDING_DATA);
		File completed = new File(taskDir, COMPLETED_DATA);
		File undo = new File(taskDir, UNDO_DATA);

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
