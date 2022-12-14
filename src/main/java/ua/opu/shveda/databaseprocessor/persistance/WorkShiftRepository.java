package ua.opu.shveda.databaseprocessor.persistance;

import ua.opu.shveda.databaseprocessor.model.Mapper;
import ua.opu.shveda.databaseprocessor.model.WorkShift;

import java.sql.*;
import java.util.List;
import java.util.Optional;

public class WorkShiftRepository extends RepositoryBase<WorkShift> {
    final Mapper<WorkShift> mapper = set -> new WorkShift(
            set.getInt("id"),
            set.getTime("start_time").toLocalTime(),
            set.getTime("end_time").toLocalTime(),
            set.getDate("work_date").toLocalDate()
    );

    public static boolean workShiftExistsById(int id) {
        try (
                Connection conn = DriverManager.getConnection(dbPath);
                Statement st = conn.createStatement()
        ) {
            st.execute(String.format("SELECT * FROM WORK_SHIFT WHERE id = %d", id));
            return st.getResultSet().next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<WorkShift> findById(int id) {
        try (
                Connection conn = DriverManager.getConnection(dbPath);
                PreparedStatement st = conn.prepareStatement(
                        "SELECT * " +
                                "FROM WORK_SHIFT " +
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

    public List<WorkShift> findAll() {
        return executeList("SELECT * FROM WORK_SHIFT", mapper);
    }

    public void update(WorkShift workShift) {
        try (
                Connection conn = DriverManager.getConnection(dbPath);
                PreparedStatement st = conn.prepareStatement(
                        "UPDATE WORK_SHIFT " +
                                "SET start_time = ?, end_time = ?, work_date = ? WHERE id = ?;"
                        )
        ) {
            st.setTime(1, Time.valueOf(workShift.getStartTime()));
            st.setTime(2, Time.valueOf(workShift.getEndTime()));
            st.setDate(3, Date.valueOf(workShift.getWorkDate()));
            st.setInt(4, workShift.getId());
            st.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void insert(WorkShift workShift) {
        try (
                Connection conn = DriverManager.getConnection(dbPath);
                PreparedStatement st = conn.prepareStatement(
                        "INSERT INTO WORK_SHIFT (start_time, end_time, work_date) " +
                                "VALUES (?, ?, ?)"
                )
        ) {
            st.setTime(1, Time.valueOf(workShift.getStartTime()));
            st.setTime(2, Time.valueOf(workShift.getEndTime()));
            st.setDate(3, Date.valueOf(workShift.getWorkDate()));
            st.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
