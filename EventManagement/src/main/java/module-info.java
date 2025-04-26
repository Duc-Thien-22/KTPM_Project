module com.ntn.eventmanagement {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.persistence;
    requires java.base;
    requires de.mkammerer.argon2.nolibs;
    requires com.sun.jna;
            
    opens com.ntn.eventmanagement to javafx.fxml;
    opens com.ntn.controllers to javafx.fxml; 
    opens com.ntn.pojo to javafx.base;
    opens com.ntn.pojo.DTO to javafx.base;
            
    exports com.ntn.eventmanagement;

}
