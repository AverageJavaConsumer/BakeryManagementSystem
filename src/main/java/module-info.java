module com.example.bakerymanagementsystem {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;


    requires org.controlsfx.controls;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires java.sql;
    requires org.xerial.sqlitejdbc;


    opens com.example.bakerymanagementsystem to javafx.fxml;
    exports com.example.bakerymanagementsystem;
}