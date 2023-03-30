module com.daviddang.library {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires mysql.connector.j;

    opens com.daviddang.library to javafx.fxml;
    exports com.daviddang.library;
}