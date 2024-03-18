package com.mongodb.starter.models;

import org.bson.types.ObjectId;

import java.util.Date;
import java.util.List;
import java.util.Objects;

public class EinkaufszettelEntity {

    private ObjectId id;
    private Boolean isChecked;
    private int anzahl;
    private String produkt;


    public EinkaufszettelEntity() {
    }

    public EinkaufszettelEntity(ObjectId id,
                                Boolean isChecked,
                                int anzahl,
                                String produkt) {
        this.id = id;
        this.isChecked = isChecked;
        this.anzahl = anzahl;
        this.produkt = produkt;
    }

    public ObjectId getId() {
        return id;
    }

    public EinkaufszettelEntity setId(ObjectId id) {
        this.id = id;
        return this;
    }

    public Boolean getStatus() {
        return isChecked;
    }

    public EinkaufszettelEntity setStatus(Boolean isChecked) {
        this.isChecked = isChecked;
        return this;
    }

    public int getAnzahl() {
        return anzahl;
    }

    public EinkaufszettelEntity setAnzahl(int anzahl) {
        this.anzahl = anzahl;
        return this;
    }

    public String getProdukt() {
        return produkt;
    }

    public EinkaufszettelEntity setProdukt(String produkt) {
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
        EinkaufszettelEntity einkaufszettelEntity = (EinkaufszettelEntity) o;
        return isChecked == einkaufszettelEntity.isChecked && Objects.equals(id, einkaufszettelEntity.id) && Objects.equals(anzahl,
                                                                                                einkaufszettelEntity.anzahl) && Objects.equals(
                produkt, einkaufszettelEntity.produkt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, isChecked, anzahl, produkt);
    }

}
