package ua.opu.shveda.databaseprocessor.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.Objects;

public final class Worker {
    public enum Post {
        LEADER("Бригадир"), ELECTRICIAN("Електрик"), ENGINEER("Інженер"), DRIVER("Водій"),
        LOCKSMITH("Слюсар"), MECHANIC("Механік");

        Post(String s) {
            this.s = s;
        }

        public final String s;
    }
    SimpleIntegerProperty id;
    SimpleStringProperty name;
    SimpleStringProperty address;
    SimpleStringProperty phone;
    SimpleStringProperty post;

    public Worker(
            int id,
            String name,
            String address,
            String phone,
            String post
    ) {
        this.id = new SimpleIntegerProperty(id);
        this.name = new SimpleStringProperty(name);
        this.address = new SimpleStringProperty(address);
        this.phone = new SimpleStringProperty(phone);
        this.post = new SimpleStringProperty(post);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Worker) obj;
        return this.id == that.id &&
                Objects.equals(this.name, that.name) &&
                Objects.equals(this.address, that.address) &&
                Objects.equals(this.phone, that.phone) &&
                Objects.equals(this.post, that.post);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, address, phone, post);
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public void setAddress(String address) {
        this.address.set(address);
    }

    public void setPhone(String phone) {
        this.phone.set(phone);
    }

    public void setPost(String post) {
        this.post.set(post);
    }

    public int getId() {
        return id.get();
    }

    public SimpleIntegerProperty idProperty() {
        return id;
    }

    public String getName() {
        return name.get();
    }

    public SimpleStringProperty nameProperty() {
        return name;
    }

    public String getAddress() {
        return address.get();
    }

    public SimpleStringProperty addressProperty() {
        return address;
    }

    public String getPhone() {
        return phone.get();
    }

    public SimpleStringProperty phoneProperty() {
        return phone;
    }

    public String getPost() {
        return post.get();
    }

    public SimpleStringProperty postProperty() {
        return post;
    }
}
