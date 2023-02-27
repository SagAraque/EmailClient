module com.clienteemail {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires mail;


    opens com.clienteemail to javafx.fxml;
    exports com.clienteemail;
    exports com.clienteemail.ViewProviders;
    opens com.clienteemail.ViewProviders to javafx.fxml;
}