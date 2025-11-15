package com.attic.scheduler.view;

public class ControlEvent {
    private boolean isUserListRequested;
    public ControlEvent (Object source, boolean isUserListRequested) {
        this.isUserListRequested = isUserListRequested;
    }

    public boolean getIsUserListRequested() {
        return isUserListRequested;
    }
}
