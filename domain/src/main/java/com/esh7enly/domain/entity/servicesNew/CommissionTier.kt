package com.esh7enly.domain.entity.servicesNew

import android.os.Parcel
import android.os.Parcelable

data class CommissionTier(
    val agent: String,
    val from: String,
    val merchant: String,
    val system: String,
    val to: String,
    val type: String
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()?: "",
        parcel.readString()?: "",
        parcel.readString()?: "",
        parcel.readString()?: "",
        parcel.readString()?: "",
        parcel.readString()?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(agent)
        parcel.writeString(from)
        parcel.writeString(merchant)
        parcel.writeString(system)
        parcel.writeString(to)
        parcel.writeString(type)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CommissionTier> {
        override fun createFromParcel(parcel: Parcel): CommissionTier {
            return CommissionTier(parcel)
        }

        override fun newArray(size: Int): Array<CommissionTier?> {
            return arrayOfNulls(size)
        }
    }
}