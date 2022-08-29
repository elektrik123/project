package ua.opu.shveda.databaseprocessor.persistance;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ua.opu.shveda.databaseprocessor.model.Mappers;
import ua.opu.shveda.databaseprocessor.model.Worker;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static ua.opu.shveda.databaseprocessor.model.Mappers.mapWorker;

public class WorkerRepository implements Repository {
    private static final String getAllSQL = "SELECT * FROM worker";
    private static final String getAllFilterSQL = "SELECT * FROM worker " +
            "WHERE fullname LIKE '%%%s%%' AND address LIKE '%%%s%%' AND phone_number LIKE '%%%s%%' AND post LIKE '%%%s%%'";
    private static final String getByIdSQL = "SELECT * FROM worker WHERE id = ?;";
    private static final String insertSQL = "INSERT INTO worker (fullname, address, phone_number, post) VALUES (?, ?, ?, ?)";
    private static final String deleteSql = "DELETE FROM worker WHERE id = ?;";
    private static final String getNorBrigadeSql = """
            SELECT * FROM worker
            EXCEPT SELECT DISTINCT id, fullname, address, phone_number, post
            FROM worker JOIN brigade_worker bw ON worker.id = bw.worker_id WHERE brigade_id = ?;
            """;

    private static final String getForBrigadeSql = "SELECT * " +
            "FROM worker JOIN brigade_worker bw on worker.id = bw.worker_id WHERE brigade_id = ?;";

    private static final String updateSql = "UPDATE worker " +
            "SET fullname = ?, address = ?, phone_number = ?, post = ? WHERE id = ?;";

    private static final String statement2Sql = """
            SELECT fullname AS name, COUNT(brigade_id) AS b_count
            FROM worker
            JOIN brigade_worker bw on worker.id = bw.worker_id
            GROUP BY worker_id;
                        
            """;

    public static ObservableList<Worker> findAll() {
        try (Connection conn = DriverManager.getConnection(dbPath)) {
            Statement st = conn.createStatement();
            st.execute(getAllSQL);
            ObservableList<Worker> workers = FXCollections.observableArrayList();
            var rs = st.getResultSet();
            while (rs.next()) {
                workers.add(mapWorker(rs));
            }
            rs.close();
            st.close();
            return workers;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static class WorkerBrigadeCnt {
        public SimpleStringProperty name;
        public SimpleIntegerProperty brigadesCnt;

        public WorkerBrigadeCnt(String name, int brigadesCnt) {
            this.name = new SimpleStringProperty(name);
            this.brigadesCnt = new SimpleIntegerProperty(brigadesCnt);
        }

        public String getName() {
            return name.get();
        }

        public SimpleStringProperty nameProperty() {
            return name;
        }

        public int getBrigadesCnt() {
            return brigadesCnt.get();
        }

        public SimpleIntegerProperty brigadesCntProperty() {
            return brigadesCnt;
        }
    }

    public static List<WorkerBrigadeCnt> st2 () {
        try (Connection conn = DriverManager.getConnection(dbPath)) {
            Statement st = conn.createStatement();
            st.execute(statement2Sql);
            var rs = st.getResultSet();
            var list = new ArrayList<WorkerBrigadeCnt>();
            while (rs.next()) {
                list.add(Mappers.mapWBCnt(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void create(Worker worker) {
        try (Connection conn = DriverManager.getConnection(dbPath)) {
            PreparedStatement st = conn.prepareStatement(insertSQL);
            st.setString(1, worker.getName());
            st.setString(2, worker.getAddress());
            st.setString(3, worker.getPhone());
            st.setString(4, worker.getPost());
            st.executeUpdate();
            st.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Worker> findAllByBrigade(int id) {
        try (Connection conn = DriverManager.getConnection(dbPath)) {
            PreparedStatement st = conn.prepareStatement(getForBrigadeSql);
            st.setInt(1, id);
            st.execute();
            ObservableList<Worker> workers = FXCollections.observableArrayList();
            var rs = st.getResultSet();
            while (rs.next()) {
                workers.add(mapWorker(rs));
            }
            rs.close();
            st.close();
            return workers;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void update(Worker worker) {
        try (Connection conn = DriverManager.getConnection(dbPath)) {
            PreparedStatement st = conn.prepareStatement(updateSql);
            st.setString(1, worker.getName());
            st.setString(2, worker.getAddress());
            st.setString(3, worker.getPhone());
            st.setString(4, worker.getPost());
            st.setInt(5, worker.getId());
            st.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static Worker findById(int id) {
        try (Connection conn = DriverManager.getConnection(dbPath)) {
            PreparedStatement st = conn.prepareStatement(getByIdSQL);
            st.setInt(1, id);
            st.execute();
            return mapWorker(st.getResultSet());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Worker> findByIds(List<Integer> idsToAdd) {
        return idsToAdd.stream().map(WorkerRepository::findById).toList();
    }

    public static List<Worker> findAllByNotInBrigade(int number) {
        try (Connection conn = DriverManager.getConnection(dbPath)) {
            PreparedStatement st = conn.prepareStatement(getNorBrigadeSql);
            st.setInt(1, number);
            st.execute();
            var rs = st.getResultSet();
            List<Worker> list = new ArrayList<>();
            while (rs.next()) {
                list.add(mapWorker(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Worker> findAllFilter(Worker wf) {
        try (Connection conn = DriverManager.getConnection(dbPath)) {
            Statement st = conn.createStatement();
            st.execute(String.format(getAllFilterSQL,
                    wf.getName(), wf.getAddress(), wf.getPhone(), wf.getPost()
            ));

            List<Worker> workers = new ArrayList<>();

            var rs = st.getResultSet();
            while (rs.next()) {
                workers.add(mapWorker(rs));
            }
            rs.close();
            st.close();
            return workers;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void delete(Worker worker) {
        try (Connection conn = DriverManager.getConnection(dbPath)) {
            PreparedStatement st = conn.prepareStatement(deleteSql);
            st.setInt(1, worker.getId());
            st.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
