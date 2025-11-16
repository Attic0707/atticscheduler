package com.attic.scheduler.model.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.attic.scheduler.model.User;
import com.attic.scheduler.db.DatabaseConnection;

public class UserDAO {
    private final DatabaseConnection dbConn = DatabaseConnection.getInstance();

    public List<User> getAllUsers() throws SQLException {
        List<User> result = new ArrayList<>();

        String sql = "SELECT id, username, password, first_name, last_name, street, postal_code, city, state, country, bio, phone, email, company, website, social, member_since FROM users ORDER BY id";

        try (Connection conn = dbConn.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                User user = mapRowToUser(rs);
                result.add(user);
            }
        }
        return result;
    }

    public User insertUser(User user) throws SQLException {

        String sql = "INSERT INTO users " + 
            "(username, password, first_name, last_name, street, postal_code, city, state, country, " +
            "bio, phone, email, company) " + 
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
            "RETURNING id";

        try (Connection conn = dbConn.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1,  user.getUserName());
            ps.setString(2,  String.valueOf(user.getPassword()));
            ps.setString(3,  user.getFirstName());
            ps.setString(4,  user.getLastName());
            ps.setString(5,  user.getStreet());
            ps.setString(6,  String.valueOf(user.getPostalCode()));
            ps.setString(7,  user.getCity());
            ps.setString(8,  user.getState());
            ps.setString(9,  user.getCountry());
            ps.setString(10, user.getBio());
            ps.setString(11, String.valueOf(user.getPhone()));
            ps.setString(12, user.getEmail());
            ps.setString(13, user.getCompany());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int generatedId = rs.getInt(1);
                    user.setUserId(generatedId);
                }
            }
        }
        return user;
    }


    // HELPER: map a ResultSet row to User
    private User mapRowToUser(ResultSet rs) throws SQLException {
        User user = new User();

        user.setUserId(rs.getInt("id"));

        return user;
    }
}