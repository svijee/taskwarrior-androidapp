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

package org.svij.taskwarriorapp.data;

import java.util.Date;
import java.util.UUID;

import android.text.TextUtils;

/*
 * A Task represents a taskwarrior task.
 *
 * @author Sujeevan Vijayakumaran <mail@svij.org>
 */
public class Task {

	public Task() {
		super();
	}

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
	private String tags;

	private float urgency;

	private boolean active;
	private boolean blocked;

	private static final float epsilon = 0.000001f;
	private float urgencyPriorityCoefficient = 6.0f;
	private float urgencyProjectCoefficient = 1.0f;
	private float urgencyActiveCoefficient = 4.0f;
	private float urgencyScheduledCoefficient = 5.0f;
	private float urgencyWaitingCoefficient = -3.0f;
	private float urgencyBlockedCoefficient = -5.0f;
	private float urgencyAnnotationsCoefficient = 1.0f;
	private float urgencyTagsCoefficient = 1.0f;
	private float urgencyNextCoefficient = 15.0f;
	private float urgencyDueCoefficient = 12.0f;
	private float urgencyBlockingCoefficient = 8.0f;
	private float urgencyAgeCoefficient = 2.0f;

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
		return uuid + ".) " + description + " – Entry: " + entry
				+ " – Status: " + status + " – Due: " + duedate + " – Project:"
				+ project + "– Priority: " + priority + "– End:" + end;
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

	public void setActive(boolean active) {
		this.active = active;
	}

	public boolean getActive() {
		return active;
	}

	public boolean isBlocked() {
		return blocked;
	}

	public void setBlocked(boolean blocked) {
		this.blocked = blocked;
	}

	public float getUrgency() {
		return urgency;
	}

	public void setUrgency(float urgency) {
		this.urgency = urgency;
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

	public float urgency_c() {
		float value = 0.0f;

		value += Math.abs(urgencyPriorityCoefficient)		> epsilon ? (urgency_priority()		* urgencyPriorityCoefficient)		: 0.0;
		value += Math.abs(urgencyProjectCoefficient)		> epsilon ? (urgency_project()		* urgencyProjectCoefficient)		: 0.0;
		value += Math.abs(urgencyActiveCoefficient)			> epsilon ? (urgency_active()		* urgencyActiveCoefficient)			: 0.0;
		value += Math.abs(urgencyScheduledCoefficient)		> epsilon ? (urgency_scheduled()	* urgencyScheduledCoefficient)		: 0.0;
		value += Math.abs(urgencyWaitingCoefficient)		> epsilon ? (urgency_waiting()		* urgencyWaitingCoefficient)		: 0.0;
		value += Math.abs(urgencyBlockedCoefficient)		> epsilon ? (urgency_blocked()		* urgencyBlockedCoefficient)		: 0.0;
		value += Math.abs(urgencyAnnotationsCoefficient)	> epsilon ? (urgency_annotations()	* urgencyAnnotationsCoefficient)	: 0.0;
		value += Math.abs(urgencyTagsCoefficient)			> epsilon ? (urgency_tags()			* urgencyTagsCoefficient)			: 0.0;
		value += Math.abs(urgencyNextCoefficient)			> epsilon ? (urgency_next()			* urgencyNextCoefficient)			: 0.0;
		value += Math.abs(urgencyDueCoefficient)			> epsilon ? (urgency_due()			* urgencyDueCoefficient)			: 0.0;
		value += Math.abs(urgencyBlockingCoefficient)		> epsilon ? (urgency_blocking()		* urgencyBlockingCoefficient)		: 0.0;
		value += Math.abs(urgencyAgeCoefficient)			> epsilon ? (urgency_age()			* urgencyAgeCoefficient)			: 0.0;

		setUrgency(value);
		return value;
	}

	private float urgency_priority() {
		String value = getPriority();

		if (value.equals("High"))
			return 1.0f;
		else if (value.equals("Middle"))
			return 0.65f;
		else if (value.equals("Low"))
			return 0.3f;
		else
			return 0.0f;
	}

	private float urgency_project() {
		if (TextUtils.isEmpty(project)) {
			return 0.0f;
		} else {
			return 1.0f;
		}
	}

	private float urgency_active() {
		if (active) {
			return 1.0f;
		} else {
			return 0.0f;
		}
	}

	private float urgency_scheduled() {
		// TODO: implement scheduled
		// Empty!
		return 0.0f;
	}

	private float urgency_waiting() {
		// TODO: implement waiting
		// Empty!
		return 0.0f;
	}

	private float urgency_blocked() {
		if (isBlocked()) {
			return 1.0f;
		} else {
			return 0.0f;
		}
	}

	private float urgency_annotations() {
		// TODO: implement annotations
		// Empty!
		return 0.0f;
	}

	private float urgency_tags() {
		// TODO: implement tags
		// Empty!
		return 0.0f;
	}

	private float urgency_next() {
		// TODO: implement next
		// Empty!
		return 0.0f;
	}

	private float urgency_due() {
		if (duedate.getTime() != 0) {
			long now = System.currentTimeMillis() / 1000;
			long due = duedate.getTime() / 1000;
			long days_overdue = (now - due) / 86400;

			if (days_overdue >= 7) {
				return 1.0f; // 7 days ago
			} else if (days_overdue >= 6) {
				return 0.96f;
			} else if (days_overdue >= 5) {
				return 0.92f;
			} else if (days_overdue >= 4) {
						return 0.88f;
			} else if (days_overdue >= 3) {
						return 0.84f;
			} else if (days_overdue >= 2) {
				return 0.80f;
			} else if (days_overdue >= 1) {
				return 0.76f;
			} else if (days_overdue >= 0) {
				return 0.72f;
			} else if (days_overdue >= -1) {
				return 0.68f;
			} else if (days_overdue >= -2) {
				return 0.64f;
			} else if (days_overdue >= -3) {
				return 0.60f;
			} else if (days_overdue >= -4) {
				return 0.56f;
			} else if (days_overdue >= -5) {
				return 0.52f;
			} else if (days_overdue >= -6) {
				return 0.48f;
			} else if (days_overdue >= -7) {
				return 0.44f;
			} else if (days_overdue >= -8) {
				return 0.40f;
			} else if (days_overdue >= -9) {
				return 0.36f;
			} else if (days_overdue >= -10) {
				return 0.32f;
			} else if (days_overdue >= -11) {
				return 0.28f;
			} else if (days_overdue >= -12) {
				return 0.24f;
			} else if (days_overdue >= -13) {
				return 0.20f;
			} else {
				return 0.16f; // two weeks from now
			}
		}
		return 0.0f;
	}

	private float urgency_age() {
		long now = System.currentTimeMillis() / 1000;
		int  age = (int) ((now - entry) / 86400);
		float max = 365f;

		if (max == 0 || age > max) {
			return 1.0f;
		}
		return (1.0f * age/max);
	}

	private float urgency_blocking() {
		if (isBlocked()) {
			return 1.0f;
		} else {
			return 0.0f;
		}
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}
}
