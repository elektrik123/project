package ua.opu.shveda.databaseprocessor.model;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.time.LocalDate;
import java.time.LocalTime;

public class WorkShift {
    final SimpleIntegerProperty id;
    final SimpleObjectProperty<LocalTime> startTime;
    final SimpleObjectProperty<LocalTime> endTime;
    final SimpleObjectProperty<LocalDate> workDate;

    public WorkShift() {
        this(0, null, null, null);
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

    public LocalTime getStartTime() {
        return startTime.get();
    }

    public SimpleObjectProperty<LocalTime> startTimeProperty() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime.set(startTime);
    }

    public LocalTime getEndTime() {
        return endTime.get();
    }

    public SimpleObjectProperty<LocalTime> endTimeProperty() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime.set(endTime);
    }

    public LocalDate getWorkDate() {
        return workDate.get();
    }

    public SimpleObjectProperty<LocalDate> workDateProperty() {
        return workDate;
    }

    public void setWorkDate(LocalDate workDate) {
        this.workDate.set(workDate);
    }

    public WorkShift(int id, LocalTime startTime, LocalTime endTime, LocalDate workDate) {
        this.id = new SimpleIntegerProperty(id);
        this.startTime = new SimpleObjectProperty<>(startTime);
        this.endTime = new SimpleObjectProperty<>(endTime);
        this.workDate = new SimpleObjectProperty<>(workDate);
    }
}
