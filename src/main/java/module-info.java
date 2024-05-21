module org.lab5.lab_5 {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;


    opens org.lab5.lab_5 to javafx.fxml;
    opens org.lab5.lab_5.Utilities to com.google.gson;
    exports org.lab5.lab_5;
    exports org.lab5.lab_5.ViewControllers;
    opens org.lab5.lab_5.ViewControllers to javafx.fxml;
}