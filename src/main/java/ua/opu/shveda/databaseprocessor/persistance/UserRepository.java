package ua.opu.shveda.databaseprocessor.persistance;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Modality;
import ua.opu.shveda.databaseprocessor.model.User;

import java.sql.*;

import static ua.opu.shveda.databaseprocessor.model.Mappers.mapUser;

public class UserRepository implements Repository {
    private final static String getAllSQL = "SELECT * FROM user";
    private final static String validateSQL = "SELECT EXISTS(SELECT * FROM user WHERE login = ? AND  password = ?) AS result";
    private final static String isExistSQL = "SELECT EXISTS(SELECT * FROM user WHERE login = ?) AS result";
    private final static String createSQL = "INSERT INTO user VALUES (?, ?, ?);";
    private final static String findByLoginSql = "SELECT * FROM user WHERE login = ?";
    private final static String updateSQL = "UPDATE user SET login = ?, password = ?, role = ? WHERE login = ?;";

    public static ObservableList<User> findAll() {
        try (Connection conn = DriverManager.getConnection(dbPath)) {
            Statement st = conn.createStatement();
            st.execute(getAllSQL);
            ObservableList<User> users = FXCollections.observableArrayList();
            var rs = st.getResultSet();
            while (rs.next()) {
                users.add(mapUser(rs));
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
            return mapUser(st.getResultSet());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void update(User user) {
        update(user, user.getLogin());
    }

    public static void update(User user, String login) {
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
