package com.attic.scheduler.controller;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.attic.scheduler.model.Assignee;
import com.attic.scheduler.model.Database;
import com.attic.scheduler.model.Issue;
import com.attic.scheduler.model.Priority;
import com.attic.scheduler.model.Type;
import com.attic.scheduler.model.User;
import com.attic.scheduler.model.dao.IssueDAO;
import com.attic.scheduler.model.dao.UserDAO;
import com.attic.scheduler.view.FormEvent;
import com.attic.scheduler.view.SignUpEvent;

public class Controller {
	Database db = new Database();
	IssueDAO issueDAO = new IssueDAO();
	UserDAO userDAO = new UserDAO();

	public List<Issue> getIssueList() {
		return db.getIssues();
	}

	public List<User> getUserList() {
		return db.getUsers();
	}

	public ArrayList<String> getUserNameList() {
		return db.getUserNameList();
	}

	public void setUserDetails(ArrayList<String> userDetails) {
		db.setUserDetails(userDetails);
	}

	public ArrayList<String> getUserDetails() {
		return db.getUserDetails();
	}

	public int getUserCount() {
		return db.getUsers().size();
	}

	public int getIssueCount() {
		return db.getIssues().size();
	}

	public void putToUserMap(String userName, char[] password) {
		db.putToUserMap(userName, password);
	}

	public void putToIssueMap(String issueId, String issueName) {
		db.putToIssueMap(issueId, issueName);
	}

	public void removeFromIssueMap(String issueId) {
		db.removeFromIssueMap(issueId);
	}

	public String getCurrentUser() {
		return db.getLoggedInUser();
	}

	public boolean setUserLoginHistoryMap(String userName, String loginHistory) {
		return db.setUserLoginHistoryMap(userName, loginHistory);
	}

	public void updateUserDetails(String userName, String fieldName, String newValue) {
		db.updateUserDetails(userName, fieldName, newValue);
	}

	public HashMap<String, ArrayList<String>> getUserLoginHistoryMap(String userName) {
		return db.getUserLoginHistoryMap(userName);
	}

	public void setCurrentUser(String userName) {
		db.setLoggedInUser(userName);
	}

	public void logOutCurrentUser() {
		db.logOutUser();
	}

	public void deleteRequest(boolean confirm, char[] confirmPass) {
		db.deleteAccount();
	}

	public void logHistory(String loginDate) {
		// db.setLoginHistoryItem();
	}

	public Boolean issueValidation(String issueId, String issueName) {
		if (!(db.getIssueMap().isEmpty()) && (db.getIssueMap().containsKey(issueId))
				&& (db.getIssueMap().get(issueId)).equals(issueName)) {
			return true;
		}
		return false;
	}

	public Boolean checkForValid(String userName, char[] pass) {
		if (!(db.getUserMap().isEmpty()) && (db.getUserMap().containsKey(userName))
				&& Arrays.equals(db.getUserMap().get(userName), pass)) {
			return true;
		} else {
			return false;
		}
	};

	public ArrayList<String> getCountryList() {
		return db.getCountries();
	};

	public ArrayList<String> getUserDetails(String userName) {
		ArrayList<String> userDetails = new ArrayList<String>();
		for (User u : getUserList()) {
			if (u.getUserName().equals(userName)) {
				userDetails.add(u.getFirstName());
				userDetails.add(u.getLastName());
				userDetails.add(u.getUserName());
				userDetails.add(String.valueOf(u.getPassword()));
				userDetails.add(u.getStreet());
				userDetails.add(String.valueOf(u.getPostalCode()));
				userDetails.add(u.getCity());
				userDetails.add(u.getState());
				userDetails.add(u.getCountry());
				userDetails.add(u.getBio());
				userDetails.add(String.valueOf(u.getPhone()));
				userDetails.add(u.getEmail());
				userDetails.add(u.getCompany());
				userDetails.add(u.getWebsite());
				userDetails.add(u.getSocial());
				userDetails.add(u.getMemberSince());
			}

		}
		return userDetails;
	}

	public void insertUserRecord(SignUpEvent sue) {
		String firstName = sue.getFirstName();
		String lastName = sue.getLastName();
		String userName = sue.getUserName();
		char[] password = sue.getPassword();
		String street = sue.getStreet();
		int postalCode = sue.getPostalCode();
		String city = sue.getCity();
		String state = sue.getState();
		String country = sue.getCountry();
		String bio = sue.getBio();
		String email = sue.getEmail();
		String company = sue.getCompany();
		String website = sue.getWebsite();
		String social = sue.getSocial();
		int phone = sue.getPhone();
		String memberSince = sue.getMemberSince();
		User user = new User(firstName, lastName, userName, password, street, postalCode, city, state, country, bio,
				phone, email, company, website, social, memberSince);
		// Check if the user name already exists in the DB
		if (db.getUserNameList().size() != 0 && db.getUserNameList().contains(userName)) { // legacy
			System.out.println("Username in DB: ");
		} else {
			db.addUser(user); // legacy
			System.out.println("DB size: " + db.getUsers().size()); // legacy
			try {
				userDAO.insertUser(user);
				getAllUsers();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public void addIssue(FormEvent fv) {
		String issueName = fv.getIssueName();
		String issueStatus = fv.getStatus();
		int stageId = fv.getStageId();
		String issueDesc = fv.getDesc();
		String dueDate = fv.getDate();
		String issueAssignee = fv.getAssignee();
		String issueType = fv.getType();
		String issuePriority = fv.getPriority();
		int issueDifficulty = fv.getDifficulty();
		String subTaskId = fv.getSubTaskId();

		Assignee assignee = null;
		if (issueAssignee.equals("Admin")) {
			assignee = Assignee.Admin;
		} else if (issueAssignee.equals("User 1")) {
			assignee = Assignee.User1;
		} else if (issueAssignee.equals("User 2")) {
			assignee = Assignee.User2;
		} else if (issueAssignee.equals("User 3")) {
			assignee = Assignee.User3;
		} else if (issueAssignee.equals("User 4")) {
			assignee = Assignee.User4;
		} else if (issueAssignee.equals("User 5")) {
			assignee = Assignee.User5;
		}

		Type type = null;
		if (issueType.equals("Bug")) {
			type = Type.Bug;
		} else if (issueType.equals("Feature")) {
			type = Type.Feature;
		} else if (issueType.equals("Story")) {
			type = Type.Story;
		}

		Priority priority = null;
		if (issuePriority.equals("Low")) {
			priority = Priority.Low;
		} else if (issuePriority.equals("Medium")) {
			priority = Priority.Medium;
		} else if (issuePriority.equals("High")) {
			priority = Priority.High;
		} else if (issuePriority.equals("Urgent")) {
			priority = Priority.Urgent;
		}
		Issue issue = Issue.create(
				issueName,
				stageId,
				issueStatus,
				issueDesc,
				dueDate,
				assignee,
				type,
				priority,
				issueDifficulty,
				subTaskId
		);
		db.addIssue(issue); // legacy

		try {
			issueDAO.insert(issue);
			getAllIssues();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void saveToFile(File file) throws IOException {
		db.saveToFile(file);
	}

	public void exportUsers(File file) throws IOException {
		db.exportUsers(file);
	}

	public void loadFromFile(File file) throws IOException {
		db.loadFromFile(file);
	}

	public void loadCSVFile(File file) throws IOException {
		db.loadCSVFile(file);
	}

	public void removeIssue(int index) {
		db.removeIssue(index);
	}

	public void editIssue(int index, Object editedValue) {
		db.editIssue(index, editedValue);
	}

	public void saveIssuesAsJson(File file) throws IOException {
		db.saveIssuesAsJson(file);
	}

	public void loadIssuesFromJson(File file) throws IOException {
		db.loadIssuesFromJson(file);
	}

	public void getAllIssues() {
		List<Issue> allIssues;
		try {
			allIssues = issueDAO.findAll();
			System.out.println("ALL ISSUES: " + allIssues);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void getAllUsers() {
		List<User> allUsers;
		try {
			allUsers = userDAO.getAllUsers();
			System.out.println("ALL USERS: " + allUsers);

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}