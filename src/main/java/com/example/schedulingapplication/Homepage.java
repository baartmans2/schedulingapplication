package com.example.schedulingapplication;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;

public class Homepage {
    @FXML private TableView<Customer> customersTable = new TableView<>();
    @FXML private TableView<Appointment> appointmentsTable = new TableView<>();
    @FXML private TableView<ApptsMonthTypeView> apptsMonthTypeTable = new TableView<>();
    @FXML private TableView<ApptsLocationView> apptsLocationTable = new TableView<>();

    @FXML private TableView<Appointment> contactApptsTable = new TableView<>();

    @FXML private ComboBox<String> contactCombo;
    @FXML private ToggleGroup monthWeekToggle;
    @FXML private RadioButton monthRadio;
    @FXML private RadioButton weekRadio;
    @FXML private Label timeframeLabel;

    private boolean monthMode = true;

    private int activeContact = 0;

    private int weeksMonthsCounter = 0;

    private User activeUser;
    public int getActiveContact() {
        return activeContact;
    }

    public void setActiveContact(int activeContact) {
        this.activeContact = activeContact;
    }

    public User getActiveUser() {
        return activeUser;
    }

    public void setActiveUser(User activeUser) {
        this.activeUser = activeUser;
        updateTables();
    }

    public int getWeeksMonthsCounter() {
        return weeksMonthsCounter;
    }

    public void setWeeksMonthsCounter(int weeksMonthsCounter) {
        this.weeksMonthsCounter = weeksMonthsCounter;
    }

    public boolean isMonthMode() {
        return monthMode;
    }

    public void setMonthMode(boolean monthMode) {
        this.monthMode = monthMode;
    }

    /**
     * handler for radio button to sort appointments by week
     */
    @FXML
    public void selectWeek() {
        weekRadio.setSelected(true);
        setMonthMode(false);
        setWeeksMonthsCounter(0);
        updateTables();
    }

    /**
     * handler for radio button to sort appointments by month
     */
    @FXML
    public void selectMonth() {
        monthRadio.setSelected(true);
        setMonthMode(true);
        setWeeksMonthsCounter(0);
        updateTables();

    }

    /**
     * handler for radio button to view appointments by the previous month/week
     */
    @FXML
    public void previousMonthWeek() {
        setWeeksMonthsCounter(getWeeksMonthsCounter() - 1);
        updateTables();
    }

    /**
     * handler for radio button to view appointments by the next month/week
     */
    @FXML
    public void nextMonthWeek() {
        setWeeksMonthsCounter(getWeeksMonthsCounter() + 1);
        updateTables();
    }

    @FXML
    public void initialize() {
        monthWeekToggle = new ToggleGroup();
        monthRadio.setToggleGroup(monthWeekToggle);
        weekRadio.setToggleGroup(monthWeekToggle);
        monthRadio.setSelected(true);
        ObservableList<String> contactLabels = FXCollections.observableArrayList();
        Contact.getAllContacts().forEach(contact -> {contactLabels.add(contact.getId() + " (" + contact.getName() + ", " + contact.getEmail() + ")" );});
        contactCombo.setItems(contactLabels);
        contactCombo.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                setActiveContact(Integer.parseInt(contactCombo.getValue().substring(0,1)));
                contactApptsTable.setItems(Appointment.getApptsByContact(getActiveContact()));
            }
        });
        updateTables();
    }

    /**
     * updates all data displays on the homepage
     */
    @FXML
    public void updateTables() {
        customersTable.setItems(Customer.getAllCustomers());
        appointmentsTable.setItems(Appointment.getAllAppointmentsInInterval(isMonthMode(), getWeeksMonthsCounter()));
        apptsMonthTypeTable.setItems(ApptsMonthTypeView.getAllApptsByMonthAndType());
        apptsLocationTable.setItems(ApptsLocationView.getAllApptsByLocation());
        if (isMonthMode()) {
            timeframeLabel.setText(SchedulingApplication.labels.getString("ui.MonthOf") + ": " + Utils.getTimeframe(isMonthMode(), getWeeksMonthsCounter()));
        } else {
            timeframeLabel.setText(SchedulingApplication.labels.getString("ui.WeekOf") + ": " + Utils.getTimeframe(isMonthMode(), getWeeksMonthsCounter()));
        }
        contactApptsTable.setItems(Appointment.getApptsByContact(getActiveContact()));
    }

    /**
     * handler for add customer button, opens the customer form
     * @throws IOException
     */
    @FXML
    public void addCustomer() throws IOException {
        FXMLLoader loader = new FXMLLoader(SchedulingApplication.class.getResource("customer.fxml"), SchedulingApplication.labels);
        Parent root = loader.load();
        CustomerForm controller = loader.getController();
        controller.setActiveUser(getActiveUser());
        Scene newScene = new Scene(root);
        Stage newStage = new Stage();
        newStage.setScene(newScene);
        newStage.initModality(Modality.APPLICATION_MODAL);
        newStage.setTitle(SchedulingApplication.labels.getString("ui.AddCust"));
        newStage.showAndWait();
        updateTables();
    }

    /**
     * handler for update customer button, opens customer form
     * @throws IOException
     */
    @FXML
    public void updateCustomer() throws IOException {
        Customer selectedCustomer = customersTable.getSelectionModel().getSelectedItem();
        if (selectedCustomer == null) {
            Utils.showErrorMessage(SchedulingApplication.labels.getString("Error"), SchedulingApplication.labels.getString("Error"), SchedulingApplication.labels.getString("ui.SelectCustUpdate"));
        } else {
            FXMLLoader loader = new FXMLLoader(SchedulingApplication.class.getResource("customer.fxml"), SchedulingApplication.labels);
            Parent root = loader.load();
            CustomerForm controller = loader.getController();
            controller.setActiveUser(getActiveUser());
            controller.setUpdating();
            controller.setCustomer(selectedCustomer);
            controller.setFields();
            Scene newScene = new Scene(root);
            Stage newStage = new Stage();
            newStage.setScene(newScene);
            newStage.initModality(Modality.APPLICATION_MODAL);
            newStage.setTitle(SchedulingApplication.labels.getString("ui.UpdateCust"));
            newStage.showAndWait();
            updateTables();
        }
    }

    /**
     * handler for delete customer button. shows confirmation popup, then deletes customer if user confirms.
     */
    @FXML
    public void deleteCustomer() {
        Customer selectedCustomer = customersTable.getSelectionModel().getSelectedItem();
        if (selectedCustomer == null) {
            Utils.showErrorMessage(SchedulingApplication.labels.getString("Error"), SchedulingApplication.labels.getString("Error"), SchedulingApplication.labels.getString("ui.SelectCustDelete"));
        } else {
            //confirmation
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,SchedulingApplication.labels.getString("ui.ConfirmCustDelete"), ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
            confirm.showAndWait();
            if (confirm.getResult() == ButtonType.YES) {
                Customer.deleteCustomer(selectedCustomer.getId());
                updateTables();
                Utils.showInfoMessage(SchedulingApplication.labels.getString("ui.Success"), SchedulingApplication.labels.getString("ui.Success"), SchedulingApplication.labels.getString("ui.CustDeleteSuccess"));
            }
        }
    }

    /**
     * handler for add appointment button. opens appointment form
     * @throws IOException
     */
    @FXML
    public void addAppointment() throws IOException {
        FXMLLoader loader = new FXMLLoader(SchedulingApplication.class.getResource("appointment.fxml"), SchedulingApplication.labels);
        Parent root = loader.load();
        AppointmentForm controller = loader.getController();
        controller.setActiveUser(getActiveUser());
        Scene newScene = new Scene(root);
        Stage newStage = new Stage();
        newStage.setScene(newScene);
        newStage.initModality(Modality.APPLICATION_MODAL);
        newStage.setTitle(SchedulingApplication.labels.getString("ui.AddAppt"));
        newStage.showAndWait();
        updateTables();
    }

    /**
     * handler for update appointment button. opens appointment form.
     * @throws IOException
     */
    @FXML
    public void updateAppointment() throws IOException {
        Appointment selectedAppointment = appointmentsTable.getSelectionModel().getSelectedItem();
        if (selectedAppointment == null) {
            Utils.showErrorMessage(SchedulingApplication.labels.getString("Error"), SchedulingApplication.labels.getString("Error"), SchedulingApplication.labels.getString("ui.SelectApptUpdate"));
        } else {
            FXMLLoader loader = new FXMLLoader(SchedulingApplication.class.getResource("appointment.fxml"), SchedulingApplication.labels);
            Parent root = loader.load();
            AppointmentForm controller = loader.getController();
            controller.setActiveUser(getActiveUser());
            controller.setUpdating();
            controller.setAppointment(selectedAppointment);
            controller.setFields();
            Scene newScene = new Scene(root);
            Stage newStage = new Stage();
            newStage.setScene(newScene);
            newStage.initModality(Modality.APPLICATION_MODAL);
            newStage.setTitle(SchedulingApplication.labels.getString("ui.UpdateAppt"));
            newStage.showAndWait();
            updateTables();
        }
    }

    /**
     * handler for delete appointment button. shows confirmation popup, then deletes appointment if user confirms.
     */
    @FXML
    public void deleteAppointment() {
        Appointment selectedAppointment = appointmentsTable.getSelectionModel().getSelectedItem();
        if (selectedAppointment == null) {
            Utils.showErrorMessage(SchedulingApplication.labels.getString("Error"), SchedulingApplication.labels.getString("Error"), SchedulingApplication.labels.getString("ui.SelectApptDelete"));
        } else {
            //confirmation
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,SchedulingApplication.labels.getString("ui.ConfirmApptDelete"), ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
            confirm.showAndWait();
            if (confirm.getResult() == ButtonType.YES) {
                Appointment.deleteAppointment(selectedAppointment.getId());
                updateTables();
                Utils.showInfoMessage(SchedulingApplication.labels.getString("ui.Success"), SchedulingApplication.labels.getString("ui.Success"), SchedulingApplication.labels.getString("ui.ApptDeleteSuccess") + " " + Utils.parenthesize.run(SchedulingApplication.labels.getString("ui.ID") + ": " + selectedAppointment.getId() + ", " + SchedulingApplication.labels.getString("ui.Type") + ": " + selectedAppointment.getType()));
            }
        }
    }
}
