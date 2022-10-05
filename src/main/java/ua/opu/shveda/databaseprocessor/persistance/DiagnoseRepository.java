package ua.opu.shveda.databaseprocessor.persistance;

import ua.opu.shveda.databaseprocessor.model.Diagnose;
import ua.opu.shveda.databaseprocessor.model.Mapper;

import java.sql.*;
import java.util.List;
import java.util.Optional;

public class DiagnoseRepository extends RepositoryBase<Diagnose> {
    final WorkerRepository workerRepository = new WorkerRepository();
    final PatientRepository patientRepository = new PatientRepository();

    final Mapper<Diagnose> mapper = set -> new Diagnose(
            set.getInt("id"),
            set.getString("name"),
            workerRepository.findById(set.getInt("worker_id")).orElse(null),
            patientRepository.findById(set.getInt("patient_id")).orElse(null),
            set.getString("state")
    );

    public static boolean diagnoseExistsById(int id) {
        try (
                Connection conn = DriverManager.getConnection(dbPath);
                Statement st = conn.createStatement()
        ) {
            st.execute(String.format("SELECT * FROM DIAGNOSE WHERE id = %d", id));
            return st.getResultSet().next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<Diagnose> findById(int id) {
        try (
                Connection conn = DriverManager.getConnection(dbPath);
                PreparedStatement st = conn.prepareStatement(
                        "SELECT * " +
                                "FROM DIAGNOSE " +
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

    public List<Diagnose> findAll() {
        return executeList("SELECT * FROM DIAGNOSE", mapper);
    }

    public void update(Diagnose diagnose) {
        executeUpdate(String.format("" +
                        "UPDATE DIAGNOSE " +
                        "SET name = '%s', state = '%s, worker_id = %d, patient_id = %d WHERE id = %d;",
                diagnose.getName(), diagnose.getState(), diagnose.getWorker().getId(), diagnose.getWorker().getId(),
                diagnose.getId()
        ));
    }

    public void insert(Diagnose diagnose) {
        executeUpdate(String.format("" +
                        "INSERT INTO DIAGNOSE (name, state, worker_id, patient_id) " +
                        "VALUES ('%s', '%s', '%s', %d)",
                diagnose.getName(), diagnose.getState(), diagnose.getWorker().getId(), diagnose.getWorker().getId()
        ));
    }
}
