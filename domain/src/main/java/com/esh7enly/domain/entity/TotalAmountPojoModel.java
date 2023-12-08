package com.esh7enly.domain.entity;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TotalAmountPojoModel {

    public TotalAmountPojoModel()
    {
    }

    public TotalAmountPojoModel(String imei , int serviceId, String amount, List<Params> attributes) {
        this.imei = imei;
        this.serviceId = serviceId;
        this.amount = amount;
        this.attributes = attributes;

    }

    public TotalAmountPojoModel(String imei , int serviceId, String amount, int inquiryTransactionId, List<Params> attributes) {
        this.imei = imei;
        this.serviceId = serviceId;
        this.amount = amount;
        this.inquiryTransactionId = inquiryTransactionId;
        this.attributes = attributes;

    }

    public TotalAmountPojoModel(String imei , int serviceId, String amount)
    {
        this.imei = imei;
        this.serviceId = serviceId;
        this.amount = amount;
    }

    public TotalAmountPojoModel(String imei , int serviceId, String amount, int inquiryTransactionId) {
        this.imei = imei;
        this.serviceId = serviceId;
        this.amount = amount;
        this.inquiryTransactionId = inquiryTransactionId;
    }
    @SerializedName("imei")
    private String imei;
    @Expose
    @SerializedName("service_id")
    private int serviceId;
    @Expose
    @SerializedName("amount")
    private String amount;
    @Expose
    @SerializedName("inquiry_transaction_id")
    private int inquiryTransactionId;
    @Expose
    @SerializedName("parameters")
    private List<Params> attributes;

    public int getServiceId() {
        return serviceId;
    }

    public void setCategory(int serviceId) {
        this.serviceId = serviceId;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public int getInquiryTransactionId() {
        return inquiryTransactionId;
    }

    public void setInquiryTransactionId(int inquiryTransactionId) {
        this.inquiryTransactionId = inquiryTransactionId;
    }

    public List<Params> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<Params> attributes) {
        this.attributes = attributes;
    }

    public static class Params {
        @Expose
        @SerializedName("key")
        private String key;
        @Expose
        @SerializedName("value")
        private String value;

        public Params(String key, String value) {
            this.key = key;
            this.value = value;
        }

        public String getId() {
            return key;
        }

        public void setId(String id) {
            this.key = id;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}
