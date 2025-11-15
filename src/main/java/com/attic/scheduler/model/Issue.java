package com.attic.scheduler.model;

import java.io.Serializable;

public class Issue implements Serializable {
    private static final long serialVersionUID = 1L;

    private static int count = 0;

    private int id;
    private String issueName;
    private int stageId;
    private String status;
    private String desc;
    private String date;
    private Assignee assignee;
    private Type type;
    private Priority priority;
    private int difficulty;
    private String subTaskId;

    // *** Required by Jackson ***
    public Issue() {
        // Jackson will call this and then use setters / fields
    }

    // *** Factory for your own code ***
    public static Issue create(
            String issueName,
            int stageId,
            String status,
            String desc,
            String date,
            Assignee assignee,
            Type type,
            Priority priority,
            int difficulty,
            String subTaskId
    ) {
        Issue issue = new Issue();
        issue.id = count++;
        issue.issueName = issueName;
        issue.stageId = stageId;
        issue.status = status;
        issue.desc = desc;
        issue.date = date;
        issue.assignee = assignee;
        issue.type = type;
        issue.priority = priority;
        issue.difficulty = difficulty;
        issue.subTaskId = subTaskId;
        return issue;
    }

    public int getId() {
        return id;
    }

    public String getIssueName() {
        return issueName;
    }

    public void setIssueName(String issueName) {
        this.issueName = issueName;
    }

    public int getStageId() {
        return stageId;
    }

    public void setStageId(int stageId) {
        this.stageId = stageId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Assignee getAssignee() {
        return assignee;
    }

    public void setAssignee(Assignee assignee) {
        this.assignee = assignee;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int diff) {
        this.difficulty = diff;
    }

    public String getSubTaskId() {
        return subTaskId;
    }

    public void setSubTaskId(String subTaskId) {
        this.subTaskId = subTaskId;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Name : ")
            .append(issueName).append("\n")
            .append("Description: ").append(desc).append("\n")
            .append("Due Date: ").append(date).append("\n")
            .append("Assignee: ").append(assignee).append("\n")
            .append("Priority: ").append(priority).append("\n")
            .append("Difficulty: ").append(difficulty).append("\n")
            .append("Subtask (if any) :").append(subTaskId).append("\n");
        return sb.toString();
    }
}
