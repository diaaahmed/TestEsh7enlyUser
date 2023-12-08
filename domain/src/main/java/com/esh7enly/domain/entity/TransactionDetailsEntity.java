package com.esh7enly.domain.entity;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

@Entity
public class TransactionDetailsEntity implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    @Expose
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

    public TransactionDetailsEntity() {
    }

    protected TransactionDetailsEntity(Parcel in) {
        id = in.readInt();
        status = in.readByte() != 0;
        code = in.readInt();
        message = in.readString();
    }

    public static final Creator<TransactionDetailsEntity> CREATOR = new Creator<TransactionDetailsEntity>() {
        @Override
        public TransactionDetailsEntity createFromParcel(Parcel in) {
            return new TransactionDetailsEntity(in);
        }

        @Override
        public TransactionDetailsEntity[] newArray(int size) {
            return new TransactionDetailsEntity[size];
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

    public static class DataEntity {
        @Expose
        @SerializedName("parameters")
        public List<ParametersEntity> parameters;
        @Expose
        @SerializedName("updated_at")
        public String updatedAt;
        @Expose
        @SerializedName("created_at")
        public String createdAt;
        @Expose
        @SerializedName("parent_merchant_id")
        public String parentMerchantId;
        @Expose
        @SerializedName("staff_id")
        public int staffId;
        @Expose
        @SerializedName("merchant_commission")
        public String merchantCommission;
        @Expose
        @SerializedName("agent_commission")
        public String agentCommission;
        @Expose
        @SerializedName("system_commission")
        public String systemCommission;
        @Expose
        @SerializedName("request_duration")
        public int requestDuration;
        @Expose
        @SerializedName("user_agent")
        public String userAgent;
        @Expose
        @SerializedName("ip")
        public String ip;
        @Expose
        @SerializedName("description")
        public String description;
        @Expose
        @SerializedName("response")
        public String response;
       /* @Expose
        @SerializedName("validation_error")
        public String validationError;*/
        @Expose
        @SerializedName("integration_provider_balance")
        public String integrationProviderBalance;
        @Expose
        @SerializedName("integration_provider_amount")
        public String integrationProviderAmount;
        @Expose
        @SerializedName("provider_transaction_id")
        public String providerTransactionId;
        @Expose
        @SerializedName("is_paid")
        public int isPaid;
        @Expose
        @SerializedName("balance_before")
        public String balanceBefore;
        @Expose
        @SerializedName("balance_after")
        public String balanceAfter;
        @Expose
        @SerializedName("max_amount")
        public String maxAmount;
        @Expose
        @SerializedName("min_amount")
        public String minAmount;
        @Expose
        @SerializedName("paid_amount")
        public String paidAmount;
        @Expose
        @SerializedName("total_amount")
        public String totalAmount;
        @Expose
        @SerializedName("service_charge")
        public String serviceCharge;
        @Expose
        @SerializedName("amount")
        public String amount;
        @Expose
        @SerializedName("client_number")
        public String clientNumber;
        @Expose
        @SerializedName("message")
        public String message;
        @Expose
        @SerializedName("status_code")
        public int statusCode;
        @Expose
        @SerializedName("status")
        public int status;
        @Expose
        @SerializedName("settlement_type")
        public int settlementType;
        @Expose
        @SerializedName("is_settled")
        public int isSettled;
        @Expose
        @SerializedName("external_transaction_id")
        public String externalTransactionId;
        @Expose
        @SerializedName("inquiry_transaction_id")
        public String inquiryTransactionId;
        @Expose
        @SerializedName("type")
        public int type;
        @Embedded(prefix = "merchant_")
        @Expose
        @SerializedName("merchant")
        public MerchantEntity merchant;
        @Embedded(prefix = "service_")
        @Expose
        @SerializedName("service")
        public ServiceEntity service;
        @Embedded(prefix = "integration_provider_")
        @Expose
        @SerializedName("integration_provider")
        public IntegrationProviderEntity integrationProvider;
        @Expose
        @SerializedName("id")
        public int id;
    }

    public static class ParametersEntity {


        @Expose
        @SerializedName("value")
        public String value;
        @Expose
        @SerializedName("key")
        public String key;
        @SerializedName("internal_id")
        @Expose
        public String internalId;
        @SerializedName("display_name")
        @Expose
        public String displayName;
    }

    public static class MerchantEntity {
        @Expose
        @SerializedName("name")
        public String name;
        @Expose
        @SerializedName("store_name")
        public String storeName;
        @Expose
        @SerializedName("id")
        public int id;
    }

    public static class ServiceEntity {
        @Embedded(prefix = "category_")
        @Expose
        @SerializedName("category")
        public CategoryEntity category;
        @Embedded(prefix = "provider_")
        @Expose
        @SerializedName("provider")
        public ProviderEntity provider;
        @Expose
        @SerializedName("powered_by")
        public String poweredBy;
        @Expose
        @SerializedName("footer_description")
        public String footerDescription;
        @Expose
        @SerializedName("description")
        public String description;
        @Expose
        @SerializedName("name")
        public String name;
        @Expose
        @SerializedName("type")
        public int type;
        @Expose
        @SerializedName("id")
        public int id;
    }

    public static class CategoryEntity {
        @Expose
        @SerializedName("icon")
        public String icon;
        @Expose
        @SerializedName("description")
        public String description;
        @Expose
        @SerializedName("name")
        public String name;
        @Expose
        @SerializedName("id")
        public int id;
    }

    public static class ProviderEntity {
        @Expose
        @SerializedName("logo")
        public String logo;
        @Expose
        @SerializedName("description")
        public String description;
        @Expose
        @SerializedName("name")
        public String name;
        @Expose
        @SerializedName("id")
        public int id;
    }

    public static class IntegrationProviderEntity {
        @Expose
        @SerializedName("description")
        public String description;
        @Expose
        @SerializedName("name")
        public String name;
        @Expose
        @SerializedName("id")
        public int id;
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
