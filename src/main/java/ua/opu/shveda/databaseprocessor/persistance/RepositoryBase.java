package ua.opu.shveda.databaseprocessor.persistance;

import ua.opu.shveda.databaseprocessor.model.Mapper;

import java.sql.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public abstract class RepositoryBase<T> implements Repository<T> {
    final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public final DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm:ss");

    void executeUpdate(String sql) {
        try (
                Connection conn = DriverManager.getConnection(dbPath);
                Statement st = conn.createStatement()
        ) {
            st.executeUpdate(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    List<T> executeList(String sql, Mapper<T> mapper) {
        try (
                Connection conn = DriverManager.getConnection(dbPath);
                Statement st = conn.createStatement()
                ) {
            st.execute(sql);
            ResultSet set = st.getResultSet();
            List<T> list = new ArrayList<>();
            while (set.next()) {
                list.add(mapper.map(set));
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
