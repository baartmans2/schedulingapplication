package com.example.schedulingapplication;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import java.util.Date;
import java.util.Calendar;

public abstract class Utils {
    interface StringFunction {
        String run(String str);
    }

    /**
     * lamba expression - shortens code for parenthesizing strings
     */
    public static StringFunction parenthesize = (s) -> "(" + s + ")";
    /**
     * Shows an Error Message Popup
     *
     * @param title message title
     * @param header message header
     * @param message message description
     */
    @FXML
    public static void showErrorMessage(String title, String header, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(message);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.showAndWait();
    }

    /**
     * Shows a popup for messages that are not errors, such as success messages
     *
     * @param title message title
     * @param header message header
     * @param message message description
     */
    @FXML
    public static void showInfoMessage(String title, String header, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(message);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.showAndWait();
    }

    /**
     * Gets a string representation of the timeframe that is being used to display appointments
     *
     * @param isMonthMode whether appointments are being displayed by month or by week
     * @param num the number of months/weeks ahead/behind from which appointments are being displayed
     * @return a string representation of the timeframe that is being used to display appointments
     */
    public static String getTimeframe(boolean isMonthMode, int num) {
        Date date = new Date(System.currentTimeMillis());
        if (isMonthMode) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.MONTH, num);
            return ((calendar.get(Calendar.MONTH) + 1) + " / " + calendar.get(Calendar.YEAR));
        } else {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.WEEK_OF_YEAR, num);
            return ((calendar.get(Calendar.MONTH) + 1) + " / " + calendar.get(Calendar.DAY_OF_MONTH) + " / " + calendar.get(Calendar.YEAR));
        }
    }
}
