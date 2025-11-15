package com.attic.scheduler.model.io;

import com.attic.scheduler.model.Issue;
import com.attic.scheduler.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class JsonStorage {

    private static final ObjectMapper mapper = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT);

    // ---- ISSUE EXPORT ----
    public static void exportIssues(File file, List<Issue> issues) throws IOException {
        mapper.writeValue(file, issues);
    }

    // ---- ISSUE IMPORT ----
    public static List<Issue> importIssues(File file) throws IOException {
        return List.of(mapper.readValue(file, Issue[].class));
    }

    // ---- USER EXPORT ----
    public static void exportUsers(File file, List<User> users) throws IOException {
        mapper.writeValue(file, users);
    }

    // ---- USER IMPORT ----
    public static List<User> importUsers(File file) throws IOException {
        return List.of(mapper.readValue(file, User[].class));
    }
}
