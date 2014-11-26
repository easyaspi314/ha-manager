//Copyright (c) 2014 Nico Alt GPLv2 or later

package de.nico.ha_manager.database;

public class Entry {

	private long id;
	private String urgent;
	private String subject;
	private String homework;
	private String until;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getUrgent() {
		return urgent;
	}

	public void setUrgent(String urgent) {
		this.urgent = urgent;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getHomework() {
		return homework;
	}

	public void setHomework(String homework) {
		this.homework = homework;
	}

	public String getUntil() {
		return until;
	}

	public void setUntil(String until) {
		this.until = until;
	}

	@Override
	public String toString() {
		/*
		 * This is the output of the homework in the ListView, for example:
		 * 
		 * Urgent! French (Thursday, 01.01.): Learn for test
		 */
		return String
				.format("%s%s (%s):\n%s", urgent, subject, until, homework);
	}

}
