package com.example.schedulingapplication;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.time.Instant;

public class Customer {

    /**
     * find a customer by id
     *
     * @param id id of the customer
     * @return Customer object if the customer exists, null if not
     */
    public static Customer findByID(int id) {
        try {
            Connection connection = Driver.getConnection();
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM customers WHERE Customer_ID =?;");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                return new Customer(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getTimestamp(6), rs.getString(7), rs.getTimestamp(8), rs.getString(9), rs.getInt(10));
            }
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * gets all of the customers in the database
     *
     * @return observablelist of all customers in db
     */
    public static ObservableList<Customer> getAllCustomers() {
        ObservableList<Customer> customers = FXCollections.observableArrayList();
        try {
            Connection connection = Driver.getConnection();
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT customers.*, first_level_divisions.Division, countries.Country FROM customers INNER JOIN first_level_divisions ON customers.Division_ID = first_level_divisions.Division_ID INNER JOIN countries ON first_level_divisions.Country_ID = countries.Country_ID;");
            while(rs.next()) {
                customers.add(new Customer(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getTimestamp(6), rs.getString(7), rs.getTimestamp(8), rs.getString(9), rs.getInt(10), rs.getString(11), rs.getString(12)));
            }
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return customers;
    }

    /**
     * creates a customer in the database
     * @param customer The customer to add to the database
     */
    public static void createCustomer(Customer customer) {
        try {
            Connection connection = Driver.getConnection();
            PreparedStatement ps = connection.prepareStatement("INSERT INTO customers (Customer_Name, Address, Postal_Code, Phone, Create_Date, Created_By, Last_Update, Last_Updated_By, Division_ID) VALUES (?,?,?,?,CURRENT_TIMESTAMP,?,CURRENT_TIMESTAMP,?,?);");
            ps.setString(1, customer.getName());
            ps.setString(2, customer.getAddress());
            ps.setString(3, customer.getPostalCode());
            ps.setString(4, customer.getPhone());
            ps.setString(5, customer.getCreatedBy());
            ps.setString(6, customer.getLastUpdatedBy());
            ps.setInt(7, customer.getDivisionID());
            ps.execute();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * updates a customer in the database
     * @param customer the customer to update in the database
     */
    public static void updateCustomer(Customer customer) {
        try {
            Connection connection = Driver.getConnection();
            PreparedStatement ps = connection.prepareStatement("UPDATE customers SET Customer_Name=?, Address=?, Postal_Code=?, Phone=?, Last_Update=CURRENT_TIMESTAMP, Last_Updated_By=?, Division_ID=? WHERE Customer_ID=?;");
            ps.setString(1, customer.getName());
            ps.setString(2, customer.getAddress());
            ps.setString(3, customer.getPostalCode());
            ps.setString(4, customer.getPhone());
            ps.setString(5, customer.getLastUpdatedBy());
            ps.setInt(6, customer.getDivisionID());
            ps.setInt(7, customer.getId());
            ps.execute();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * deletes a customer from the database
     *
     * @param id the id of the customer to be deleted
     */
    public static void deleteCustomer(int id) {
        try {
            Connection connection = Driver.getConnection();
            PreparedStatement ps = connection.prepareStatement("DELETE FROM appointments WHERE Customer_ID =?;");
            ps.setInt(1, id);
            ps.execute();
            PreparedStatement ps2 = connection.prepareStatement("DELETE FROM customers WHERE Customer_ID =?;");
            ps2.setInt(1, id);
            ps2.execute();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int id;
    private String name;
    private String address;
    private String postalCode;
    private String phone;
    private Timestamp createDate;
    private String createdBy;
    private Timestamp lastUpdate;
    private String lastUpdatedBy;
    private int divisionID;
    private String divisionName;

    private String country;

    /**
     * constructor for customer object that includes customer's division name
     *
     * @param id
     * @param name
     * @param address
     * @param postalCode
     * @param phone
     * @param createDate
     * @param createdBy
     * @param lastUpdate
     * @param lastUpdatedBy
     * @param divisionID
     * @param divisionName
     */
    public Customer(int id, String name, String address, String postalCode, String phone, Timestamp createDate, String createdBy, Timestamp lastUpdate, String lastUpdatedBy, int divisionID, String divisionName, String country) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.postalCode = postalCode;
        this.phone = phone;
        this.createDate = createDate;
        this.createdBy = createdBy;
        this.lastUpdate = lastUpdate;
        this.lastUpdatedBy = lastUpdatedBy;
        this.divisionID = divisionID;
        this.divisionName = divisionName;
        this.country = country;
    }

    /**
     * constructor for customer object
     *
     * @param id
     * @param name
     * @param address
     * @param postalCode
     * @param phone
     * @param createDate
     * @param createdBy
     * @param lastUpdate
     * @param lastUpdatedBy
     * @param divisionID
     */
    public Customer(int id, String name, String address, String postalCode, String phone, Timestamp createDate, String createdBy, Timestamp lastUpdate, String lastUpdatedBy, int divisionID) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.postalCode = postalCode;
        this.phone = phone;
        this.createDate = createDate;
        this.createdBy = createdBy;
        this.lastUpdate = lastUpdate;
        this.lastUpdatedBy = lastUpdatedBy;
        this.divisionID = divisionID;
    }

    /**
     * constructor for customer object that takes zero arguments
     */
    public Customer() {
        this.id = 0;
        this.name = "";
        this.address = "";
        this.postalCode = "";
        this.phone = "";
        this.createDate = new Timestamp(System.currentTimeMillis());
        this.createdBy = "";
        this.lastUpdate = new Timestamp(System.currentTimeMillis());;
        this.lastUpdatedBy = "";
        this.divisionID = 0;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    /**
     * validates input, then sets customer name
     * @param name
     * @return true if input was valid and field was updated
     */
    public boolean setName(String name) {
        if (name.length() > 50 || name.equals("") || name == null) {
            Utils.showErrorMessage(SchedulingApplication.labels.getString("ui.InvalidName"), SchedulingApplication.labels.getString("ui.InvalidInput"), SchedulingApplication.labels.getString("ui.InvalidInputMsg"));
            return false;
        } else {
            this.name = name;
            return true;
        }

    }

    public String getAddress() {
        return address;
    }

    /**
     * validates input, then sets customer address
     * @param address
     * @return true if input was valid and field was updated
     */
    public boolean setAddress(String address) {
        if (address.length() > 100 || address.equals("") || address == null) {
            Utils.showErrorMessage(SchedulingApplication.labels.getString("ui.InvalidAddress"), SchedulingApplication.labels.getString("ui.InvalidInput"), SchedulingApplication.labels.getString("ui.InvalidInputMsg"));
            return false;
        } else {
            this.address = address;
            return true;
        }
    }

    public String getPostalCode() {
        return postalCode;
    }


    public String getDivisionName() {
        return divisionName;
    }

    public void setDivisionName(String divisionName) {
        this.divisionName = divisionName;
    }

    /**
     * validates input, then sets customer postal code
     * @param postalCode
     * @return true if input was valid and field was updated
     */
    public boolean setPostalCode(String postalCode) {
        if (postalCode.length() > 50  || postalCode.equals("") || postalCode == null) {
            Utils.showErrorMessage(SchedulingApplication.labels.getString("ui.InvalidZip"), SchedulingApplication.labels.getString("ui.InvalidInput"), SchedulingApplication.labels.getString("ui.InvalidInputMsg"));
            return false;
        } else {
            this.postalCode = postalCode;
            return true;
        }
    }

    public String getPhone() {
        return phone;
    }

    /**
     * validates input, then sets customer phone
     * @param phone
     * @return true if input was valid and field was updated
     */
    public boolean setPhone(String phone) {
        if (phone.length() > 50  || phone.equals("") || phone == null) {
            Utils.showErrorMessage(SchedulingApplication.labels.getString("ui.InvalidPhone"), SchedulingApplication.labels.getString("ui.InvalidInput"), SchedulingApplication.labels.getString("ui.InvalidInputMsg"));
            return false;
        } else {
            this.phone = phone;
            return true;
        }
    }

    public Timestamp getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Timestamp createDate) {
        this.createDate = createDate;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Timestamp getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Timestamp lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public String getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    public void setLastUpdatedBy(String lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }

    public int getDivisionID() {
        return divisionID;
    }

    public String getCountry() {
        return country;
    }

    public void setDivisionID(int divisionID) {
        this.divisionID = divisionID;
    }
}
