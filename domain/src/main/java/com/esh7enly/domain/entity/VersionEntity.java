package com.esh7enly.domain.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Entity
public class VersionEntity {

    @PrimaryKey(autoGenerate = true)
    @Expose
    @SerializedName("id")
    public int id;
    @Expose
    @SerializedName("service")
    public String service;
    @Expose
    @SerializedName("api")
    public String api;

    @Expose
    @SerializedName("service_update_num")
    public String service_update_num;


    public VersionEntity(String service, String api,String service_update_num) {
        this.service = service;
        this.api = api;
        this.service_update_num = service_update_num;
    }
}
