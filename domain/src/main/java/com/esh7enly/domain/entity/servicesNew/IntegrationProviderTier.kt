package com.esh7enly.domain.entity.servicesNew

import android.os.Parcel
import android.os.Parcelable

data class IntegrationProviderTier(
    val charge: String,
    val commission: String,
    val from: String,
    val to: String,
    val type: String
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()?: "",
        parcel.readString()?: "",
        parcel.readString()?: "",
        parcel.readString()?: "" ,
        parcel.readString()?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(charge)
        parcel.writeString(commission)
        parcel.writeString(from)
        parcel.writeString(to)
        parcel.writeString(type)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<IntegrationProviderTier> {
        override fun createFromParcel(parcel: Parcel): IntegrationProviderTier {
            return IntegrationProviderTier(parcel)
        }

        override fun newArray(size: Int): Array<IntegrationProviderTier?> {
            return arrayOfNulls(size)
        }
    }
}