package com.esh7enly.domain.entity;

import androidx.room.TypeConverter;

import com.esh7enly.domain.entity.userservices.TypeValue;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;

public class Converters {

    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }

    @TypeConverter // note this annotation
    public String fromTypeValuesList(List<TypeValue> optionValues) {
        if (optionValues == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<TypeValue>>() {
        }.getType();
        String json = gson.toJson(optionValues, type);
        return json;
    }

    @TypeConverter // note this annotation
    public List<TypeValue> toTypeValuesList(String optionValuesString) {
        if (optionValuesString == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<TypeValue>>() {
        }.getType();
        List<TypeValue> typeValuesEntityList = gson.fromJson(optionValuesString, type);
        return typeValuesEntityList;
    }

    @TypeConverter // note this annotation
    public String fromParametersEntityList(List<PaymentEntity.ParametersEntity> optionValues) {
        if (optionValues == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<PaymentEntity.ParametersEntity>>() {
        }.getType();
        String json = gson.toJson(optionValues, type);
        return json;
    }

    @TypeConverter // note this annotation
    public List<PaymentEntity.ParametersEntity> toParametersEntityList(String optionValuesString) {
        if (optionValuesString == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<PaymentEntity.ParametersEntity>>() {
        }.getType();
        List<PaymentEntity.ParametersEntity> parametersEntityList = gson.fromJson(optionValuesString, type);
        return parametersEntityList;
    }

//    @TypeConverter // note this annotation
//    public String fromTransactionDetailsParametersEntityList(List<TransactionDetailsEntity.ParametersEntity> optionValues) {
//        if (optionValues == null) {
//            return (null);
//        }
//        Gson gson = new Gson();
//        Type type = new TypeToken<List<TransactionDetailsEntity.ParametersEntity>>() {
//        }.getType();
//        String json = gson.toJson(optionValues, type);
//        return json;
//    }
//
//    @TypeConverter // note this annotation
//    public List<TransactionDetailsEntity.ParametersEntity> toTransactionDetailsParametersEntityList(String optionValuesString) {
//        if (optionValuesString == null) {
//            return (null);
//        }
//        Gson gson = new Gson();
//        Type type = new TypeToken<List<TransactionDetailsEntity.ParametersEntity>>() {
//        }.getType();
//        List<TransactionDetailsEntity.ParametersEntity> parametersEntityList = gson.fromJson(optionValuesString, type);
//        return parametersEntityList;
//    }
//
//    @TypeConverter // note this annotation
//    public String fromMerchantWalletEntityList(List<MerchantEntity.WalletEntity> optionValues) {
//        if (optionValues == null) {
//            return (null);
//        }
//        Gson gson = new Gson();
//        Type type = new TypeToken<List<MerchantEntity.WalletEntity>>() {
//        }.getType();
//        String json = gson.toJson(optionValues, type);
//        return json;
//    }
//
//    @TypeConverter // note this annotation
//    public List<MerchantEntity.WalletEntity> toMerchantWalletEntityList(String optionValuesString) {
//        if (optionValuesString == null) {
//            return (null);
//        }
//        Gson gson = new Gson();
//        Type type = new TypeToken<List<MerchantEntity.WalletEntity>>() {
//        }.getType();
//        List<MerchantEntity.WalletEntity> parametersEntityList = gson.fromJson(optionValuesString, type);
//        return parametersEntityList;
//    }
//
//    @TypeConverter // note this annotation
//    public String fromPosBrandEntityList(List<UserEntity.PosBrandEntity> optionValues) {
//        if (optionValues == null) {
//            return (null);
//        }
//        Gson gson = new Gson();
//        Type type = new TypeToken<List<UserEntity.PosBrandEntity>>() {
//        }.getType();
//        String json = gson.toJson(optionValues, type);
//        return json;
//    }
//
//    @TypeConverter // note this annotation
//    public List<UserEntity.PosBrandEntity> toPosBrandEntityList(String optionValuesString) {
//        if (optionValuesString == null) {
//            return (null);
//        }
//        Gson gson = new Gson();
//        Type type = new TypeToken<List<UserEntity.PosBrandEntity>>() {
//        }.getType();
//        List<UserEntity.PosBrandEntity> posBrandEntityList = gson.fromJson(optionValuesString, type);
//        return posBrandEntityList;
//    }


}
