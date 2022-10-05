package ua.opu.shveda.databaseprocessor.persistance;

import ua.opu.shveda.databaseprocessor.model.Car;
import ua.opu.shveda.databaseprocessor.model.Mapper;

import java.sql.*;
import java.util.List;
import java.util.Optional;

public class CarRepository extends RepositoryBase<Car> {
    final BrigadeRepository brigadeRepository = new BrigadeRepository();

    final Mapper<Car> mapper = set -> new Car(
            set.getInt("id"),
            set.getString("mark"),
            brigadeRepository.findById(set.getInt("brigade_id")).orElse(null)
    );

    public static boolean carExistsById(int id) {
        try (
                Connection conn = DriverManager.getConnection(dbPath);
                Statement st = conn.createStatement()
        ) {
            st.execute(String.format("SELECT * FROM CAR WHERE id = %d", id));
            return st.getResultSet().next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<Car> findById(int id) {
        try (
                Connection conn = DriverManager.getConnection(dbPath);
                PreparedStatement st = conn.prepareStatement(
                        "SELECT * " +
                                "FROM CAR " +
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

    public List<Car> findAll() {
        return executeList("SELECT * FROM CAR", mapper);
    }

    public void update(Car car) {
        executeUpdate(String.format("" +
                        "UPDATE CAR " +
                        "SET mark = '%s', brigade_id = %d WHERE id = %d;",
                car.getMark(), car.getBrigade().getId(), car.getId()
        ));
    }

    public void insert(Car car) {
        executeUpdate(String.format("" +
                        "INSERT INTO CAR (mark, brigade_id) " +
                        "VALUES ('%s', %d)",
                car.getMark(), car.getBrigade().getId()
        ));
    }
}
