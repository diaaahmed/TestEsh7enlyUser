package com.esh7enly.domain.entity;

import androidx.room.Entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Entity(primaryKeys = "id")
public class FawryEntity {
    @Expose
    @SerializedName("id")
    private int id;

    @Expose
    @SerializedName("imie")
    private String imie;

    @Expose
    @SerializedName("date")
    private Long date;

    public FawryEntity(int id, String imie, Long date) {
        this.id = id;
        this.imie = imie;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public String getImie() {
        return imie;
    }

    public void setImie(String imie) {
        this.imie = imie;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }
}
