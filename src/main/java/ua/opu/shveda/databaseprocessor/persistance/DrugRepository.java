package ua.opu.shveda.databaseprocessor.persistance;

import ua.opu.shveda.databaseprocessor.model.Drug;
import ua.opu.shveda.databaseprocessor.model.Mapper;

import java.sql.*;
import java.util.List;
import java.util.Optional;

public class DrugRepository extends RepositoryBase<Drug> {
    final CallRepository callRepository = new CallRepository();

    final Mapper<Drug> mapper = set -> new Drug(
            set.getInt("id"),
            set.getString("name"),
            set.getString("dosage"),
            set.getFloat("price"),
            callRepository.findById(set.getInt("call_id")).orElse(null)
    );

// --Commented out by Inspection START (9/18/2022 4:54 PM):
//    public static boolean drugExistsById(int id) {
//        try (
//                Connection conn = DriverManager.getConnection(dbPath);
//                Statement st = conn.createStatement()
//        ) {
//            st.execute(String.format("SELECT * FROM DRUG WHERE id = %d", id));
//            return st.getResultSet().next();
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//    }
// --Commented out by Inspection STOP (9/18/2022 4:54 PM)

    public Optional<Drug> findById(int id) {
        try (
                Connection conn = DriverManager.getConnection(dbPath);
                PreparedStatement st = conn.prepareStatement(
                        "SELECT * " +
                                "FROM DRUG " +
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

    public List<Drug> findAll() {
        return executeList("SELECT * FROM DRUG", mapper);
    }

    public void update(Drug drug) {
        executeUpdate(String.format("" +
                        "UPDATE DRUG " +
                        "SET name = '%s', dosage = '%s' price = %f, call_id = %d WHERE id = %d;",
                drug.getName(), drug.getDosage(), drug.getPrice(), drug.getCall().getId(), drug.getId()
        ));
    }

    public void insert(Drug drug) {
        executeUpdate(String.format("" +
                        "INSERT INTO DRUG (name, dosage, price, call_id) " +
                        "VALUES ('%s', '%s', %f, %d)",
                drug.getName(), drug.getDosage(), drug.getPrice(), drug.getCall().getId()
        ));
    }
}
