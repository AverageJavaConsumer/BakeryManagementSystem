module com.example.bakerymanagementsystem {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires java.sql;

    opens com.example.bakerymanagementsystem to javafx.fxml;
    exports com.example.bakerymanagementsystem;
}