package org.svij.taskwarriorapp;

public class Task {
	private long id;
	private String description;
	private String duedate;
	private long entry;
	private String priority;
	private String project;
	private String status;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getProject() {
		return project;
	}

	public void setProject(String project) {
		this.project = project;
	}

	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	@Override
	public String toString() {
		if (duedate.length() == 0) {
			return id + ".) " + description + " – Entry: " + entry + " – Status: " + status;
		} else {
			return id + ".) " + description + " – Due: " + duedate;
		}
	}

	public String getDuedate() {
		return duedate;
	}

	public void setDuedate(String duedate) {
		this.duedate = duedate;
	}

	public long getEntry() {
		return entry;
	}

	public void setEntry(long entry) {
		this.entry = entry;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}
