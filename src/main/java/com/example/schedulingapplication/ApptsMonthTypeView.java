package com.example.schedulingapplication;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class ApptsMonthTypeView {
    private String month;
    private int year;
    private String type;
    private int amount;

    /**
     * constructor for appointments by month report object
     * @param month
     * @param year
     * @param amount number of appointments in the month of year
     */
    public ApptsMonthTypeView(String month, int year, String type, int amount) {
        this.month = month;
        this.year = year;
        this.type = type;
        this.amount = amount;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getType() {
        return type;
    }

    /**
     *
     * @return observablelist for reports of appointments by month
     */
    public static ObservableList<ApptsMonthTypeView> getAllApptsByMonthAndType() {
        ObservableList<ApptsMonthTypeView> appts = FXCollections.observableArrayList();
        try {
            Connection connection = Driver.getConnection();
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT monthname(Start) as month, year(Start), type, COUNT(*) as amount FROM appointments GROUP BY monthname(Start), year(Start), type;");
            while(rs.next()) {
                appts.add(new ApptsMonthTypeView(rs.getString(1), rs.getInt(2), rs.getString(3), rs.getInt(4)));
            }
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return appts;
    }
}
