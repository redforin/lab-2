module com.example.project {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fasterxml.jackson.databind;


    opens com.example.project to javafx.fxml;
    exports com.example.project;
}