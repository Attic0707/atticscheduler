package com.attic.scheduler.view;

public class TalkerEvent {
	private String message;
	private boolean redirect;

	public TalkerEvent(Object source, String message, boolean redirect) {
		this.message = message;
		this.redirect = redirect;
	}
	public String getMessage() {
		return message;
	}
	public boolean getRedirect() {
		return redirect;
	}
}
