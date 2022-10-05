module ua.opu.shveda.databaseprocessor {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires com.dlsc.formsfx;
    requires org.xerial.sqlitejdbc;


    opens ua.opu.shveda.databaseprocessor to javafx.fxml;
    opens ua.opu.shveda.databaseprocessor.controller to  javafx.fxml;
    opens ua.opu.shveda.databaseprocessor.model to javafx.base;
    opens ua.opu.shveda.databaseprocessor.persistance to javafx.base;
    exports ua.opu.shveda.databaseprocessor;
}