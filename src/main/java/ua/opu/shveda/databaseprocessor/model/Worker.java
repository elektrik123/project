package ua.opu.shveda.databaseprocessor.model;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

import java.time.LocalDate;

public class Worker {
    final SimpleIntegerProperty id;
    final SimpleStringProperty pib;
    final SimpleStringProperty phone;
    final SimpleStringProperty address;
    final SimpleObjectProperty<LocalDate> birthDate;
    final SimpleObjectProperty<Brigade> brigade;
    final SimpleStringProperty speciality;

    public Worker() {
        this(0, null, null, null, null, null, null);
    }

    public Brigade getBrigade() {
        return brigade.get();
    }

    public SimpleObjectProperty<Brigade> brigadeProperty() {
        return brigade;
    }

    public void setBrigade(Brigade brigade) {
        this.brigade.set(brigade);
    }

    public Worker(int id, String pib, String phone, String address, LocalDate birthDate, Brigade brigade, String speciality) {
        this.id = new SimpleIntegerProperty(id);
        this.pib = new SimpleStringProperty(pib);
        this.phone = new SimpleStringProperty(phone);
        this.address = new SimpleStringProperty(address);
        this.birthDate = new SimpleObjectProperty<>(birthDate);
        this.brigade = new SimpleObjectProperty<>(brigade);
        this.speciality = new SimpleStringProperty(speciality);
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

    public String getPhone() {
        return phone.get();
    }

    public SimpleStringProperty phoneProperty() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone.set(phone);
    }

    public String getAddress() {
        return address.get();
    }

    public SimpleStringProperty addressProperty() {
        return address;
    }

    public void setAddress(String address) {
        this.address.set(address);
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

    public String getSpeciality() {
        return speciality.get();
    }

    public SimpleStringProperty specialityProperty() {
        return speciality;
    }

    public void setSpeciality(String speciality) {
        this.speciality.set(speciality);
    }
}
