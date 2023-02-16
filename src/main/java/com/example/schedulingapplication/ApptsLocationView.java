package com.example.schedulingapplication;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class ApptsLocationView {
    private String location;
    private int amount;

    /**
     * constructor for appointments by location object
     * @param location
     * @param amount number of appointments at the location
     */
    public ApptsLocationView(String location, int amount) {
        this.location = location;
        this.amount = amount;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    /**
     *
     * @return observablelist for reports of appointments by location
     */
    public static ObservableList<ApptsLocationView> getAllApptsByLocation() {
        ObservableList<ApptsLocationView> appts = FXCollections.observableArrayList();
        try {
            Connection connection = Driver.getConnection();
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("Select Location, Count(*) as amount FROM appointments GROUP BY Location;");
            while(rs.next()) {
                appts.add(new ApptsLocationView(rs.getString(1), rs.getInt(2)));
            }
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return appts;
    }
}
