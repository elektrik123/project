package ua.opu.shveda.databaseprocessor.model;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

import java.time.LocalDate;
import java.time.LocalTime;

public class Call {
    final SimpleIntegerProperty id;
    final SimpleObjectProperty<LocalDate> date;
    final SimpleObjectProperty<LocalTime> time;
    final SimpleStringProperty address;

    public Brigade getBrigade() {
        return brigade.get();
    }

    public SimpleObjectProperty<Brigade> brigadeProperty() {
        return brigade;
    }

    public void setBrigade(Brigade brigade) {
        this.brigade.set(brigade);
    }

    final SimpleObjectProperty<Brigade> brigade;

    public Call() {
        this(0, null, null, "", null);
    }

    public Call(int id, LocalDate date, LocalTime time, String address, Brigade brigade) {
        this.id = new SimpleIntegerProperty(id);
        this.date = new SimpleObjectProperty<>(date);
        this.time = new SimpleObjectProperty<>(time);
        this.address = new SimpleStringProperty(address);
        this.brigade = new SimpleObjectProperty<>(brigade);
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

    public LocalDate getDate() {
        return date.get();
    }

    public SimpleObjectProperty<LocalDate> dateProperty() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date.set(date);
    }

    public LocalTime getTime() {
        return time.get();
    }

    public SimpleObjectProperty<LocalTime> timeProperty() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time.set(time);
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
}
