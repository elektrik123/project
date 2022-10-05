package ua.opu.shveda.databaseprocessor.model;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

import java.time.LocalDate;

public class Patient {
    final SimpleIntegerProperty id;
    final SimpleStringProperty pib;
    final SimpleStringProperty sex;
    final SimpleObjectProperty<LocalDate> birthDate;
    final SimpleStringProperty phone;

    public Patient() {
        this(0, null, null, null, null);
    }

    public Patient(int id, String pib, LocalDate birthDate, String sex, String phone) {
        this.id = new SimpleIntegerProperty(id);
        this.pib = new SimpleStringProperty(pib);
        this.sex = new SimpleStringProperty(sex);
        this.birthDate = new SimpleObjectProperty<>(birthDate);
        this.phone = new SimpleStringProperty(phone);
    }

    public int getId() {
        return id.get();
    }

    public SimpleIntegerProperty idProperty() {
        return id;
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public String getPib() {
        return pib.get();
    }

    public SimpleStringProperty pibProperty() {
        return pib;
    }

    public void setPib(String pib) {
        this.pib.set(pib);
    }

    public String getSex() {
        return sex.get();
    }

    public SimpleStringProperty sexProperty() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex.set(sex);
    }

    public LocalDate getBirthDate() {
        return birthDate.get();
    }

    public SimpleObjectProperty<LocalDate> birthDateProperty() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate.set(birthDate);
    }

    public String getPhone() {
        return phone.get();
    }

    public SimpleStringProperty phoneProperty() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone.set(phone);
    }
}
