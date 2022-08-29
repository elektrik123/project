package ua.opu.shveda.databaseprocessor.persistance;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ua.opu.shveda.databaseprocessor.model.Brigade;
import ua.opu.shveda.databaseprocessor.model.WorkShift;

import java.sql.*;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import static ua.opu.shveda.databaseprocessor.model.Mappers.mapWorkShift;

public class WorkShiftRepository implements Repository {
    private static final String getForBrigadeSql = "SELECT id, work_date, daytime FROM workshift ws " +
            "JOIN brigade_workshift bw ON ws.id = bw.workshift_id WHERE brigade_number = ?";

    private static final String getNotInBrigade = """
            SELECT * FROM workshift
            EXCEPT SELECT DISTINCT id, work_date, daytime
            FROM workshift JOIN brigade_workshift bw ON workshift.id = bw.workshift_id WHERE brigade_number = ?;
            """;

    public static List<WorkShift> getByBrigade(int brigadeNumber) {
        try (Connection conn = DriverManager.getConnection(dbPath)) {
            PreparedStatement st = conn.prepareStatement(getForBrigadeSql);
            st.setInt(1, brigadeNumber);
            st.execute();
            ObservableList<WorkShift> workShifts = FXCollections.observableArrayList();
            var rs = st.getResultSet();
            while (rs.next()) {
                workShifts.add(mapWorkShift(rs));
            }
            rs.close();
            st.close();
            return workShifts;
        } catch (SQLException | ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<WorkShift> findNotInBrigade(Brigade brigade) {
        try (Connection conn = DriverManager.getConnection(dbPath)) {
            PreparedStatement st = conn.prepareStatement(getNotInBrigade);
            st.setInt(1, brigade.id());
            st.execute();
            List<WorkShift> list = new ArrayList<>();
            ResultSet rs = st.getResultSet();
            while (rs.next()) {
                list.add(mapWorkShift(rs));
            }
            return list;
        } catch (SQLException | ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<WorkShift> findAll() {
        try (Connection conn = DriverManager.getConnection(dbPath)) {
            Statement st = conn.createStatement();
            st.execute("SELECT * FROM workshift;");
            List<WorkShift> list = new ArrayList<>();
            var rs = st.getResultSet();
            while (rs.next()) {
                list.add(mapWorkShift(rs));
            }
            return list;
        } catch (SQLException | ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
