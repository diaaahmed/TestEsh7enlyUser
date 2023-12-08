package com.esh7enly.domain.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class SpinnerModel implements Parcelable {

    private String id, aName,eName;

    public SpinnerModel(String id, String aName, String eName) {
        this.id = id;
        this.aName = aName;
        this.eName = eName;
    }

    protected SpinnerModel(Parcel in) {
        id = in.readString();
        aName = in.readString();
        eName = in.readString();

    }

    public static final Creator<SpinnerModel> CREATOR = new Creator<SpinnerModel>() {
        @Override
        public SpinnerModel createFromParcel(Parcel in) {
            return new SpinnerModel(in);
        }

        @Override
        public SpinnerModel[] newArray(int size) {
            return new SpinnerModel[size];
        }
    };

    public SpinnerModel() {

    }

    public String geteName() {
        return eName;
    }

    public void seteName(String eName) {
        this.eName = eName;
    }

    public String getaName() {
        return this.aName;
    }

    public void setaName(String aName) {
        this.aName = aName;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(aName);
        parcel.writeString(eName);


    }
}
