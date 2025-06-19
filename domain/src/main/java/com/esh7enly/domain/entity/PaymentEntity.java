package com.esh7enly.domain.entity;

import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

@Entity
public class PaymentEntity {
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
    @PrimaryKey(autoGenerate = true)
    @SerializedName("id")
    public int id;


    @Embedded(prefix = "data_")
    @Expose
    @SerializedName("data")
    public DataEntity data;

    public static class DataEntity {

        public DataEntity() {
        }

        @Ignore
        public DataEntity(List<ParametersEntity> parameters,
                          String createdAt,
                          String description,
                          double paidAmount,
                          double totalAmount,
                          double serviceCharge,
                          double amount,
                          String clientNumber,
                          String message,
                          String type,
                          ServiceEntity service,
                          int id) {

            this.parameters = parameters;
            this.createdAt = createdAt;
            this.description = description;
            this.paidAmount = paidAmount;
            this.totalAmount = totalAmount;
            this.serviceCharge = serviceCharge;
            this.amount = amount;
            this.clientNumber = clientNumber;
            this.service = service;
            this.type = type;
            this.id = id;

        }

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
        @SerializedName("imei")
        public String imei;
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
        @Expose
        @SerializedName("request")
        public String request;
        /* @Expose
         @SerializedName("validation_error")
         public String validationError;*/
        @Expose
        @SerializedName("integration_provider_balance")
        public String integrationProviderBalance;
        @Expose
        @SerializedName("integration_provider_amount")
        public double integrationProviderAmount;
        @Expose
        @SerializedName("provider_transaction_id")
        public String providerTransactionId;
        @Expose
        @SerializedName("balance_before")
        public double balanceBefore;
        @Expose
        @SerializedName("balance_after")
        public double balanceAfter;
        @Expose
        @SerializedName("max_amount")
        public double maxAmount;
        @Expose
        @SerializedName("min_amount")
        public double minAmount;
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
        @SerializedName("client_number")
        public String clientNumber;
        @Expose
        @SerializedName("status_code")
        public int statusCode;
        @Expose
        @SerializedName("status")
        public int status;
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
        public String type;
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
        @Ignore
        @Embedded(prefix = "extra_ddata_")
        @Expose
        @SerializedName("extra_ddata")
        public List<ExtraDataEntity> extraData;

        @Expose
        @SerializedName("id")
        public int id;

        public List<ParametersEntity> getParameters() {
            return parameters;
        }

        public void setParameters(List<ParametersEntity> parameters) {
            this.parameters = parameters;
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

        public String getParentMerchantId() {
            return parentMerchantId;
        }

        public void setParentMerchantId(String parentMerchantId) {
            this.parentMerchantId = parentMerchantId;
        }

        public int getStaffId() {
            return staffId;
        }

        public void setStaffId(int staffId) {
            this.staffId = staffId;
        }

        public String getMerchantCommission() {
            return merchantCommission;
        }

        public void setMerchantCommission(String merchantCommission) {
            this.merchantCommission = merchantCommission;
        }

        public String getAgentCommission() {
            return agentCommission;
        }

        public void setAgentCommission(String agentCommission) {
            this.agentCommission = agentCommission;
        }

        public String getSystemCommission() {
            return systemCommission;
        }

        public void setSystemCommission(String systemCommission) {
            this.systemCommission = systemCommission;
        }

        public String getImei() {
            return imei;
        }

        public void setImei(String imei) {
            this.imei = imei;
        }

        public int getRequestDuration() {
            return requestDuration;
        }

        public void setRequestDuration(int requestDuration) {
            this.requestDuration = requestDuration;
        }

        public String getUserAgent() {
            return userAgent;
        }

        public void setUserAgent(String userAgent) {
            this.userAgent = userAgent;
        }

        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getResponse() {
            return response;
        }

        public void setResponse(String response) {
            this.response = response;
        }

        public String getRequest() {
            return request;
        }

        public void setRequest(String request) {
            this.request = request;
        }

      /*  public String getValidationError() {
            return validationError;
        }

        public void setValidationError(String validationError) {
            this.validationError = validationError;
        }*/

        public String getIntegrationProviderBalance() {
            return integrationProviderBalance;
        }

        public void setIntegrationProviderBalance(String integrationProviderBalance) {
            this.integrationProviderBalance = integrationProviderBalance;
        }

        public double getIntegrationProviderAmount() {
            return integrationProviderAmount;
        }

        public void setIntegrationProviderAmount(double integrationProviderAmount) {
            this.integrationProviderAmount = integrationProviderAmount;
        }

        public String getProviderTransactionId() {
            return providerTransactionId;
        }

        public void setProviderTransactionId(String providerTransactionId) {
            this.providerTransactionId = providerTransactionId;
        }

        public double getMaxAmount() {
            return maxAmount;
        }

        public void setMaxAmount(double maxAmount) {
            this.maxAmount = maxAmount;
        }

        public double getMinAmount() {
            return minAmount;
        }

        public void setMinAmount(double minAmount) {
            this.minAmount = minAmount;
        }

        public double getPaidAmount() {
            return paidAmount;
        }

        public void setPaidAmount(double paidAmount) {
            this.paidAmount = paidAmount;
        }

        public double getTotalAmount() {
            return totalAmount;
        }

        public void setTotalAmount(double totalAmount) {
            this.totalAmount = totalAmount;
        }

        public double getServiceCharge() {
            return serviceCharge;
        }

        public void setServiceCharge(int serviceCharge) {
            this.serviceCharge = serviceCharge;
        }

        public double getAmount() {
            return amount;
        }

        public void setAmount(double amount) {
            this.amount = amount;
        }

        public String getClientNumber() {
            return clientNumber;
        }

        public void setClientNumber(String clientNumber) {
            this.clientNumber = clientNumber;
        }

        public int getStatusCode() {
            return statusCode;
        }

        public void setStatusCode(int statusCode) {
            this.statusCode = statusCode;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getExternalTransactionId() {
            return externalTransactionId;
        }

        public void setExternalTransactionId(String externalTransactionId) {
            this.externalTransactionId = externalTransactionId;
        }

        public String getInquiryTransactionId() {
            return inquiryTransactionId;
        }

        public void setInquiryTransactionId(String inquiryTransactionId) {
            this.inquiryTransactionId = inquiryTransactionId;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public MerchantEntity getMerchant() {
            return merchant;
        }

        public void setMerchant(MerchantEntity merchant) {
            this.merchant = merchant;
        }

        public ServiceEntity getService() {
            return service;
        }

        public void setService(ServiceEntity service) {
            this.service = service;
        }

        public IntegrationProviderEntity getIntegrationProvider() {
            return integrationProvider;
        }

        public void setIntegrationProvider(IntegrationProviderEntity integrationProvider) {
            this.integrationProvider = integrationProvider;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }

    public static class ParametersEntity {
        @Ignore
        public ParametersEntity(String value, String key) {
            this.value = value;
            this.key = key;
        }

        @Ignore
        public ParametersEntity(String value, String key, String internalId) {
            this.value = value;
            this.key = key;
            this.internalId = internalId;
        }

        @Ignore
        public ParametersEntity(String value, String key, String internalId, String displayName) {
            this.value = value;
            this.key = key;
            this.internalId = internalId;
            this.displayName = displayName;
        }
        @Expose
        @SerializedName("internal_id")
        public String internalId;
        @Expose
        @SerializedName("value")
        public String value;
        @Expose
        @SerializedName("key")
        public String key;

        @SerializedName("display_name")
        @Expose
        private String displayName;

        public Boolean isShow = true;

        public Boolean getShow() {
            if(isShow == null){
                return true;
            }else {
                return isShow;
            }

        }

        public void setShow(Boolean show) {
            isShow = show;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getKey() {
            return key;
        }

        public String getInternalId() {
            return internalId;
        }

        public void setInternalId(String internalId) {
            this.internalId = internalId;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getDisplayName() {
            return displayName;
        }

        public void setDisplayName(String displayName) {
            this.displayName = displayName;
        }
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

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
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
        @SerializedName("id")
        public int id;

        public ServiceEntity(CategoryEntity category,
                             ProviderEntity provider,
                             String poweredBy,
                             String footerDescription,
                             String description,
                             String name,
                             int id) {
            this.category = category;
            this.provider = provider;
            this.poweredBy = poweredBy;
            this.footerDescription = footerDescription;
            this.description = description;
            this.name = name;
            this.id = id;
        }

        public CategoryEntity getCategory() {
            return category;
        }

        public void setCategory(CategoryEntity category) {
            this.category = category;
        }

        public ProviderEntity getProvider() {
            return provider;
        }

        public void setProvider(ProviderEntity provider) {
            this.provider = provider;
        }

        public String getPoweredBy() {
            return poweredBy;
        }

        public void setPoweredBy(String poweredBy) {
            this.poweredBy = poweredBy;
        }

        public String getFooterDescription() {
            return footerDescription;
        }

        public void setFooterDescription(String footerDescription) {
            this.footerDescription = footerDescription;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }

    public static class CategoryEntity {


        public CategoryEntity(String icon, String description, String name, int id) {
            this.icon = icon;
            this.description = description;
            this.name = name;
            this.id = id;
        }

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

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }

    public static class ProviderEntity {

        public ProviderEntity(String logo, String description, String name, int id) {
            this.logo = logo;
            this.description = description;
            this.name = name;
            this.id = id;
        }

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

        public String getLogo() {
            return logo;
        }

        public void setLogo(String logo) {
            this.logo = logo;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }

    public static class IntegrationProviderEntity
    {
        @Expose
        @SerializedName("description")
        public String description;
        @Expose
        @SerializedName("name")
        public String name;
        @Expose
        @SerializedName("id")
        public int id;

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }

    public static class ExtraDataEntity {
        @Expose
        @SerializedName("key")
        public String key;
        @Expose
        @SerializedName("value")
        public String value;

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}
