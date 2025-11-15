package com.attic.scheduler.view;

public class ToolEvent {
    private String message;
    private boolean redirect;

    public ToolEvent(Object source, String message, boolean redirect) {
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
