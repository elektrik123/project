package ua.opu.shveda.databaseprocessor.persistance;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ua.opu.shveda.databaseprocessor.model.Brigade;
import ua.opu.shveda.databaseprocessor.model.WorkShift;
import ua.opu.shveda.databaseprocessor.model.Worker;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static ua.opu.shveda.databaseprocessor.model.Mappers.mapBrigade;

public class BrigadeRepository implements Repository {
    private final static String getAllSql = "SELECT * FROM brigade";
    private final static String getByNumberSql = "SELECT * FROM brigade WHERE id_number = ?";
    private static String updateSql = "UPDATE brigade SET workers_cnt = workers_cnt WHERE id_number = ?";
    private static String updateWorkersSql = "UPDATE brigade SET workers_cnt = workers_cnt WHERE id_number = ?";
    private static String addWorkerSql = "INSERT INTO brigade_worker (brigade_id, worker_id) VALUES (?, ?)";
    private static String addWorkShifterSql = "INSERT INTO brigade_workshift (brigade_number, workshift_id) \n" +
            "VALUES (?, ?);";
    private static String removeWorkerSql = "DELETE FROM brigade_worker WHERE brigade_id = ? AND  worker_id = ?";
    private static String removeWorkShiftSql = "DELETE FROM brigade_workshift WHERE brigade_number = ? AND  workshift_id = ?";
    private static String findByWorkShiftSql = "SELECT * FROM brigade b\n" +
            "JOIN brigade_workshift bw on b.id_number = bw.brigade_number\n" +
            "WHERE workshift_id = ?;";

    private static String st1 = """
            SELECT DISTINCT id_number, workers_cnt
            FROM brigade
            JOIN brigade_workshift bw ON brigade.id_number = bw.brigade_number
            JOIN workshift w on w.id = bw.workshift_id
            WHERE w.daytime = 'Нічна';
            """;


    public static List<Brigade> findAll() {
        try (Connection conn = DriverManager.getConnection(dbPath)) {
            Statement st = conn.createStatement();
            st.execute(getAllSql);
            ObservableList<Brigade> brigades = FXCollections.observableArrayList();
            var rs = st.getResultSet();
            while (rs.next()) {
                var brigade = mapBrigade(rs);
                brigade.workShifts().addAll(WorkShiftRepository.getByBrigade(brigade.id()));
                brigade.workers().addAll(WorkerRepository.findAllByBrigade(brigade.id()));
                brigades.add(brigade);
            }
            rs.close();
            st.close();
            return brigades;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void addWorkers(Brigade brigade, List<Worker> workersToAdd) {
        brigade.workers().addAll(workersToAdd);
        applyWorkers(brigade, workersToAdd, addWorkerSql);
    }

    public static Optional<Brigade> findByNumber(int number) {
        try (Connection conn = DriverManager.getConnection(dbPath)) {
            PreparedStatement st = conn.prepareStatement(getByNumberSql);
            st.setInt(1, number);
            st.execute();
            var brigade = mapBrigade(st.getResultSet());
            brigade.workShifts().addAll(WorkShiftRepository.getByBrigade(brigade.id()));
            brigade.workers().addAll(WorkerRepository.findAllByBrigade(brigade.id()));
            return Optional.of(brigade);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void removeWorkers(Brigade brigade, List<Worker> toRemove) {
        brigade.workers().removeAll(toRemove);
        applyWorkers(brigade, toRemove, removeWorkerSql);
    }

    private static void applyWorkers(Brigade brigade, List<Worker> workers, String actionSql) {
        try (Connection conn = DriverManager.getConnection(dbPath)) {
            for (Worker w : workers) {
                PreparedStatement st = conn.prepareStatement(actionSql);
                st.setInt(1, brigade.id());
                st.setInt(2, w.getId());
                st.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static void applyWorkShifts(Brigade brigade, List<WorkShift> workShifts, String actionSql) {
        try (Connection conn = DriverManager.getConnection(dbPath)) {
            for (WorkShift w : workShifts) {
                PreparedStatement st = conn.prepareStatement(actionSql);
                st.setInt(1, brigade.id());
                st.setInt(2, w.getId());
                st.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void addWorkShifts(Brigade brigade, List<WorkShift> toAdd) {
        brigade.workShifts().addAll(toAdd);
        applyWorkShifts(brigade, toAdd, addWorkShifterSql);
    }

    public static void removeWorkShifts(Brigade brigade, List<WorkShift> toRemove) {
        brigade.workShifts().removeAll(toRemove);
        applyWorkShifts(brigade, toRemove, removeWorkShiftSql);
    }

    public static List<Brigade> findForDispatcher(WorkShift workShift) {
        try (Connection conn = DriverManager.getConnection(dbPath)) {
            PreparedStatement st = conn.prepareStatement(findByWorkShiftSql);
            st.setInt(1, workShift.getId());
            st.execute();
            var list = new ArrayList<Brigade>();
            var rs = st.getResultSet();
            while (rs.next()) {
                var brigade = mapBrigade(rs);
                brigade.workers().addAll(WorkerRepository.findAllByBrigade(brigade.id()));
                brigade.workShifts().addAll(WorkShiftRepository.getByBrigade(brigade.id()));
                list.add(brigade);

            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Brigade> statement1() {
        try (Connection conn = DriverManager.getConnection(dbPath)) {
            Statement st = conn.createStatement();
            st.execute(st1);
            var rs = st.getResultSet();
            List<Brigade> list = new ArrayList<>();
            while (rs.next()) {
                var brigade = mapBrigade(rs);
                brigade.workShifts().addAll(WorkShiftRepository.getByBrigade(brigade.id()));
                brigade.workers().addAll(WorkerRepository.findAllByBrigade(brigade.id()));
                list.add(brigade);
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

//    public static void update(Brigade brigade) {
//        try (Connection conn = DriverManager.getConnection(dbPath)) {
//            PreparedStatement st = conn.prepareStatement(updateSql);
//
//            st.executeUpdate();
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//    }
}
