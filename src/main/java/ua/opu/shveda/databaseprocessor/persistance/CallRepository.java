package ua.opu.shveda.databaseprocessor.persistance;

import ua.opu.shveda.databaseprocessor.model.Call;
import ua.opu.shveda.databaseprocessor.model.Mapper;

import java.sql.*;
import java.util.List;
import java.util.Optional;

public class CallRepository extends RepositoryBase<Call> {
    final BrigadeRepository brigadeRepository = new BrigadeRepository();

    final Mapper<Call> mapper = set -> new Call(
            set.getInt("id"),
            set.getDate("_date").toLocalDate(),
            set.getTime("_time").toLocalTime(),
            set.getString("address"),
            brigadeRepository.findById(set.getInt("brigade_id")).orElse(null)
    );

    public static boolean callExistsById(int id) {
        try (
                Connection conn = DriverManager.getConnection(dbPath);
                Statement st = conn.createStatement()
        ) {
            st.execute(String.format("SELECT * FROM CALL WHERE id = %d", id));
            return st.getResultSet().next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<Call> findById(int id) {
        try (
                Connection conn = DriverManager.getConnection(dbPath);
                PreparedStatement st = conn.prepareStatement(
                        "SELECT * " +
                                "FROM CALL " +
                                "WHERE id = ?;")
        ) {
            st.setInt(1, id);
            st.execute();
            ResultSet set = st.getResultSet();
            return set.next() ? Optional.of(mapper.map(set)) : Optional.empty();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Call> findAll() {
        return executeList("SELECT * FROM CALL", mapper);
    }

    public void update(Call call) {
        try (
                Connection conn = DriverManager.getConnection(dbPath);
                PreparedStatement st = conn.prepareStatement(
                                "UPDATE CALL " +
                                "SET _date = ?, _time = ?, address = ?, brigade_id = ? WHERE id = ?;"
                        )
        ) {
            st.setDate(1, Date.valueOf(call.getDate()));
            st.setTime(2, Time.valueOf(call.getTime()));
            st.setString(3, call.getAddress());
            st.setInt(4, call.getBrigade().getId());
            st.setInt(4, call.getId());
            st.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void insert(Call call) {
        try (
                Connection conn = DriverManager.getConnection(dbPath);
                PreparedStatement st = conn.prepareStatement(
                        "INSERT INTO CALL (_date, _time, address, brigade_id) " +
                                "VALUES (?, ?, ?, ?)"
                )
        ) {
            st.setDate(1, Date.valueOf(call.getDate()));
            st.setTime(2, Time.valueOf(call.getTime()));
            st.setString(3, call.getAddress());
            st.setInt(4, call.getBrigade().getId());
            st.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
