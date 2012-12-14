package org.svij.taskwarriorapp.data;

import java.util.Date;
import java.util.UUID;

/*
 * A Task represents a taskwarrior task.
 *
 * @author Sujeevan Vijayakumaran <mail@svij.org>
 */
public class Task {

	/*
	 * Unique identifier for a task. Will be replaced by a uuid in the future.
	 */
	private UUID uuid;

	/*
	 * Description of a task
	 */
	private String description;

	/*
	 * A tasks due date
	 */
	private Date duedate;

	/*
	 * Entry timestamp – This is automatically generated when creating a task
	 */
	private long entry;

	/*
	 * Priority of a task
	 */
	private String priority;

	/*
	 * Priority ID – needed for the spinner in Edit-Mode
	 */
	private int priorityID;

	/*
	 * Project of a task
	 */
	private String project;

	/*
	 * Status of a task It can be "pending", "completed" or "deleted"
	 */
	private String status;

	/*
	 * End timestamp This is automatically generated when marking a task as "done"
	 *
	 */
	private long end;

	public UUID getUuid() {
		return uuid;
	}

	public void setId(UUID uuid) {
		this.uuid = uuid;
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
		return uuid + ".) " + description + " – Entry: " + entry + " – Status: "
				+ status + " – Due: " + duedate + " – Project:" + project
				+ "– Priority: " + priority + "– End:" + end;
	}

	public Date getDuedate() {
		return duedate;
	}

	public void setDuedate(Date duedate) {
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

	public long getEnd() {
		return end;
	}

	public void setEnd(long end) {
		this.end = end;
	}

	public int getPriorityID() {
		if (priority.equals("no priority")) {
			priorityID = 0;
		} else if (priority.equals("High")) {
			priorityID = 1;
		} else if (priority.equals("Middle")) {
			priorityID = 2;
		} else if (priority.equals("Low")) {
			priorityID = 3;
		}

		return priorityID;
	}

	public void setPriorityID(int priorityID) {
		this.priorityID = priorityID;
	}
}
