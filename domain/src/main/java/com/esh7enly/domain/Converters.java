package com.esh7enly.domain;

import androidx.room.TypeConverter;

import com.esh7enly.domain.entity.userservices.TypeValue;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


import java.lang.reflect.Type;
import java.util.List;

public class Converters {


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
}
