package ua.opu.shveda.databaseprocessor.model;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

public class Car {
    final SimpleIntegerProperty id;
    final SimpleStringProperty mark;
    final SimpleObjectProperty<Brigade> brigade;

    public Car() {
        this(0, null, null);
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

    public String getMark() {
        return mark.get();
    }

    public SimpleStringProperty markProperty() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark.set(mark);
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

    public Car(int id, String mark, Brigade brigade) {
        this.id = new SimpleIntegerProperty(id);
        this.mark = new SimpleStringProperty(mark);
        this.brigade = new SimpleObjectProperty<>(brigade);
    }
}



