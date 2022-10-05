package ua.opu.shveda.databaseprocessor.model;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

public class Diagnose {
    final SimpleIntegerProperty id;
    final SimpleStringProperty name;
    final SimpleObjectProperty<Worker> worker;
    final SimpleObjectProperty<Patient> patient;

    final SimpleStringProperty state;

    public Diagnose() {
        this(0, null, null, null, "");
    }

    public String getState() {
        return state.get();
    }

    public SimpleStringProperty stateProperty() {
        return state;
    }

    public void setState(String state) {
        this.state.set(state);
    }

    public Diagnose(int id, String name, Worker worker, Patient patient, String state) {
        this.id = new SimpleIntegerProperty(id);
        this.name = new SimpleStringProperty(name);
        this.worker = new SimpleObjectProperty<>(worker);
        this.patient = new SimpleObjectProperty<>(patient);
        this.state = new SimpleStringProperty(state);
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

    public String getName() {
        return name.get();
    }

    public SimpleStringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public Worker getWorker() {
        return worker.get();
    }

    public SimpleObjectProperty<Worker> workerProperty() {
        return worker;
    }

    public void setWorker(Worker worker) {
        this.worker.set(worker);
    }

    public Patient getPatient() {
        return patient.get();
    }

    public SimpleObjectProperty<Patient> patientProperty() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient.set(patient);
    }
}
