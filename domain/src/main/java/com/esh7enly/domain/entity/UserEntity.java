package com.esh7enly.domain.entity;

import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


import java.util.List;
@Entity
public class UserEntity {

    @Expose
    @PrimaryKey
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
    @Expose
    @Embedded(prefix = "data_")
    @SerializedName("data")
    public DataEntity data;
    @Embedded(prefix = "version_")
    @Expose
    @SerializedName("version")
    public VersionEntity version;
    @Expose
    @SerializedName("pos_brand")
    private List<PosBrandEntity> posBrand;

    public List<PosBrandEntity> getPosBrand() {
        return posBrand;
    }

    public void setPosBrand(List<PosBrandEntity> posBrand) {
        this.posBrand = posBrand;
    }

    public static class VersionEntity {
        @Expose
        @SerializedName("service")
        public String service;
        @Expose
        @SerializedName("api")
        public String api;
        @Expose
        @SerializedName("app")
        public String app;

        @Expose
        @SerializedName("service_update_num")
        public String service_update_num;
    }

    public UserEntity() {
    }

    public static class DataEntity {

        @Expose
        @SerializedName("setting")
        @Embedded(prefix = "setting")
        public SettingsEntity settingsEntity;

        @Expose
        @SerializedName("points")
        public String points;

        @Expose
        @SerializedName("is_login")
        public boolean isLogin;

        @Expose
        @SerializedName("merchant_category")
        @Embedded(prefix = "merchant_category")
        public MerchantCategoryEntity merchantCategory;

        @Expose
        @SerializedName("updated_at")
        public String updatedAt;

        @Expose
        @SerializedName("created_at")
        public String createdAt;

        @Expose
        @SerializedName("last_login")
        public String lastLogin;

        @Expose
        @SerializedName("creatable_id")
        public int creatableId;

        @Expose
        @SerializedName("creatable_type")
        public String creatableType;

        @Expose
        @SerializedName("staff_id")
        public int staffId;

        @Expose
        @SerializedName("contract_image")
        public String contractImage;

        @Expose
        @SerializedName("id_back_image")
        public String idBackImage;

        @Expose
        @SerializedName("id_front_image")
        public String idFrontImage;

        @Expose
        @SerializedName("status")
        public int status;

        @Expose
        @SerializedName("settlement_type")
        public int settlementType;

        @Expose
        @SerializedName("longitude")
        public String longitude;

        @Expose
        @SerializedName("latitude")
        public String latitude;

        @Expose
        @SerializedName("address")
        public String address;

        @Expose
        @SerializedName("password")
        public String password;

        @Expose
        @SerializedName("description")
        public String description;

        @Expose
        @SerializedName("must_change_password")
        public int mustChangePassword;

        @Expose
        @SerializedName("birth_date")
        public String birthDate;

        @Expose
        @SerializedName("mobile")
        public String mobile;

        @Expose
        @SerializedName("national_id")
        public String nationalId;

        @Expose
        @SerializedName("name")
        public String name;

        @Expose
        @SerializedName("store_name")
        public String storeName;

        @Ignore
        @Expose
        @SerializedName("platform_ids")
        public List<String> platformIds;

        @Expose
        @SerializedName("neighborhood_id")
        public int neighborhoodId;

        @Expose
        @SerializedName("merchant_type_id")
        public int merchantTypeId;

        @Expose
        @SerializedName("merchant_category_id")
        public int merchantCategoryId;

        @Expose
        @SerializedName("key")
        public String key;

        @Expose
        @SerializedName("token")
        public String token;

        @Expose
        @SerializedName("device_token")
        public String deviceToken;

        @Expose
        @SerializedName("id")
        public int id;


        public String getPoints() {
            return points;
        }

        public void setPoints(String points) {
            this.points = points;
        }

        public boolean getIsLogin() {
            return isLogin;
        }

        public void setIsLogin(boolean isLogin) {
            this.isLogin = isLogin;
        }

        public MerchantCategoryEntity getMerchantCategory() {
            return merchantCategory;
        }

        public void setMerchantCategory(MerchantCategoryEntity merchantCategory) {
            this.merchantCategory = merchantCategory;
        }

        public String getUpdatedAt() {
            return updatedAt;
        }

        public void setUpdatedAt(String updatedAt) {
            this.updatedAt = updatedAt;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        public String getLastLogin() {
            return lastLogin;
        }

        public void setLastLogin(String lastLogin) {
            this.lastLogin = lastLogin;
        }

        public int getCreatableId() {
            return creatableId;
        }

        public void setCreatableId(int creatableId) {
            this.creatableId = creatableId;
        }

        public String getCreatableType() {
            return creatableType;
        }

        public void setCreatableType(String creatableType) {
            this.creatableType = creatableType;
        }

        public int getStaffId() {
            return staffId;
        }

        public void setStaffId(int staffId) {
            this.staffId = staffId;
        }

        public String getContractImage() {
            return contractImage;
        }

        public void setContractImage(String contractImage) {
            this.contractImage = contractImage;
        }

        public String getIdBackImage() {
            return idBackImage;
        }

        public void setIdBackImage(String idBackImage) {
            this.idBackImage = idBackImage;
        }

        public String getIdFrontImage() {
            return idFrontImage;
        }

        public void setIdFrontImage(String idFrontImage) {
            this.idFrontImage = idFrontImage;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getLongitude() {
            return longitude;
        }

        public void setLongitude(String longitude) {
            this.longitude = longitude;
        }

        public String getLatitude() {
            return latitude;
        }

        public void setLatitude(String latitude) {
            this.latitude = latitude;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public int getMustChangePassword() {
            return mustChangePassword;
        }

        public void setMustChangePassword(int mustChangePassword) {
            this.mustChangePassword = mustChangePassword;
        }

        public String getBirthDate() {
            return birthDate;
        }

        public void setBirthDate(String birthDate) {
            this.birthDate = birthDate;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public String getNationalId() {
            return nationalId;
        }

        public void setNationalId(String nationalId) {
            this.nationalId = nationalId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getStoreName() {
            return storeName;
        }

        public void setStoreName(String storeName) {
            this.storeName = storeName;
        }

        public List<String> getPlatformIds() {
            return platformIds;
        }

        public void setPlatformIds(List<String> platformIds) {
            this.platformIds = platformIds;
        }

        public int getNeighborhoodId() {
            return neighborhoodId;
        }

        public void setNeighborhoodId(int neighborhoodId) {
            this.neighborhoodId = neighborhoodId;
        }

        public int getMerchantTypeId() {
            return merchantTypeId;
        }

        public void setMerchantTypeId(int merchantTypeId) {
            this.merchantTypeId = merchantTypeId;
        }

        public int getMerchantCategoryId() {
            return merchantCategoryId;
        }

        public void setMerchantCategoryId(int merchantCategoryId) {
            this.merchantCategoryId = merchantCategoryId;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getDeviceToken() {
            return deviceToken;
        }

        public void setDeviceToken(String deviceToken) {
            this.deviceToken = deviceToken;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getSettlementType() {
            return settlementType;
        }

        public void setSettlementType(int settlementType) {
            this.settlementType = settlementType;
        }

        public SettingsEntity getSettingsEntity() {
            return settingsEntity;
        }

        public void setSettingsEntity(SettingsEntity settingsEntity) {
            this.settingsEntity = settingsEntity;
        }
    }

    public static class MerchantCategoryEntity {
        @Expose
        @SerializedName("updated_at")
        public String updatedAt;
        @Expose
        @SerializedName("created_at")
        public String createdAt;
        @Expose
        @SerializedName("staff_id")
        public int staffId;
        @Expose
        @SerializedName("status")
        public int status;
        @Expose
        @SerializedName("description_en")
        public String descriptionEn;
        @Expose
        @SerializedName("description_ar")
        public String descriptionAr;
        @Expose
        @SerializedName("name_en")
        public String nameEn;
        @Expose
        @SerializedName("name_ar")
        public String nameAr;
        @Expose
        @SerializedName("id")
        public int id;
    }

    public static class PosBrandEntity {
        public PosBrandEntity() {
        }

        @Expose
        @SerializedName("id")
        public String id;
        @Expose
        @SerializedName("app_version")
        public String version;

    }

    public static class SettingsEntity {

        public SettingsEntity() {
        }

        @Expose
        @SerializedName("display_commission_in_total_amount")
        public String displayCommissionInTotalAmount;
        @Expose
        @SerializedName("display_commission_in_print")
        public String displayCommissionInPrint;

    }
}
