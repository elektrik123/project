package ua.opu.shveda.databaseprocessor.model;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;

public class Brigade {
    final SimpleIntegerProperty id;
    final SimpleIntegerProperty number;
    final SimpleObjectProperty<WorkShift> workShift;

    public Brigade(int id, int number, WorkShift workShift) {
        this.id = new SimpleIntegerProperty(id);
        this.number = new SimpleIntegerProperty(number);
        this.workShift = new SimpleObjectProperty<>(workShift);
    }

    public Brigade() {
        this(0, 0, null);
    }

    public WorkShift getWorkShift() {
        return workShift.get();
    }

    public SimpleObjectProperty<WorkShift> workShiftProperty() {
        return workShift;
    }

    public void setWorkShift(WorkShift workShift) {
        this.workShift.set(workShift);
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

    public int getNumber() {
        return number.get();
    }

    public SimpleIntegerProperty numberProperty() {
        return number;
    }

    public void setNumber(int number) {
        this.number.set(number);
    }
}
