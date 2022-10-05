package ua.opu.shveda.databaseprocessor.model;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface Mapper<T> {
    T map(ResultSet set) throws SQLException;
}
