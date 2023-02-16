package com.example.schedulingapplication;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
public class CustomerForm {
    @FXML private ComboBox<String> countryInput;
    @FXML private ComboBox<String> divisionInput;

    @FXML private TextField nameInput;
    @FXML private TextField addressInput;
    @FXML private TextField zipInput;
    @FXML private TextField phoneInput;
    @FXML private TextField idInput;
    private boolean updating = false;
    private Customer customer;

    private User activeUser;

    public User getActiveUser() {
        return activeUser;
    }

    public void setActiveUser(User activeUser) {
        this.activeUser = activeUser;
    }

    public void setUpdating() {
        updating = true;
    }
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    /**
     * pre-fills the field with existing values when a customer is being updated
     */
    public void setFields() {
        idInput.setText(customer.getId() + " " + SchedulingApplication.labels.getString("ui.AutoGen"));
        nameInput.setText(customer.getName());
        addressInput.setText(customer.getAddress());
        zipInput.setText(customer.getPostalCode());
        phoneInput.setText(customer.getPhone());
        countryInput.getSelectionModel().select( Country.findByID(FirstLevelDivision.findByID(customer.getDivisionID()).getCountryID()).getName());
        divisionInput.getSelectionModel().select(customer.getDivisionName());
    }

    public void initialize() {
        countryInput.setItems(Country.getAllCountries());
        countryInput.getSelectionModel().selectFirst();
        divisionInput.setItems(FirstLevelDivision.getDivisionsByCountry(countryInput.getValue()));
        divisionInput.getSelectionModel().selectFirst();
        countryInput.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                divisionInput.setItems(FirstLevelDivision.getDivisionsByCountry(countryInput.getValue()));
                divisionInput.getSelectionModel().selectFirst();
            }
        });
    }

    /**
     * Validates user input and then saves the new or existing customer.
     */
    @FXML
    public void save() {
        if (!updating) {
            customer = new Customer();
        }
        if (customer.setName(nameInput.getText()) && customer.setAddress(addressInput.getText()) && customer.setPostalCode(zipInput.getText()) && customer.setPhone(phoneInput.getText())) {
            customer.setDivisionID(FirstLevelDivision.findIDByName(divisionInput.getValue()));
            customer.setLastUpdatedBy(getActiveUser().getUsername());
            if (updating) {
                Customer.updateCustomer(customer);
            } else {
                customer.setCreatedBy(getActiveUser().getUsername());
                Customer.createCustomer(customer);
            }
            Stage stage = (Stage) nameInput.getScene().getWindow();
            stage.close();
        }
    }

    /**
     * exits the form back to homepage
     */
    @FXML
    public void cancel() {
        Stage stage = (Stage) nameInput.getScene().getWindow();
        stage.close();
    }
}
