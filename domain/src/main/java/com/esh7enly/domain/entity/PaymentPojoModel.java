package com.esh7enly.domain.entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;


public class PaymentPojoModel {

    public PaymentPojoModel(int serviceId, String amount) {
    }

    public PaymentPojoModel(String imei, String printLang, int serviceId, String amount)
    {
        this.imei = imei;
        this.lang = printLang;
        this.serviceId = serviceId;
        this.amount = amount;
    }

    public PaymentPojoModel(String imei, String printLang, int serviceId, String amount,
                            List<TotalAmountPojoModel.Params> params)
    {
        this.imei = imei;
        this.lang = printLang;
        this.serviceId = serviceId;
        this.amount = amount;
        this.params = params;
        this.externalTransactionId = "";
    }

    public PaymentPojoModel(String imei, String printLang, int serviceId, String amount,
                            String paymentTransactionId,
                            String externalTransactionId,
                            List<TotalAmountPojoModel.Params> params)
    {
        this.imei = imei;
        this.lang = printLang;
        this.serviceId = serviceId;
        this.amount = amount;
        this.externalTransactionId = externalTransactionId;
        PaymentTransactionId = paymentTransactionId;
        this.params = params;
    }
    @Expose
    @SerializedName("imei")
    private String imei;
    @Expose
    @SerializedName("lang")
    private String lang;
    @Expose
    @SerializedName("service_id")
    private int serviceId;
    @Expose
    @SerializedName("amount")
    private String amount;
    @Expose
    @SerializedName("external_transaction_id")
    private String externalTransactionId;
    @Expose
    @SerializedName("inquiry_transaction_id")
    private String PaymentTransactionId;
    @Expose
    @SerializedName("parameters")
    private List<TotalAmountPojoModel.Params> params;

    public String getExternalTransactionId() {
        return externalTransactionId;
    }

    public void setExternalTransactionId(String externalTransactionId) {
        this.externalTransactionId = externalTransactionId;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public int getServiceId() {
        return serviceId;
    }

    public void setServiceId(int serviceId) {
        this.serviceId = serviceId;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getPaymentTransactionId() {
        return PaymentTransactionId;
    }

    public void setPaymentTransactionId(String PaymentTransactionId) {
        this.PaymentTransactionId = PaymentTransactionId;
    }

    public List<TotalAmountPojoModel.Params> getParams() {
        return params;
    }

    public void setParams(List<TotalAmountPojoModel.Params> params) {
        this.params = params;
    }

    public static class Params {
        @Expose
        @SerializedName("key")
        private int id;
        @Expose
        @SerializedName("value")
        private String value;

        public Params(int id, String value) {
            this.id = id;
            this.value = value;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}
