package com.attic.scheduler.view;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LogInEvent {
	private String userName;
	private char[] password;
	private boolean granted;

	public LogInEvent(Object source, String userName, char[] password) {
		this.userName = userName;
		this.password = password;
		this.granted = false;
	}

	public LogInEvent(Object source, boolean granted) {
		this.granted = granted;
	}

	public String getUserName() {
		return userName;
	}
	
	public char[] getPassword() {
		return password;
	}

	public void setGranted() {
		granted = true;
	}

	public boolean getGranted() {
		return granted;
	}

	public void loggedOut() {
		granted = false;
	}

	public String getLoginDateTime() {
		LocalDateTime now = LocalDateTime.now();
		DateTimeFormatter dtf = DateTimeFormatter.ISO_DATE_TIME;
		String dateFormatted = dtf.format(now);
		return dateFormatted;
	}
}