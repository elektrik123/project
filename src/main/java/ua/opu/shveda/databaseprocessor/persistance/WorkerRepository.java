package ua.opu.shveda.databaseprocessor.persistance;

import ua.opu.shveda.databaseprocessor.model.Mapper;
import ua.opu.shveda.databaseprocessor.model.Worker;

import java.sql.*;
import java.util.List;
import java.util.Optional;

public class WorkerRepository extends RepositoryBase<Worker> {
    final BrigadeRepository brigadeRepository = new BrigadeRepository();

    final Mapper<Worker> mapper = set -> new Worker(
            set.getInt("id"),
            set.getString("pib"),
            set.getString("phone"),
            set.getString("address"),
            set.getDate("birth_date").toLocalDate(),
            brigadeRepository.findById(set.getInt("brigade_number")).orElse(null),
            set.getString("speciality")
    );

    public static boolean workerExistsById(int id) {
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

    public Optional<Worker> findById(int id) {
        try (
                Connection conn = DriverManager.getConnection(dbPath);
                PreparedStatement st = conn.prepareStatement(
                        "SELECT * " +
                                "FROM WORKER " +
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

    public List<Worker> findAll() {
        return executeList("SELECT * FROM WORKER", mapper);
    }

    public void update(Worker worker) {
        executeUpdate(String.format("" +
                        "UPDATE WORKER " +
                        "SET pib = '%s', phone = '%s, address = '%s', birth_date = '%s'," +
                        " brigade_number = %d, speciality = '%s' WHERE id = %d;",
                worker.getPib(), worker.getPhone(), worker.getAddress(),
                worker.getBirthDate().format(dateFormat), worker.getBrigade().getId(),
                worker.getSpeciality(), worker.getId()
        ));
    }

    public void insert(Worker worker) {
        executeUpdate(String.format("" +
                        "INSERT INTO WORKER (pib, phone, address, birth_date, brigade_number, speciality) " +
                        "VALUES ('%s', '%s', '%s', '%s', %d, '%s')",
                worker.getPib(), worker.getPhone(), worker.getAddress(),
                worker.getBirthDate().format(dateFormat), worker.getBrigade().getId(),
                worker.getSpeciality()
        ));
    }
}
