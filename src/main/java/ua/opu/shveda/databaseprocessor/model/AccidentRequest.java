package ua.opu.shveda.databaseprocessor.model;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

import java.time.LocalDateTime;

public class AccidentRequest {
    private final SimpleIntegerProperty id;
    private final SimpleDoubleProperty cost;
    private final SimpleDoubleProperty bonus;
    private final SimpleStringProperty typeName;
    private final Request request;
    private final SimpleObjectProperty<LocalDateTime> datatime;
    private final SimpleObjectProperty<LocalDateTime> completed;
    private final SimpleStringProperty address;


    public AccidentRequest(int id, SimpleDoubleProperty cost, double bonus, String typeName, Request request) {
        this.id = new SimpleIntegerProperty(id);
        this.cost = cost;
        this.bonus = new SimpleDoubleProperty(bonus);
        this.typeName = new SimpleStringProperty(typeName);
        this.request = request;
        this.datatime = new SimpleObjectProperty<>(request.dateTime());
        this.completed = new SimpleObjectProperty<>(null);
        this.address = new SimpleStringProperty(request.address());
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

    public double getCost() {
        return cost.get();
    }

    public SimpleDoubleProperty costProperty() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost.set(cost);
    }

    public double getBonus() {
        return bonus.get();
    }

    public SimpleDoubleProperty bonusProperty() {
        return bonus;
    }

    public void setBonus(double bonus) {
        this.bonus.set(bonus);
    }

    public String getTypeName() {
        return typeName.get();
    }

    public SimpleStringProperty typeNameProperty() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName.set(typeName);
    }

    public Request getRequest() {
        return request;
    }

    public LocalDateTime getDatatime() {
        return datatime.get();
    }

    public SimpleObjectProperty<LocalDateTime> datatimeProperty() {
        return datatime;
    }

    public void setDatatime(LocalDateTime datatime) {
        this.datatime.set(datatime);
    }

    public LocalDateTime getCompleted() {
        return completed.get();
    }

    public SimpleObjectProperty<LocalDateTime> completedProperty() {
        return completed;
    }

    public void setCompleted(LocalDateTime completed) {
        this.completed.set(completed);
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
