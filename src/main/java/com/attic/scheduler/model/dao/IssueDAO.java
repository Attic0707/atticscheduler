package com.attic.scheduler.model.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.attic.scheduler.model.Assignee;
import com.attic.scheduler.model.Issue;
import com.attic.scheduler.model.Priority;
import com.attic.scheduler.model.Type;
import com.attic.scheduler.db.DatabaseConnection;

public class IssueDAO {

    private final DatabaseConnection dbConn = DatabaseConnection.getInstance();

    public List<Issue> findAll() throws SQLException {
        List<Issue> result = new ArrayList<>();

        String sql = "SELECT id, issue_name, stage_id, status, description, due_date, assignee, type, priority, difficulty FROM issues ORDER BY id";

        try (Connection conn = dbConn.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Issue issue = mapRowToIssue(rs);
                result.add(issue);
            }
        }

        return result;
    }

    public Issue insert(Issue issue) throws SQLException {
        String sql =
            "INSERT INTO issues " +
            "(issue_name, stage_id, status, description, due_date, assignee, type, priority, difficulty) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) " +
            "RETURNING id";

        try (Connection conn = dbConn.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, issue.getIssueName());
            ps.setInt(2, issue.getStageId());
            ps.setString(3, issue.getStatus());
            ps.setString(4, issue.getDesc());
            ps.setString(5, issue.getDate());
            ps.setString(6, issue.getAssignee() != null ? issue.getAssignee().name() : null);
            ps.setString(7, issue.getType() != null ? issue.getType().name() : null);
            ps.setString(8, issue.getPriority() != null ? issue.getPriority().name() : null);
            ps.setInt(9, issue.getDifficulty());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int generatedId = rs.getInt(1);
                    issue.setId(generatedId);
                }
            }
        }
        return issue;
    }

    // HELPER: map a ResultSet row to Issue
    private Issue mapRowToIssue(ResultSet rs) throws SQLException {
        Issue issue = new Issue();

        issue.setId(rs.getInt("id"));
        issue.setIssueName(rs.getString("issue_name"));
        issue.setStageId(rs.getInt("stage_id"));
        issue.setStatus(rs.getString("status"));
        issue.setDesc(rs.getString("description"));
        issue.setDate(rs.getString("due_date"));

        String assigneeStr = rs.getString("assignee");
        String typeStr     = rs.getString("type");
        String priorityStr = rs.getString("priority");

        if (assigneeStr != null) {
            issue.setAssignee(Assignee.valueOf(assigneeStr));
        }
        if (typeStr != null) {
            issue.setType(Type.valueOf(typeStr));
        }
        if (priorityStr != null) {
            issue.setPriority(Priority.valueOf(priorityStr));
        }

        issue.setDifficulty(rs.getInt("difficulty"));

        return issue;
    }
}
