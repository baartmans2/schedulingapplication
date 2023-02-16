module com.example.schedulingapplication {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.example.schedulingapplication to javafx.fxml;
    exports com.example.schedulingapplication;
}