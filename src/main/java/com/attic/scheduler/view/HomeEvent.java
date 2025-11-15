package com.attic.scheduler.view;

public class HomeEvent {
	private boolean isFormVisible;

	public HomeEvent(Object source, boolean isFormVisible) {
		this.isFormVisible = isFormVisible;
	}

	public boolean getFormVisible() {
		return isFormVisible;
	}
	
}
