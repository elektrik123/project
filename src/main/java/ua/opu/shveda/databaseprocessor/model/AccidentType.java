package ua.opu.shveda.databaseprocessor.model;

import javafx.beans.property.SimpleIntegerProperty;
import ua.opu.shveda.databaseprocessor.persistance.Repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public record AccidentType(int id, String name) implements Repository {
    List<AccidentType> loadFromDB() {
        try (Connection conn = DriverManager.getConnection(dbPath)) {
            Statement st = conn.createStatement();
            st.execute("SELECT * FROM accident_type");
            var list = new ArrayList<AccidentType>();
            var rs = st.getResultSet();
            while (rs.next()) {
                list.add(Mappers.mapAccidentType(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
