package com.example.schedulingapplication;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.WindowEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.ZoneId;
import java.util.Locale;
import java.util.ResourceBundle;

public class SchedulingApplication extends Application {

    @FXML
    private Label zoneID;
    @FXML
    private TextField username;
    @FXML
    private TextField password;
    public static ResourceBundle labels = ResourceBundle.getBundle("com.example.schedulingapplication.LabelsBundle", Locale.getDefault());

    @FXML
    public void initialize() {
        zoneID.setText(labels.getString("ZoneID") + " " + ZoneId.systemDefault().getId());
    }

    /**
     * Submits a login attempt using the values entered in the username/password field. Logs attempt in login_activity.txt, and if the login is successful, opens application homepage
     * @throws IOException
     */
    @FXML
    public void submitLogin() throws IOException {
        boolean success = false;
        User user = User.login(username.getText(), password.getText());
        if (user == null) {
            System.out.println("Invalid username or password.");
        } else {
            success = true;
            System.out.println("Logged in as " + user.getUsername() + ".");
        }

        File file;
        FileOutputStream fos = null;
        try {
            file = new File("login_activity.txt");
            fos = new FileOutputStream(file, true);
            if (!file.exists()) {
                file.createNewFile();
            }
            if (success) {
                fos.write(("Successful Login Attempt at " + new Timestamp(System.currentTimeMillis()) + " (Username: " + username.getText() + ", Password: " + password.getText() + ").\n").getBytes());
            } else {
                fos.write(("Failed Login Attempt at " + new Timestamp(System.currentTimeMillis()) + " (Username: " + username.getText() + ", Password: " + password.getText() + ").\n").getBytes());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (success) {
            Appointment upcomingAppt = Appointment.getUpcomingAppt(user.getId());
            if (upcomingAppt != null) {
                Utils.showInfoMessage(labels.getString("ui.UpcomingAppt"), labels.getString("ui.UpcomingAppt"), (labels.getString("ui.Start") + ": " + upcomingAppt.getStartTime() + ", " + labels.getString("ui.ID") + ": " + upcomingAppt.getId()));
            }
            //open homepage
            FXMLLoader loader = new FXMLLoader(SchedulingApplication.class.getResource("homepage.fxml"), labels);
            Parent root = loader.load();
            Homepage controller = loader.getController();
            controller.setActiveUser(user);
            Scene newScene = new Scene(root);
            Stage newStage = new Stage();
            newStage.setScene(newScene);
            Stage oldStage = (Stage) zoneID.getScene().getWindow();
            oldStage.close();
            newStage.setTitle(labels.getString("ui.Homepage"));
            newStage.show();
            newStage.setOnCloseRequest((EventHandler<WindowEvent>) new EventHandler<WindowEvent>() {
                public void handle(WindowEvent we) {

                }
            });
        } else {
            Utils.showErrorMessage(labels.getString("Error"), labels.getString("Error"), labels.getString("LoginFail"));
        }
    }

    @Override @FXML
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(SchedulingApplication.class.getResource("sign-in-form.fxml"), labels);
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle(labels.getString("SignIn"));
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}