package org.svij.taskwarriorapp;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.UUID;

import android.text.TextUtils;
import android.text.format.DateUtils;

/*
 * A Task represents a taskwarrior task.
 *
 * @author Sujeevan Vijayakumaran <mail@svij.org>
 */
public class Task {

	private static String completed = "completed";
	private static String deleted = "deleted";

	public Task() {
		super();

		urgencyValue = 0.0f;
		setRecalcUrgency(true);
		isBlocked = false;
		isBlocking = false;

        this.uuid = UUID.randomUUID();
        this.entry = new Date();
        this.status = "pending";
	}

	private String status;
	private UUID uuid;
	private Date entry;
	private String description;
	private Date start;
	private Date end;
	private Date due;
	private Date until;
	private Date wait;
	private Date modified;
	private String recur;
	private String mask;
	private String imask;
	private UUID parent;
	private ArrayList<String> annotations;
	private String project;
	private ArrayList<String> tags;
	private String priority;
	private int priorityID;
	private ArrayList<UUID> depends;
	private boolean active;

	private boolean isBlocked;
	private boolean isBlocking;

	private float urgencyValue;
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

	private boolean recalcUrgency;

	public UUID getUuid() {
		return uuid;
	}

	public void setUUID(UUID uuid) {
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

	public Date getDue() {
		return due;
	}

	public void setDue(Date due) {
		this.due = due;
	}

	public Date getEntry() {
		return entry;
	}

	public void setEntry(Date entry) {
		this.entry = entry;
		setRecalcUrgency(true);
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
		setRecalcUrgency(true);
	}

	public Date getEnd() {
		return end;
	}

	public void setEnd(Date end) {
		this.end = end;
		setRecalcUrgency(true);
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public boolean getActive() {
		return active;
	}

	public boolean isBlocked() {
		return isBlocked;
	}

	public void setBlocked(boolean blocked) {
		this.isBlocked = blocked;
	}

	public float getUrgency() {
		return urgencyValue;
	}

	public void setUrgency(float urgency) {
		this.urgencyValue = urgency;
	}

	public int getPriorityID() {
		if (priority == null) {
			priorityID = 0;
		} else {
			if (priority.equals("H")) {
				priorityID = 1;
			} else if (priority.equals("M")) {
				priorityID = 2;
			} else if (priority.equals("L")) {
				priorityID = 3;
			} else {
				priorityID = 0;
			}
		}

		return priorityID;
	}

	public void setPriorityID(int priorityID) {
		this.priorityID = priorityID;
	}

	public float urgency_c() {
		float value = 0.0f;

		value += Math.abs(urgencyPriorityCoefficient)	> epsilon ? (urgencyPriority()		* urgencyPriorityCoefficient)		: 0.0;
		value += Math.abs(urgencyProjectCoefficient)	> epsilon ? (urgencyProject()		* urgencyProjectCoefficient)		: 0.0;
		value += Math.abs(urgencyActiveCoefficient)		> epsilon ? (urgencyActive()		* urgencyActiveCoefficient)			: 0.0;
		value += Math.abs(urgencyScheduledCoefficient)	> epsilon ? (urgencyScheduled()		* urgencyScheduledCoefficient)		: 0.0;
		value += Math.abs(urgencyWaitingCoefficient)	> epsilon ? (urgencyWaiting() 		* urgencyWaitingCoefficient)		: 0.0;
		value += Math.abs(urgencyBlockedCoefficient)	> epsilon ? (urgencyBlocked() 		* urgencyBlockedCoefficient)		: 0.0;
		value += Math.abs(urgencyAnnotationsCoefficient)> epsilon ? (urgencyAnnotations() 	* urgencyAnnotationsCoefficient)	: 0.0;
		value += Math.abs(urgencyTagsCoefficient) 		> epsilon ? (urgencyTags() 			* urgencyTagsCoefficient)			: 0.0;
		value += Math.abs(urgencyNextCoefficient) 		> epsilon ? (urgencyNext() 			* urgencyNextCoefficient)			: 0.0;
		value += Math.abs(urgencyDueCoefficient) 		> epsilon ? (urgencyDue() 			* urgencyDueCoefficient)			: 0.0;
		value += Math.abs(urgencyBlockingCoefficient) 	> epsilon ? (urgencyBlocking() 		* urgencyBlockingCoefficient)		: 0.0;
		value += Math.abs(urgencyAgeCoefficient) 		> epsilon ? (urgencyAge() 			* urgencyAgeCoefficient)			: 0.0;

		return value;
	}

	public float urgency() {
		if(recalcUrgency) {
			urgencyValue = urgency_c();
			recalcUrgency = false;
		}

		return urgencyValue;
	}

	private float urgencyPriority() {
		String value = getPriority();

		if (value != null) {
			if (value.equals("H"))
				return 1.0f;
			else if (value.equals("M"))
				return 0.65f;
			else if (value.equals("L"))
				return 0.3f;
		}
		return 0.0f;
	}

	private float urgencyProject() {
		if (TextUtils.isEmpty(project)) {
			return 0.0f;
		} else {
			return 1.0f;
		}
	}

	private float urgencyActive() {
		if (active) {
			return 1.0f;
		} else {
			return 0.0f;
		}
	}

    private float urgencyScheduled() {
		// TODO: implement scheduled
		return 0.0f;
	}

	private float urgencyWaiting() {
		if (status == "waiting") {
			return 1.0f;
		}
		return 0.0f;
	}

	private float urgencyBlocked() {
		if (isBlocked()) {
			return 1.0f;
		} else {
			return 0.0f;
		}
	}

	private float urgencyAnnotations() {
		if (annotations == null) {
			return 0.0f;
		} else if (annotations.size() >= 3) {
			return 1.0f;
		} else if (annotations.size() == 2) {
			return 0.9f;
		} else if (annotations.size() == 1) {
			return 0.8f;
		} else {
			return 0.0f;
		}
	}

	private float urgencyTags() {
		switch (getTagCount()) {
		case 0:
			return 0.0f;
		case 1:
			return 0.8f;
		case 2:
			return 0.9f;
		default:
			return 1.0f;
		}
	}

	private float urgencyNext() {
		if (tags != null) {
            return 1.0f;
        } else {
            return 0.0f;
        }
	}

	private float urgencyDue() {
		if (due != null && due.getTime() != 0) {
			long now = System.currentTimeMillis() / 1000;
			long duedate = due.getTime() / 1000;
			long days_overdue = (now - duedate) / 86400;

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

	private float urgencyAge() {
		Date now = new Date(System.currentTimeMillis());
		int age = (int) ((now.getTime() - entry.getTime()) / (1000 * 60 * 60 * 24));
		float max = 365f;

		if (max == 0 || age > max) {
			return 1.0f;
		}

		return (1.0f * age / max);
	}

	private float urgencyBlocking() {
		if (isBlocking()) {
			return 1.0f;
		} else {
			return 0.0f;
		}
	}

	public ArrayList<String> getTags() {
		return tags;
	}

	public void setTags(ArrayList<String> tags) {
		this.tags = tags;
	}

	public void addTags(String tag) {
		this.tags.add(tag);
	}

	public void removeTag(String tag) {
		this.tags.remove(tag);
	}

	public int getTagCount() {
		if (tags != null) {
			return tags.size();
		} else {
			return 0;
		}
	}

	public Date getStart() {
		return start;
	}

	public void setStart(Date start) {
		this.start = start;
		setRecalcUrgency(true);
	}

	public Date getUntil() {
		return until;
	}

	public void setUntil(Date until) {
		this.until = until;
	}

	public Date getWait() {
		return wait;
	}

	public void setWait(Date wait) {
		this.wait = wait;
	}

	public String getRecur() {
		return recur;
	}

	public void setRecur(String recur) {
		this.recur = recur;
	}

	public String getMask() {
		return mask;
	}

	public void setMask(String mask) {
		this.mask = mask;
	}

	public String getImask() {
		return imask;
	}

	public void setImask(String imask) {
		this.imask = imask;
	}

	public UUID getParent() {
		return parent;
	}

	public void setParent(UUID parent) {
		this.parent = parent;
	}

	public ArrayList<String> getAnnotation() {
		return annotations;
	}

	public void setAnnotation(ArrayList<String> annotation) {
		this.annotations = annotation;
	}

	public ArrayList<UUID> getDepends() {
		return depends;
	}

	public void setDepends(ArrayList<UUID> depends) {
		this.depends = depends;
	}

	public void addDependency(UUID depends) {
		this.depends.add(depends);
	}

	public void removeDependency(UUID depends) {
		this.depends.remove(depends);
	}

	public boolean isBlocking() {
		return isBlocking;
	}

	public void setBlocking(boolean isBlocking) {
		this.isBlocking = isBlocking;
	}

	public Date getModified() {
		return modified;
	}

	public void setModified(Date modified) {
		this.modified = modified;
		setRecalcUrgency(true);
	}

	public boolean isRecalcUrgency() {
		return recalcUrgency;
	}

	public void setRecalcUrgency(boolean recalcUrgency) {
		this.recalcUrgency = recalcUrgency;
	}

	public boolean isDue() {
		if (due != null) {
			if (status != completed &&
				status != deleted) {
				if (getDueState(due) == 1) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean isDueToday() {
		if (due != null) {
			if (status != completed &&
				status != deleted) {
				if (getDueState(due) == 2) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean isDueWeek() {
		if (due != null) {
			if (status != completed &&
				status != deleted) {
					Calendar now = Calendar.getInstance();
					Calendar caldue = new GregorianCalendar();
					caldue.setTime(due);

					if (now.get(Calendar.YEAR) == caldue.get(Calendar.YEAR) &&
						now.get(Calendar.WEEK_OF_YEAR) == caldue.get(Calendar.WEEK_OF_YEAR)) {
							return true;
					}
			}
		}
		return false;
	}

	public boolean isDueMonth() {
		if (due != null) {
			if (status != completed &&
				status != deleted) {
					Calendar now = Calendar.getInstance();
					Calendar caldue = new GregorianCalendar();
					caldue.setTime(due);

					if (now.get(Calendar.YEAR) == caldue.get(Calendar.YEAR) &&
						now.get(Calendar.MONTH) == caldue.get(Calendar.MONTH)) {
							return true;
					}
			}
		}
		return false;
	}

	public boolean isDueYear() {
		if (due != null) {
			if (status != completed &&
				status != deleted) {
					Calendar now = Calendar.getInstance();
					Calendar caldue = new GregorianCalendar();
					caldue.setTime(due);

					if (now.get(Calendar.YEAR) == caldue.get(Calendar.YEAR)) {
							return true;
					}
			}
		}
		return false;
	}

	public boolean isOverDue() {
		if (due != null) {
			if (status != completed &&
				status != deleted) {
				if (getDueState(due) == 3) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean hasAnnotations() {
		if (annotations.size() == 0) {
			return true;
		} else {
			return false;
		}
	}

	public void addAnnotation(String description) {
		annotations.add(description);
	}

    private int getDueState(Date due) {
        Date now = new Date();

        if (due.before(now)) {
            return 3;
        }

        if (DateUtils.isToday(due.getTime())) {
            return 2;
        }

        long imminentDay = Calendar.DAY_OF_MONTH + 7 * 86400;

        if (due.getTime() < imminentDay) {
            return 1;
        }

        return 0;
    }

    @Override
    public String toString() {
        return this.getDescription();
    }
}
