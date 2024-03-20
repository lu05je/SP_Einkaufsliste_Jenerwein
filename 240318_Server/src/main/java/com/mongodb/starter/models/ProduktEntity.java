package com.mongodb.starter.models;

import org.bson.types.ObjectId;

import java.util.Objects;

public class ProduktEntity {

    private ObjectId id;
    private Boolean isChecked;
    private int anzahl;
    private String produkt;


    public ProduktEntity() {
    }

    public ProduktEntity(ObjectId id,
                         int anzahl,
                         String produkt) {
        this.id = id;
        this.isChecked = false;
        this.anzahl = anzahl;
        this.produkt = produkt;
    }

    public ObjectId getId() {
        return id;
    }

    public ProduktEntity setId(ObjectId id) {
        this.id = id;
        return this;
    }

    public Boolean getStatus() {
        return isChecked;
    }

    public ProduktEntity setStatus(Boolean isChecked) {
        this.isChecked = isChecked;
        return this;
    }

    public int getAnzahl() {
        return anzahl;
    }

    public ProduktEntity setAnzahl(int anzahl) {
        this.anzahl = anzahl;
        return this;
    }

    public String getProdukt() {
        return produkt;
    }

    public ProduktEntity setProdukt(String produkt) {
        this.produkt = produkt;
        return this;
    }
    @Override
    public String toString() {
        return "Person{" + "id=" + id + ", Status='" + isChecked + '\'' + ", Anzahl='" + anzahl + '\'' + ", Produkt='" + produkt + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProduktEntity produktEntity = (ProduktEntity) o;
        return isChecked == produktEntity.isChecked && Objects.equals(id, produktEntity.id) && Objects.equals(anzahl,
                                                                                                produktEntity.anzahl) && Objects.equals(
                produkt, produktEntity.produkt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, isChecked, anzahl, produkt);
    }

}
