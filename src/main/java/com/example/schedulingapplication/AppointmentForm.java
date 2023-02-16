package com.example.schedulingapplication;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.TimeZone;

public class AppointmentForm {
    @FXML private TextField idInput;
    @FXML private TextField titleInput;
    @FXML private TextField descInput;
    @FXML private TextField locationInput;
    @FXML private TextField typeInput;
    @FXML private ComboBox<String> customerCombo;
    @FXML private ComboBox<String> userCombo;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private TextField startHour;
    @FXML private TextField startMin;
    @FXML private TextField endHour;
    @FXML private TextField endMin;
    @FXML private ComboBox<String> contactCombo;

    private User activeUser;

    public User getActiveUser() {
        return activeUser;
    }

    public void setActiveUser(User activeUser) {
        this.activeUser = activeUser;
    }

    private boolean updating = false;
    private Appointment appointment;

    public void setUpdating() { updating = true; }
    public void setAppointment(Appointment appointment) { this.appointment = appointment; }

    /**
     * pre-fills the fields with existing values if the user is updating an appointment
     */
    public void setFields() {
        idInput.setText(appointment.getId() + " " + SchedulingApplication.labels.getString("ui.AutoGen"));
        titleInput.setText(appointment.getTitle());
        descInput.setText(appointment.getDescription());
        typeInput.setText(appointment.getType());
        locationInput.setText(appointment.getLocation());
        startDatePicker.setValue(appointment.getStartTime().toLocalDateTime().toLocalDate());
        endDatePicker.setValue(appointment.getEndTime().toLocalDateTime().toLocalDate());
        startHour.setText(Integer.toString(appointment.getStartTime().toLocalDateTime().getHour()));
        int startMinute = appointment.getStartTime().toLocalDateTime().getMinute();
        if (startMinute < 10) {
            startMin.setText("0" + startMinute);
        } else {
            startMin.setText(Integer.toString(startMinute));
        }
        endHour.setText(Integer.toString(appointment.getEndTime().toLocalDateTime().getHour()));
        int endMinute = appointment.getEndTime().toLocalDateTime().getMinute();
        if (endMinute < 10) {
            endMin.setText("0" + endMinute);
        } else {
            endMin.setText(Integer.toString(endMinute));
        }
        Contact apptContact = Contact.findByID(appointment.getContactID());
        Customer apptCustomer = Customer.findByID(appointment.getCustomerID());
        User apptUser = User.findByID(appointment.getUserID());
        customerCombo.getSelectionModel().select(apptCustomer.getId() + Utils.parenthesize.run(apptCustomer.getName()));
        contactCombo.getSelectionModel().select(apptContact.getId() + " " + Utils.parenthesize.run(apptContact.getName() + ", " + apptContact.getEmail()));
        userCombo.getSelectionModel().select(apptUser.getId() + " " + Utils.parenthesize.run(apptUser.getUsername()));
    }

    /**
     * initializes appointment form. contains 3 lambda expressions which iterate through a list of objects and format each object, then insert it into a comboBox on the ui. this shortens the logic for populating all of the comboBoxes with the users, customers, and contacts in the system.
     */
    public void initialize() {
        ObservableList<String> customerNames = FXCollections.observableArrayList();
        Customer.getAllCustomers().forEach(customer -> {customerNames.add(customer.getId() + " (" + customer.getName() + ")");});
        customerCombo.setItems(customerNames);
        ObservableList<String> userLabels = FXCollections.observableArrayList();
        User.getAllUsers().forEach(user -> {userLabels.add(user.getId() + " (" + user.getUsername() + ")");});
        userCombo.setItems(userLabels);
        ObservableList<String> contactLabels = FXCollections.observableArrayList();
        Contact.getAllContacts().forEach(contact -> {contactLabels.add(contact.getId() + " (" + contact.getName() + ", " + contact.getEmail() + ")" );});
        contactCombo.setItems(contactLabels);
    }

    /**
     * validates user input, then saves the new/updated appointment to the database and closes the form
     */
    @FXML
    public void save() {
        Calendar calendar = Calendar.getInstance();
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        if (!updating) {
            appointment = new Appointment();
        }
        if (appointment.setTitle(titleInput.getText()) && appointment.setDescription(descInput.getText()) && appointment.setLocation(locationInput.getText()) && appointment.setType(typeInput.getText())) {
            Timestamp startTimestamp;
            Timestamp endTimestamp;
            if (startDatePicker.getValue().compareTo(endDatePicker.getValue()) != 0) {
                Utils.showErrorMessage(SchedulingApplication.labels.getString("Error"), SchedulingApplication.labels.getString("Error"), SchedulingApplication.labels.getString("ui.WithinBusinessHours"));
                return;
            }
            try {
                int startH = Integer.parseInt(startHour.getText());
                int startM = Integer.parseInt(startMin.getText());
                if (startH < 0 || startH > 23) {
                    Utils.showErrorMessage(SchedulingApplication.labels.getString("Error"), SchedulingApplication.labels.getString("Error"), SchedulingApplication.labels.getString("ui.InvalidStartTimeFormat"));
                    return;
                }
                if (startM < 0 || startM > 59) {
                    Utils.showErrorMessage(SchedulingApplication.labels.getString("Error"), SchedulingApplication.labels.getString("Error"), SchedulingApplication.labels.getString("ui.InvalidStartTimeFormat"));
                    return;
                }
                startTimestamp = Timestamp.valueOf(startDatePicker.getValue().toString() + " " + startH + ":" + startM + ":00");
            } catch (Exception e) {
                Utils.showErrorMessage(SchedulingApplication.labels.getString("Error"), SchedulingApplication.labels.getString("Error"), SchedulingApplication.labels.getString("ui.InvalidStartTimeFormat"));
                return;
            }
            try {
                int endH = Integer.parseInt(endHour.getText());
                int endM = Integer.parseInt(endMin.getText());
                if (endH < 0 || endH > 23) {
                    Utils.showErrorMessage(SchedulingApplication.labels.getString("Error"), SchedulingApplication.labels.getString("Error"), SchedulingApplication.labels.getString("ui.InvalidEndTimeFormat"));
                    return;
                }
                if (endM < 0 || endM > 59) {
                    Utils.showErrorMessage(SchedulingApplication.labels.getString("Error"), SchedulingApplication.labels.getString("Error"), SchedulingApplication.labels.getString("ui.InvalidEndTimeFormat"));
                    return;
                }
                endTimestamp = Timestamp.valueOf(endDatePicker.getValue().toString() + " " + endH + ":" + endM + ":00");
            } catch (Exception e) {
                Utils.showErrorMessage(SchedulingApplication.labels.getString("Error"), SchedulingApplication.labels.getString("Error"), SchedulingApplication.labels.getString("ui.InvalidEndTimeFormat"));
                return;
            }
            if (startTimestamp.compareTo(endTimestamp) > 0) {
                Utils.showErrorMessage(SchedulingApplication.labels.getString("Error"), SchedulingApplication.labels.getString("Error"), SchedulingApplication.labels.getString("ui.StartLessThanEnd"));
                return;
            }
            Timestamp startTime = Timestamp.valueOf(sdf.format(startTimestamp.getTime()));
            Timestamp endTime = Timestamp.valueOf(sdf.format(endTimestamp.getTime()));
            ZonedDateTime estStartTime = startTime.toLocalDateTime().atOffset(ZoneOffset.UTC).atZoneSameInstant(ZoneId.of("America/New_York"));
            ZonedDateTime estEndTime = endTime.toLocalDateTime().atOffset(ZoneOffset.UTC).atZoneSameInstant(ZoneId.of("America/New_York"));
            if (estStartTime.getHour() > 21 || estStartTime.getHour() < 8 || estEndTime.getHour() > 21 || estEndTime.getHour() < 8) {
                Utils.showErrorMessage(SchedulingApplication.labels.getString("Error"), SchedulingApplication.labels.getString("Error"), SchedulingApplication.labels.getString("ui.WithinBusinessHours"));
                return;
            }
            appointment.setStartTime(startTime);
            appointment.setEndTime(endTime);
            appointment.setUserID(Integer.parseInt(userCombo.getValue().substring(0,1)));
            appointment.setContactID(Integer.parseInt(contactCombo.getValue().substring(0,1)));
            appointment.setCustomerID(Integer.parseInt(customerCombo.getValue().substring(0,1)));
            appointment.setLastUpdatedBy(getActiveUser().getUsername());
            if (!Appointment.checkOverlappingAppts(appointment)) {
                if (updating) {
                    Appointment.updateAppointment(appointment);
                } else {
                    appointment.setCreatedBy(getActiveUser().getUsername());
                    Appointment.createAppointment(appointment);
                }
            } else {
                Utils.showErrorMessage(SchedulingApplication.labels.getString("Error"), SchedulingApplication.labels.getString("Error"), SchedulingApplication.labels.getString("ui.ApptOverlap"));
                return;
            }
            Stage stage = (Stage) idInput.getScene().getWindow();
            stage.close();
        }
    }

    /**
     * closes the form
     */
    @FXML
    public void cancel() {
        Stage stage = (Stage) idInput.getScene().getWindow();
        stage.close();
    }

}
