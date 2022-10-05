package ua.opu.shveda.databaseprocessor.persistance;

import ua.opu.shveda.databaseprocessor.model.Mapper;
import ua.opu.shveda.databaseprocessor.model.Patient;

import java.sql.*;
import java.util.List;
import java.util.Optional;

public class PatientRepository extends RepositoryBase<Patient> {

    final Mapper<Patient> mapper = set -> new Patient(
            set.getInt("id"),
            set.getString("pib"),
            set.getDate("birth_date").toLocalDate(),
            set.getString("sex"),
            set.getString("phone")
    );

    public static boolean patientExistsById(int id) {
        try (
                Connection conn = DriverManager.getConnection(dbPath);
                Statement st = conn.createStatement()
        ) {
            st.execute(String.format("SELECT * FROM PATIENT WHERE id = %d", id));
            return st.getResultSet().next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<Patient> findById(int id) {
        try (
                Connection conn = DriverManager.getConnection(dbPath);
                PreparedStatement st = conn.prepareStatement(
                        "SELECT * " +
                                "FROM PATIENT " +
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

    public List<Patient> findAll() {
        return executeList("SELECT * FROM PATIENT", mapper);
    }

    public void update(Patient patient) {
        try (
                Connection conn = DriverManager.getConnection(dbPath);
                PreparedStatement st = conn.prepareStatement("" +
                        "UPDATE PATIENT " +
                        "SET pib = ?, sex = ?, birth_date = ?, phone = ? WHERE id = ?;")
                ) {
            st.setString(1, patient.getPib());
            st.setString(2, patient.getSex());
            st.setDate(3, Date.valueOf(patient.getBirthDate()));
            st.setString(4, patient.getPhone());
            st.setInt(5, patient.getId());
            st.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void insert(Patient patient) {
        try (
                Connection conn = DriverManager.getConnection(dbPath);
                PreparedStatement st = conn.prepareStatement("" +
                        "INSERT INTO PATIENT (pib, sex, birth_date, phone) " +
                        "VALUES (?, ?, ?, ?)")
        ) {
            st.setString(1, patient.getPib());
            st.setString(2, patient.getSex());
            st.setDate(3, Date.valueOf(patient.getBirthDate()));
            st.setString(4, patient.getPhone());
            st.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
