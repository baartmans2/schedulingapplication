package com.example.schedulingapplication;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

public class FirstLevelDivision {

    /**
     * searches for a first level division by id
     *
     * @param id division id
     * @return FirstLevelDivision object if division with said ID exists, otherwise null
     */
    public static FirstLevelDivision findByID(int id) {
        try {
            Connection connection = Driver.getConnection();
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM first_level_divisions WHERE Division_ID = " + id + ";");
            while (rs.next()) {
                return new FirstLevelDivision(rs.getInt(1), rs.getString(2), rs.getDate(3), rs.getString(4), rs.getDate(5), rs.getString(6), rs.getInt(7));
            }
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * finds a division ID by the division Name
     *
     * @param division name of the division
     * @return division ID if the division with said name exists, otherwise -1
     */
    public static int findIDByName(String division) {
        try {
            Connection connection = Driver.getConnection();
            PreparedStatement ps = connection.prepareStatement("SELECT Division_ID FROM first_level_divisions WHERE Division =?;");
            ps.setString(1, division);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                return rs.getInt(1);
            }
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * gets all first level divisions
     *
     * @return observablelist of all FirstLevelDivisions
     */
    public static ObservableList<FirstLevelDivision> getAllDivisions() {
        ObservableList<FirstLevelDivision> divisions = FXCollections.observableArrayList();
        try {
            Connection connection = Driver.getConnection();
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM first_level_divisions;");
            while(rs.next()) {
                divisions.add(new FirstLevelDivision(rs.getInt(1), rs.getString(2), rs.getDate(3), rs.getString(4), rs.getDate(5), rs.getString(6), rs.getInt(7)));
            }
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return divisions;
    }

    /**
     * Gets all first level divisions in a country
     *
     * @param country the country name
     * @return ObservableList of all the FirstLevelDivisions in the country
     */
    public static ObservableList<String> getDivisionsByCountry(String country) {
        ObservableList<String> divisions = FXCollections.observableArrayList();
        try {
            Connection connection = Driver.getConnection();
            PreparedStatement ps = connection.prepareStatement("SELECT Division FROM first_level_divisions INNER JOIN countries ON first_level_divisions.Country_ID = countries.Country_ID WHERE Country =?;");
            ps.setString(1, country);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                divisions.add(rs.getString(1));
            }
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return divisions;
    }

    private int id;
    private String name;
    private Date createDate;
    private String createdBy;
    private Date lastUpdate;
    private String lastUpdatedBy;
    private int countryID;

    /**
     * constructor for FirstLevelDivision object
     *
     * @param id
     * @param name
     * @param createDate
     * @param createdBy
     * @param lastUpdate
     * @param lastUpdatedBy
     * @param countryID
     */
    public FirstLevelDivision(int id, String name, Date createDate, String createdBy, Date lastUpdate, String lastUpdatedBy, int countryID) {
        this.id = id;
        this.name = name;
        this.createDate = createDate;
        this.createdBy = createdBy;
        this.lastUpdate = lastUpdate;
        this.lastUpdatedBy = lastUpdatedBy;
        this.countryID = countryID;
    }

    public int getId() {
        return id;
    }
    public int getCountryID() {
        return countryID;
    }

}
