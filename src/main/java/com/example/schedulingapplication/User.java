package com.example.schedulingapplication;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;

public class User {

    /**
     * Validates username/password combo
     *
     * @param username the username
     * @param password the password
     * @return User object of the logged-in user if successful, null if not successful.
     */
    public static User login(String username, String password) {
        User user = null;
        try {
            Connection connection = Driver.getConnection();
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM users WHERE User_Name =?;");
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                user = new User(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getDate(4), rs.getString(5), rs.getDate(6), rs.getString(7));
            }
            connection.close();
            if (user != null) {
                if (user.getPassword().equals(password)) {
                    return user;
                } else {
                    return null;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Finds a user by the user id
     *
     * @param id id of the user
     * @return User if a user with said id exists, null if not
     */
    public static User findByID(int id) {
        try {
            Connection connection = Driver.getConnection();
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM users WHERE User_ID =?;");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                return new User(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getDate(4), rs.getString(5), rs.getDate(6), rs.getString(7));
            }
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * gets all users in the database
     *
     * @return list of all users in the database
     */
    public static ObservableList<User> getAllUsers() {
        ObservableList<User> users = FXCollections.observableArrayList();
        try {
            Connection connection = Driver.getConnection();
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM users;");
            while (rs.next()) {
                users.add(new User(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getDate(4), rs.getString(5), rs.getDate(6), rs.getString(7)));
            }
            return  users;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private int id;
    private String username;
    private String password;
    private Date createDate;
    private String createdBy;
    private Date lastUpdate;
    private String lastUpdatedBy;


    /**
     * constructor for user object
     *
     * @param id
     * @param username
     * @param password
     * @param createDate
     * @param createdBy
     * @param lastUpdate
     * @param lastUpdatedBy
     */
    public User(int id, String username, String password, Date createDate, String createdBy, Date lastUpdate, String lastUpdatedBy) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.createDate = createDate;
        this.createdBy = createdBy;
        this.lastUpdate = lastUpdate;
        this.lastUpdatedBy = lastUpdatedBy;
    }

    public int getId() {
        return id;
    }
    public String getUsername() {
        return username;
    }
    public String getPassword() {
        return password;
    }
}
