package ua.opu.shveda.databaseprocessor.model;

import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

public class Drug {
    final SimpleIntegerProperty id;
    final SimpleStringProperty name;
    final SimpleStringProperty dosage;
    final SimpleFloatProperty price;
    final SimpleObjectProperty<Call> call;

    public Drug() {
        this(0, null, null, 0.0F, null);
    }

    public Drug(int id, String name, String dosage, float price, Call call) {
        this.id = new SimpleIntegerProperty(id);
        this.name = new SimpleStringProperty(name);
        this.dosage = new SimpleStringProperty(dosage);
        this.price = new SimpleFloatProperty(price);
        this.call = new SimpleObjectProperty<>(call);
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

    public String getDosage() {
        return dosage.get();
    }

    public SimpleStringProperty dosageProperty() {
        return dosage;
    }

    public void setDosage(String dosage) {
        this.dosage.set(dosage);
    }

    public float getPrice() {
        return price.get();
    }

    public SimpleFloatProperty priceProperty() {
        return price;
    }

    public void setPrice(float price) {
        this.price.set(price);
    }

    public Call getCall() {
        return call.get();
    }

    public SimpleObjectProperty<Call> callProperty() {
        return call;
    }

    public void setCall(Call call) {
        this.call.set(call);
    }
}
