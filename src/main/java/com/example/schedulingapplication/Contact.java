package com.example.schedulingapplication;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

public class Contact {

    /**
     *
     * @param id contact id
     * @return contact object if contact with said id exists, null if not
     */
    public static Contact findByID(int id) {
        try {
            Connection connection = Driver.getConnection();
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM contacts WHERE Contact_ID =?;");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                return new Contact(rs.getInt("Contact_ID"), rs.getString("Contact_Name"), rs.getString("Email"));
            }
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     *
     * @return observablelist of all contacts
     */
    public static ObservableList<Contact> getAllContacts() {
        ObservableList<Contact> contacts = FXCollections.observableArrayList();
        try {
            Connection connection = Driver.getConnection();
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM contacts;");
            while(rs.next()) {
                contacts.add(new Contact(rs.getInt("Contact_ID"), rs.getString("Contact_Name"), rs.getString("Email")));
            }
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return contacts;
    }

    private int id;
    private String name;
    private String email;

    /**
     * constructor for contact object
     *
     * @param id
     * @param name
     * @param email
     */
    public Contact(int id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
