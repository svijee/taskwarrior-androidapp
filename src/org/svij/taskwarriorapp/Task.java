package org.svij.taskwarriorapp;

public class Task {
	private long id;
	private String description;
	private String project;
	private String priority;
	
	
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
		return id + ".) " + description;
	}
}
