package ua.opu.shveda.databaseprocessor.persistance;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Modality;
import ua.opu.shveda.databaseprocessor.model.Mapper;
import ua.opu.shveda.databaseprocessor.model.User;

import java.sql.*;
import java.util.List;
import java.util.Optional;

public class UserRepository extends RepositoryBase<User> {
    private final static String getAllSQL = "SELECT * FROM user";
    private final static String validateSQL = "SELECT EXISTS(SELECT * FROM user WHERE login = ? AND  password = ?) AS result";
    private final static String isExistSQL = "SELECT EXISTS(SELECT * FROM user WHERE login = ?) AS result";
    private final static String createSQL = "INSERT INTO user VALUES (?, ?, ?);";
    private final static String findByLoginSql = "SELECT * FROM user WHERE login = ?";
    private final static String updateSQL = "UPDATE user SET login = ?, password = ?, role = ? WHERE login = ?;";

    private final static Mapper<User> mapper = set -> new User(
            set.getString("login"),
            set.getString("password"),
            set.getString("role")
    );

    //Init database with admin user
    public UserRepository() {
        executeUpdate("""
                CREATE TABLE IF NOT EXISTS USER (
                    login VARCHAR(20) PRIMARY KEY,
                    password VARCHAR(20) NOT NULL,
                    role VARCHAR(10) CHECK ( role IN ('ADMIN', 'USER'))
                );""");
        if (findByLogin("admin") == null) {
            executeUpdate("INSERT INTO USER VALUES ('admin', 'password', 'ADMIN');");
        }
    }

    @Override
    public Optional<User> findById(int id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<User> findAll() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void update(User user) {
        throw new UnsupportedOperationException();
    }

    public static ObservableList<User> findAllObservable() {
        try (Connection conn = DriverManager.getConnection(dbPath)) {
            Statement st = conn.createStatement();
            st.execute(getAllSQL);
            ObservableList<User> users = FXCollections.observableArrayList();
            var rs = st.getResultSet();
            while (rs.next()) {
                users.add(mapper.map(rs));
            }
            rs.close();
            st.close();
            return users;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean validate(String login, String password) {
        try (Connection conn = DriverManager.getConnection(dbPath)) {
            PreparedStatement st = conn.prepareStatement(validateSQL);
            st.setString(1, login);
            st.setString(2, password);
            st.execute();
            var rs = st.getResultSet();
            System.out.println(rs.getBoolean(1));
            return rs.getBoolean(1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void create(User user) {
        try (Connection conn = DriverManager.getConnection(dbPath)) {
            PreparedStatement existSt = conn.prepareStatement(isExistSQL);
            existSt.setString(1, user.getLogin());
            existSt.execute();
            var rs = existSt.getResultSet();
            System.out.println(rs.getBoolean(1));
            if (rs.getBoolean("result")) {
                Alert exists = new Alert(Alert.AlertType.ERROR, "Користувач з таким логіном уже існує!", ButtonType.CLOSE);
                exists.initModality(Modality.APPLICATION_MODAL);
                exists.showAndWait();
                return;
            }
            PreparedStatement createSt = conn.prepareStatement(createSQL);
            createSt.setString(1, user.getLogin());
            createSt.setString(2, user.getPassword());
            createSt.setString(3, user.getRole());
            createSt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static User findByLogin(String login) {
        try (Connection conn = DriverManager.getConnection(dbPath)) {
            PreparedStatement st = conn.prepareStatement(findByLoginSql);
            st.setString(1, login);
            st.execute();
            ResultSet set = st.getResultSet();
            if (!set.next()) {
                return null;
            }
            return mapper.map(set);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void updateUser(User user) {
        updateUser(user, user.getLogin());
    }

    @Override
    public void insert(User user) {
        throw new UnsupportedOperationException();
    }

    public static void updateUser(User user, String login) {
        try (Connection conn = DriverManager.getConnection(dbPath)) {
            PreparedStatement st = conn.prepareStatement(updateSQL);
            st.setString(1, user.getLogin());
            st.setString(2, user.getPassword());
            st.setString(3, user.getRole());
            st.setString(4, login);
            st.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
