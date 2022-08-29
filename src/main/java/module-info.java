module ua.opu.shveda.databaseprocessor {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.xerial.sqlitejdbc;
    requires java.sql;
    requires net.synedra.validatorfx;
    requires com.dlsc.formsfx;


    opens ua.opu.shveda.databaseprocessor to javafx.fxml;
    opens ua.opu.shveda.databaseprocessor.controller to  javafx.fxml;
    opens ua.opu.shveda.databaseprocessor.model to javafx.base;
    opens ua.opu.shveda.databaseprocessor.persistance to javafx.base;
    exports ua.opu.shveda.databaseprocessor;
    exports ua.opu.shveda.databaseprocessor.controller.form;
}