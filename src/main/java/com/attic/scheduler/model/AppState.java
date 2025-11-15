package com.attic.scheduler.model;

import java.util.List;

public class AppState {

    private int version = 1;
    private List<Issue> issues;
    private List<User> users;

    public AppState() {}

    public AppState(List<Issue> issues, List<User> users) {
        this.issues = issues;
        this.users = users;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public List<Issue> getIssues() {
        return issues;
    }

    public void setIssues(List<Issue> issues) {
        this.issues = issues;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }
}
