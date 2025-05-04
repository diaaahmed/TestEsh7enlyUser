package com.esh7enly.domain.entity;


import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;



public class TotalAmountPojoModel implements Parcelable {

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

    protected TotalAmountPojoModel(Parcel in) {
        imei = in.readString();
        serviceId = in.readInt();
        amount = in.readString();
        attributes = in.createTypedArrayList(Params.CREATOR);
        inquiryTransactionId = in.readInt();
    }

    public static final Creator<TotalAmountPojoModel> CREATOR = new Creator<TotalAmountPojoModel>() {
        @Override
        public TotalAmountPojoModel createFromParcel(Parcel in) {
            return new TotalAmountPojoModel(in);
        }

        @Override
        public TotalAmountPojoModel[] newArray(int size) {
            return new TotalAmountPojoModel[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(imei);
        parcel.writeInt(serviceId);
        parcel.writeString(amount);
        parcel.writeTypedList(attributes);
        parcel.writeInt(inquiryTransactionId);
    }

    public static class Params implements Parcelable   {
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

        protected Params(Parcel in) {
            key = in.readString();
            value = in.readString();
        }

        public static final Creator<Params> CREATOR = new Creator<Params>() {
            @Override
            public Params createFromParcel(Parcel in) {
                return new Params(in);
            }

            @Override
            public Params[] newArray(int size) {
                return new Params[size];
            }
        };

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

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(@NonNull Parcel parcel, int i) {
            parcel.writeString(key);
            parcel.writeString(value);
        }
    }
}
