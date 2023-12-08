package com.esh7enly.domain.entity;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Embedded;
import androidx.room.Entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Entity(primaryKeys = "id")
public class TransactionEntity implements Parcelable {

    @Expose
    @SerializedName("id")
    public int id;
    @Expose
    @Embedded(prefix = "service_")
    @SerializedName("service")
    public ServiceEntity service;
    @Expose
    @Embedded(prefix = "provider_")
    @SerializedName("provider")
    public ProviderEntity provider;
    @Expose
    @SerializedName("type")
    public int type;
    @Expose
    @SerializedName("status")
    public int status;
    @Expose
    @SerializedName("status_code")
    public int statusCode;
    @Expose
    @SerializedName("message")
    public String message;
    @Expose
    @SerializedName("client_number")
    public String clientNumber;
    @Expose
    @SerializedName("amount")
    public String amount;
    @Expose
    @SerializedName("total_amount")
    public String totalAmount;
    @Expose
    @SerializedName("paid_amount")
    public String paidAmount;
    @Expose
    @SerializedName("merchant_commission")
    public String merchantCommission;
    @Expose
    @SerializedName("created_at")
    public String createdAt;

    public static class ServiceEntity {
        @Expose
        @SerializedName("id")
        public int id;
        @Expose
        @SerializedName("name")
        public String name;
        @Expose
        @SerializedName("icon")
        public String icon;
    }

    public static class ProviderEntity {
        @Expose
        @SerializedName("id")
        public int id;
        @Expose
        @SerializedName("name")
        public String name;
        @Expose
        @SerializedName("logo")
        public String logo;

    }

    public TransactionEntity() {
    }

    protected TransactionEntity(Parcel in) {
        id = in.readInt();
        type = in.readInt();
        status = in.readInt();
        statusCode = in.readInt();
        message = in.readString();
        clientNumber = in.readString();
        amount = in.readString();
        totalAmount = in.readString();
        paidAmount = in.readString();
        merchantCommission = in.readString();
        createdAt = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(type);
        dest.writeInt(status);
        dest.writeInt(statusCode);
        dest.writeString(message);
        dest.writeString(clientNumber);
        dest.writeString(amount);
        dest.writeString(totalAmount);
        dest.writeString(paidAmount);
        dest.writeString(merchantCommission);
        dest.writeString(createdAt);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<TransactionEntity> CREATOR = new Creator<TransactionEntity>() {
        @Override
        public TransactionEntity createFromParcel(Parcel in) {
            return new TransactionEntity(in);
        }

        @Override
        public TransactionEntity[] newArray(int size) {
            return new TransactionEntity[size];
        }
    };
}
