package com.esh7enly.domain.entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;


public class TransactionApiResponse {

    @Expose
    @SerializedName("status")
    public boolean status;
    @Expose
    @SerializedName("code")
    public int code;
    @Expose
    @SerializedName("message")
    public String message;
    @Expose
    @SerializedName("data")
    public TransactionDataEntity data;
    @Expose
    @SerializedName("version")
    public VersionEntity version;

    public static class TransactionDataEntity
    {
        @Expose
        @SerializedName("data")
        public List<TransactionEntity> data;
        @Expose
        @SerializedName("total")
        public int total;
        @Expose
        @SerializedName("per_page")
        public int perPage;
        @Expose
        @SerializedName("last_page")
        public int lastPage;
        @Expose
        @SerializedName("current_page")
        public int currentPage;

        public List<TransactionEntity> getData() {
            return data;
        }

        public void setData(List<TransactionEntity> data) {
            this.data = data;
        }

        public TransactionDataEntity() {
            this.data = new ArrayList<>();
        }
    }

    public static class VersionEntity {
        @Expose
        @SerializedName("service")
        public String service;
        @Expose
        @SerializedName("api")
        public String api;
    }
}
