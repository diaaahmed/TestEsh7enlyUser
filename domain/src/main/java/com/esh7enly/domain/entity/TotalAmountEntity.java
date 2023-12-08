package com.esh7enly.domain.entity;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Entity
public class TotalAmountEntity implements Parcelable {

    @Expose
    @PrimaryKey(autoGenerate = true)
    @SerializedName("id")
    public int id;
    @Expose
    @SerializedName("status")
    public boolean status;
    @Expose
    @SerializedName("code")
    public int code;
    @Expose
    @SerializedName("message")
    public String message;
    @Embedded(prefix = "data_")
    @Expose
    @SerializedName("data")
    public DataEntity data;
    @Embedded(prefix = "version_")
    @Expose
    @SerializedName("version")
    public VersionEntity version;

    public TotalAmountEntity() {
    }

    protected TotalAmountEntity(Parcel in) {
        id = in.readInt();
        status = in.readByte() != 0;
        code = in.readInt();
        message = in.readString();
    }

    public static final Creator<TotalAmountEntity> CREATOR = new Creator<TotalAmountEntity>() {
        @Override
        public TotalAmountEntity createFromParcel(Parcel in) {
            return new TotalAmountEntity(in);
        }

        @Override
        public TotalAmountEntity[] newArray(int size) {
            return new TotalAmountEntity[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeByte((byte) (status ? 1 : 0));
        parcel.writeInt(code);
        parcel.writeString(message);
    }

    public static class VersionEntity {
        @Expose
        @SerializedName("service")
        public String service;
        @Expose
        @SerializedName("api")
        public String api;
    }

    public static class DataEntity
    {
        @Expose
        @SerializedName("message")
        public String message;
        @Expose
        @SerializedName("code")
        public int code;
        @Expose
        @SerializedName("merchant")
        public String merchant;
        @Expose
        @SerializedName("agent")
        public String agent;
        @Expose
        @SerializedName("system")
        public String system;
        @Expose
        @SerializedName("paid_amount")
        public double paidAmount;
        @Expose
        @SerializedName("total_amount")
        public double totalAmount;
        @Expose
        @SerializedName("service_charge")
        public double serviceCharge;
        @Expose
        @SerializedName("amount")
        public double amount;
        @Expose
        @SerializedName("status")
        public boolean status;
    }


}
