package com.esh7enly.domain.entity.servicesNew

import android.os.Parcel
import android.os.Parcelable

data class ChargeTier(
    val amount: String,
    val from: String,
    val to: String,
    val type: String
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()?: "",
        parcel.readString()?: "",
        parcel.readString()?: "",
        parcel.readString() ?:""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(amount)
        parcel.writeString(from)
        parcel.writeString(to)
        parcel.writeString(type)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ChargeTier> {
        override fun createFromParcel(parcel: Parcel): ChargeTier {
            return ChargeTier(parcel)
        }

        override fun newArray(size: Int): Array<ChargeTier?> {
            return arrayOfNulls(size)
        }
    }
}