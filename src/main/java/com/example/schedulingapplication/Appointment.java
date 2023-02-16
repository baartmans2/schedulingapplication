package com.example.schedulingapplication;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class Appointment {

    /* public static Appointment findByID(int id) {
        try {
            Connection connection = Driver.getConnection();
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM appointments WHERE Appointment_ID = " + id + ";");
            while (rs.next()) {
                return new Appointment(rs.getInt("Appointment_ID"), rs.getString("Title"), rs.getString("Description"), rs.getString("Location"), rs.getString("Type"), rs.getTimestamp("Start"), rs.getTimestamp("End"), rs.getTimestamp("Create_Date"), rs.getString("Created_By"), rs.getTimestamp("Last_Update"), rs.getString("Last_Updated_By"), rs.getInt("Customer_ID"), rs.getInt("User_ID"), rs.getInt("Contact_ID"));
            }
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    } */

    /**
     * checks if user has an upcoming appointment in the next 15 minutes
     * @param userID id of active user
     * @return Appointment object if there is an upcoming appointment, null if not
     */
    public static Appointment getUpcomingAppt(int userID) {
        try {
            Connection connection = Driver.getConnection();
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM appointments WHERE User_ID =? AND Start > NOW() AND Start < DATE_ADD(NOW(), INTERVAL 15 MINUTE);");
            ps.setInt(1, userID);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                System.out.println("Upcoming appt found");
                Appointment appt = new Appointment(rs.getInt("Appointment_ID"), rs.getString("Title"), rs.getString("Description"), rs.getString("Location"), rs.getString("Type"), rs.getTimestamp("Start"), rs.getTimestamp("End"), rs.getTimestamp("Create_Date"), rs.getString("Created_By"), rs.getTimestamp("Last_Update"), rs.getString("Last_Updated_By"), rs.getInt("Customer_ID"), rs.getInt("User_ID"), rs.getInt("Contact_ID"));
                ZonedDateTime startTime = appt.getStartTime().toLocalDateTime().atOffset(ZoneOffset.UTC).atZoneSameInstant(ZoneId.systemDefault());
                ZonedDateTime endTime = appt.getEndTime().toLocalDateTime().atOffset(ZoneOffset.UTC).atZoneSameInstant(ZoneId.systemDefault());
                appt.setStartTime(Timestamp.valueOf(startTime.toLocalDateTime()));
                appt.setEndTime(Timestamp.valueOf(endTime.toLocalDateTime()));
                return appt;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * checks if an appointment overlaps with any pre-existing appointments, excluding itself
     * @param appt the appointment to check against
     * @return true if there are overlaps, false if not
     */
    public static Boolean checkOverlappingAppts(Appointment appt) {
        try {
            Connection connection = Driver.getConnection();
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM appointments WHERE Customer_ID =? AND ( (Start <=? AND End >=?) OR (Start <=? AND End >=?) );");
            ps.setInt(1, appt.getCustomerID());
            ps.setObject(2, appt.getStartTime());
            ps.setObject(3, appt.getStartTime());
            ps.setObject(4, appt.getEndTime());
            ps.setObject(5, appt.getEndTime());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                if (rs.getInt(1) == appt.getId()) {
                    return false;
                }
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * gets all appointments to display on homepage form in the specified interval
     *
     * @param isMonthMode whether appointments are being displayed by month or by week
     * @param num the number of months/weeks ahead/behind from which appointments are being displayed
     * @return observableList of all appointments within the specified interval
     */
    public static ObservableList<Appointment> getAllAppointmentsInInterval(boolean isMonthMode, int num) {
        ObservableList<Appointment> appointments = FXCollections.observableArrayList();
        Calendar calendar = Calendar.getInstance();
        java.util.Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Connection connection = Driver.getConnection();
            PreparedStatement ps;
            if (isMonthMode) {
                int years = 0;
                if (num >= 12 || num <= -12) {
                    years = num / 12;
                }
                if (years > 0 || years < 0) {
                    num = num % 12;
                }
                //grab month of appointments
                ps = connection.prepareStatement("SELECT * FROM appointments WHERE Year(Start) = (Year(curdate()) +?) AND Month(Start) = (Month(curdate()) +?);");
                ps.setInt(1, years);
                ps.setInt(2, num);
            } else {
                //grab week of appointments
                ps = connection.prepareStatement("SELECT * FROM appointments WHERE date(Start) >= date_add(curdate(), INTERVAL (7 * ?) DAY) AND date(Start) < date_add(curdate(), INTERVAL (7 * ? + 7) DAY);");
                ps.setInt(1, num);
                ps.setInt(2, num);
            }
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                Appointment appt = new Appointment(rs.getInt("Appointment_ID"), rs.getString("Title"), rs.getString("Description"), rs.getString("Location"), rs.getString("Type"), rs.getTimestamp("Start"), rs.getTimestamp("End"), rs.getTimestamp("Create_Date"), rs.getString("Created_By"), rs.getTimestamp("Last_Update"), rs.getString("Last_Updated_By"), rs.getInt("Customer_ID"), rs.getInt("User_ID"), rs.getInt("Contact_ID"));
                ZonedDateTime startTime = appt.getStartTime().toLocalDateTime().atOffset(ZoneOffset.UTC).atZoneSameInstant(ZoneId.systemDefault());
                ZonedDateTime endTime = appt.getEndTime().toLocalDateTime().atOffset(ZoneOffset.UTC).atZoneSameInstant(ZoneId.systemDefault());
                appt.setStartTime(Timestamp.valueOf(startTime.toLocalDateTime()));
                appt.setEndTime(Timestamp.valueOf(endTime.toLocalDateTime()));
                appointments.add(appt);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return appointments;
    }

    /**
     * gets all appointments for a specific contact by start date
     * @param contactID id of contact to get appts of
     * @return observablelist of contacts for appointments
     */
    public static ObservableList<Appointment> getApptsByContact(int contactID) {
        ObservableList<Appointment> appointments = FXCollections.observableArrayList();
        try {
            Connection connection = Driver.getConnection();
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM appointments WHERE Contact_ID =? ORDER BY Start;");
            ps.setInt(1, contactID);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                appointments.add(new Appointment(rs.getInt("Appointment_ID"), rs.getString("Title"), rs.getString("Description"), rs.getString("Location"), rs.getString("Type"), rs.getTimestamp("Start"), rs.getTimestamp("End"), rs.getTimestamp("Create_Date"), rs.getString("Created_By"), rs.getTimestamp("Last_Update"), rs.getString("Last_Updated_By"), rs.getInt("Customer_ID"), rs.getInt("User_ID"), rs.getInt("Contact_ID")));
            }
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return appointments;
    }

    /* public static ObservableList<Appointment> getAllAppointments() {
        ObservableList<Appointment> appointments = FXCollections.observableArrayList();
        try {
            Connection connection = Driver.getConnection();
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM appointments;");
            while(rs.next()) {
                appointments.add(new Appointment(rs.getInt("Appointment_ID"), rs.getString("Title"), rs.getString("Description"), rs.getString("Location"), rs.getString("Type"), rs.getTimestamp("Start"), rs.getTimestamp("End"), rs.getTimestamp("Create_Date"), rs.getString("Created_By"), rs.getTimestamp("Last_Update"), rs.getString("Last_Updated_By"), rs.getInt("Customer_ID"), rs.getInt("User_ID"), rs.getInt("Contact_ID")));
            }
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return appointments;
    } */

    /**
     * updates an appointment in the database
     *
     * @param appt the updated appointment to save to the DB
     */
    public static void updateAppointment(Appointment appt) {
        try {
            Connection connection = Driver.getConnection();
            PreparedStatement ps = connection.prepareStatement("UPDATE appointments SET Title=?, Description=?, Location=?, Type=?, Start=?, End=?, Last_Update=CURRENT_TIMESTAMP, Last_Updated_By=?, Customer_ID=?, User_ID=?, Contact_ID=? WHERE Appointment_ID=?;");
            ps.setString(1, appt.getTitle());
            ps.setString(2, appt.getDescription());
            ps.setString(3, appt.getLocation());
            ps.setString(4, appt.getType());
            ps.setObject(5, appt.getStartTime());
            ps.setObject(6, appt.getEndTime());
            ps.setString(7, appt.getLastUpdatedBy());
            ps.setInt(8, appt.getCustomerID());
            ps.setInt(9, appt.getUserID());
            ps.setInt(10, appt.getContactID());
            ps.setInt(11, appt.getId());
            ps.execute();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * creates an appointment in the database
     *
     * @param appt the new appointment to save to the DB
     */
    public static void createAppointment(Appointment appt) {
        try {
            Connection connection = Driver.getConnection();
            PreparedStatement ps = connection.prepareStatement("INSERT INTO appointments(Title, Description, Location, Type, Start, End, Create_Date, Created_By, Last_Update, Last_Updated_By, Customer_ID, User_ID, Contact_ID) VALUES (?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, ?, CURRENT_TIMESTAMP, ?, ?, ?, ?);");
            ps.setString(1, appt.getTitle());
            ps.setString(2, appt.getDescription());
            ps.setString(3, appt.getLocation());
            ps.setString(4, appt.getType());
            ps.setObject(5, appt.getStartTime());
            ps.setObject(6, appt.getEndTime());
            ps.setString(7, appt.getCreatedBy());
            ps.setString(8, appt.getLastUpdatedBy());
            ps.setInt(9, appt.getCustomerID());
            ps.setInt(10, appt.getUserID());
            ps.setInt(11, appt.getContactID());
            ps.execute();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * deletes an appointment from the database
     * @param id id of the appointment to be deleted
     */
    public static void deleteAppointment(int id) {
        try {
            Connection connection = Driver.getConnection();
            PreparedStatement ps = connection.prepareStatement("DELETE FROM appointments WHERE Appointment_ID =?;");
            ps.setInt(1, id);
            ps.execute();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * builds a string object of the upcoming schedule for each contact to display in the textarea on the homepage.
     *
     * @return schedule string
     */
    public static String buildContactSchedules() {
        String schedule = "";
        String prevContact = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getDefault());
        try {
            Connection connection = Driver.getConnection();
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("select c.Contact_Name, a.* from contacts as c inner join appointments as a where c.Contact_ID = a.Contact_ID AND a.Start > NOW() order by c.Contact_Name, a.Start;");
            while(rs.next()) {
                Timestamp startTime = rs.getTimestamp("Start");
                Timestamp endTime = rs.getTimestamp("End");
                ZonedDateTime zonedStartTime = startTime.toLocalDateTime().atOffset(ZoneOffset.UTC).atZoneSameInstant(ZoneId.systemDefault());
                ZonedDateTime zonedEndTime = endTime.toLocalDateTime().atOffset(ZoneOffset.UTC).atZoneSameInstant(ZoneId.systemDefault());

                if (!rs.getString("Contact_Name").equals(prevContact)) {
                    schedule += (rs.getString("Contact_Name") + "\n");
                }
                schedule += ("- " + Utils.parenthesize.run(rs.getDate("Start") + ", " + zonedStartTime.toLocalDateTime().toLocalTime() + " - " + zonedEndTime.toLocalDateTime().toLocalTime())  + " " + SchedulingApplication.labels.getString("ui.ID") + ": " + rs.getInt("Appointment_ID") + ", " + rs.getString("Title") + ", " + rs.getString("Type") + ", " + rs.getString("Description") + ", " + SchedulingApplication.labels.getString("ui.CustomerID") + ": " + rs.getInt("Customer_ID") + "\n");
                prevContact = rs.getString("Contact_Name");
            }
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return schedule;
    }
    private int id;
    private String title;
    private String description;
    private String location;
    private String type;
    private Timestamp startTime;
    private Timestamp endTime;
    private Timestamp createDate;
    private String createdBy;
    private Timestamp lastUpdate;
    private String lastUpdatedBy;
    private int customerID;
    private int userID;
    private int contactID;

    /**
     * constructor for appointment object
     *
     * @param id
     * @param title
     * @param description
     * @param location
     * @param type
     * @param startTime
     * @param endTime
     * @param createDate
     * @param createdBy
     * @param lastUpdate
     * @param lastUpdatedBy
     * @param customerID
     * @param userID
     * @param contactID
     */
    public Appointment(int id, String title, String description, String location, String type, Timestamp startTime, Timestamp endTime, Timestamp createDate, String createdBy, Timestamp lastUpdate, String lastUpdatedBy, int customerID, int userID, int contactID) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.location = location;
        this.type = type;
        this.startTime = startTime;
        this.endTime = endTime;
        this.createDate = createDate;
        this.createdBy = createdBy;
        this.lastUpdate = lastUpdate;
        this.lastUpdatedBy = lastUpdatedBy;
        this.customerID = customerID;
        this.userID = userID;
        this.contactID = contactID;
    }

    /**
     * constructor for appointment object that takes no arguments
     */
    public Appointment() {
        this.id = -1;
        this.title = "";
        this.description = "";
        this.location = "";
        this.type = "";
        this.startTime = new Timestamp(System.currentTimeMillis());;
        this.endTime = new Timestamp(System.currentTimeMillis());;
        this.createDate = new Timestamp(System.currentTimeMillis());;
        this.createdBy = "System";
        this.lastUpdate = new Timestamp(System.currentTimeMillis());;
        this.lastUpdatedBy = "System";
        this.customerID = -1;
        this.userID = -1;
        this.contactID = -1;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    /**
     * validates input and sets title
     * @param title
     * @return true if input was valid and field was updated
     */
    public boolean setTitle(String title) {
        if (title.length() > 50  && !title.equals("")) {
            Utils.showErrorMessage(SchedulingApplication.labels.getString("ui.InvalidTitle"), SchedulingApplication.labels.getString("ui.InvalidInput"), SchedulingApplication.labels.getString("ui.InvalidInputMsg"));
            return false;
        } else {
            this.title = title;
            return true;
        }
    }

    public String getDescription() {
        return description;
    }

    /**
     * validates input and sets description
     * @param description
     * @return true if input was valid and field was updated
     */
    public boolean setDescription(String description) {
        if (description.length() > 50  && !description.equals("")) {
            Utils.showErrorMessage(SchedulingApplication.labels.getString("ui.InvalidDesc"), SchedulingApplication.labels.getString("ui.InvalidInput"), SchedulingApplication.labels.getString("ui.InvalidInputMsg"));
            return false;
        } else {
            this.description = description;
            return true;
        }
    }

    public String getLocation() {
        return location;
    }

    /**
     * validates input and sets location
     * @param location
     * @return true if input was valid and field was updated
     */
    public boolean setLocation(String location) {
        if (location.length() > 50 && !location.equals("")) {
            Utils.showErrorMessage(SchedulingApplication.labels.getString("ui.InvalidLocation"), SchedulingApplication.labels.getString("ui.InvalidInput"), SchedulingApplication.labels.getString("ui.InvalidInputMsg"));
            return false;
        } else {
            this.location = location;
            return true;
        }
    }

    public String getType() {
        return type;
    }

    /**
     * validates type and sets location
     * @param type
     * @return true if input was valid and field was updated
     */
    public boolean setType(String type) {
        if (type.length() > 50 && !type.equals("")) {
            Utils.showErrorMessage(SchedulingApplication.labels.getString("ui.InvalidApptType"), SchedulingApplication.labels.getString("ui.InvalidInput"), SchedulingApplication.labels.getString("ui.InvalidInputMsg"));
            return false;
        } else {
            this.type = type;
            return true;
        }
    }

    public Timestamp getStartTime() {
        return startTime;
    }

    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }

    public Timestamp getEndTime() {
        return endTime;
    }

    public void setEndTime(Timestamp endTime) {
        this.endTime = endTime;
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

    public int getCustomerID() {
        return customerID;
    }

    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public int getContactID() {
        return contactID;
    }

    public void setContactID(int contactID) {
        this.contactID = contactID;
    }
}
