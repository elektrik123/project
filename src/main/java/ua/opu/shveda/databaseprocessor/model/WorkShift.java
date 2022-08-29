package ua.opu.shveda.databaseprocessor.model;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

import java.util.Date;

public class WorkShift {
    SimpleIntegerProperty id;
    SimpleObjectProperty<Date> date;
    SimpleStringProperty daytime;

    public WorkShift(int id, Date date, String daytime) {
        this.id = new SimpleIntegerProperty(id);
        this.date = new SimpleObjectProperty<>(date);
        this.daytime = new SimpleStringProperty(daytime);
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

    public Date getDate() {
        return date.get();
    }

    public SimpleObjectProperty<Date> dateProperty() {
        return date;
    }

    public void setDate(Date date) {
        this.date.set(date);
    }

    public String getDaytime() {
        return daytime.get();
    }

    public SimpleStringProperty daytimeProperty() {
        return daytime;
    }

    public void setDaytime(String daytime) {
        this.daytime.set(daytime);
    }
}
