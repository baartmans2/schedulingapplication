package com.example.schedulingapplication;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;

public class Country {

    /**
     * finds a country by its id
     *
     * @param id country id
     * @return country object if it exists, null if not
     */
    public static Country findByID(int id) {
        try {
            Connection connection = Driver.getConnection();
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM countries WHERE Country_ID = " + id + ";");
            while(rs.next()) {
                return new Country(rs.getInt(1), rs.getString(2), rs.getDate(3), rs.getString(4), rs.getDate(5), rs.getString(6));
            }
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @return observablelist of all countries in the db
     */
    public static ObservableList<String> getAllCountries() {
        ObservableList<String> countries = FXCollections.observableArrayList();
        try {
            Connection connection = Driver.getConnection();
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT Country FROM countries;");
            while(rs.next()) {
                countries.add(rs.getString(1));
            }
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return countries;
    }

    private int id;
    private String name;
    private Date createDate;
    private String createdBy;
    private Date lastUpdate;
    private String lastUpdatedBy;

    /**
     * constructor for country object
     * @param id
     * @param name
     * @param createDate
     * @param createdBy
     * @param lastUpdate
     * @param lastUpdatedBy
     */
    public Country(int id, String name, Date createDate, String createdBy, Date lastUpdate, String lastUpdatedBy) {
        this.id = id;
        this.name = name;
        this.createDate = createDate;
        this.createdBy = createdBy;
        this.lastUpdate = lastUpdate;
        this.lastUpdatedBy = lastUpdatedBy;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
