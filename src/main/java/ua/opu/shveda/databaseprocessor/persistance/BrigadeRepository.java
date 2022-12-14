package ua.opu.shveda.databaseprocessor.persistance;

import ua.opu.shveda.databaseprocessor.model.Brigade;
import ua.opu.shveda.databaseprocessor.model.Mapper;

import java.sql.*;
import java.util.List;
import java.util.Optional;

public class BrigadeRepository extends RepositoryBase<Brigade> {
    final WorkShiftRepository workShiftRepository = new WorkShiftRepository();

    final Mapper<Brigade> mapper = set -> new Brigade(
            set.getInt("id"),
            set.getInt("number"),
            workShiftRepository.findById(set.getInt("work_shift_id")).orElse(null)
    );

    public static boolean brigadeExistsById(int id) {
        try (
                Connection conn = DriverManager.getConnection(dbPath);
                Statement st = conn.createStatement()
        ) {
            st.execute(String.format("SELECT * FROM main.BRIGADE WHERE id = %d", id));
            return st.getResultSet().next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<Brigade> findById(int id) {
        try (
                Connection conn = DriverManager.getConnection(dbPath);
                PreparedStatement st = conn.prepareStatement(
                        "SELECT * " +
                                "FROM BRIGADE " +
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

    public List<Brigade> findAll() {
        return executeList("SELECT * FROM BRIGADE", mapper);
    }

    public void update(Brigade brigade) {
        executeUpdate(String.format("" +
                "UPDATE BRIGADE " +
                "SET number = %d, workShiftId = %d WHERE id = %d;",
                brigade.getNumber(), brigade.getWorkShift().getId(), brigade.getId()
        ));
    }

    public void insert(Brigade brigade) {
        executeUpdate(String.format("" +
                "INSERT INTO BRIGADE (number, work_shift_id) " +
                "VALUES (%d, %d)",
                brigade.getNumber(), brigade.getWorkShift().getId()
        ));
    }
}
