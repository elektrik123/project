package ua.opu.shveda.databaseprocessor.model;

import javafx.beans.property.SimpleStringProperty;

public class User {
    public enum Role { ADMIN, USER, DISPATCHER, MANAGER }

    public SimpleStringProperty login;
    public SimpleStringProperty password;
    public SimpleStringProperty role;

    public User(String login, String password, String role) {
        this.login = new SimpleStringProperty(login);
        this.password = new SimpleStringProperty(password);
        this.role = new SimpleStringProperty(role);
    }

    public String getLogin() {
        return login.get();
    }

    public SimpleStringProperty loginProperty() {
        return login;
    }

    public void setLogin(String login) {
        this.login.set(login);
    }

    public String getPassword() {
        return password.get();
    }

    public SimpleStringProperty passwordProperty() {
        return password;
    }

    public void setPassword(String password) {
        this.password.set(password);
    }

    public String getRole() {
        return role.get();
    }

    public SimpleStringProperty roleProperty() {
        return role;
    }

    public void setRole(String role) {
        this.role.set(role);
    }
}
