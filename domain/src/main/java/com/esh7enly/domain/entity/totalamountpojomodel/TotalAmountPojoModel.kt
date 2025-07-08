package com.esh7enly.domain.entity.totalamountpojomodel

import android.os.Parcel
import android.os.Parcelable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class TotalAmountPojoModel(
    @SerialName("imei")
    val imei: String,
    @SerialName("amount")
    val amount: String,
    @SerialName("service_id")
    val serviceId: Int,
    @SerialName("inquiry_transaction_id")
    val inquiryTransactionId: Int,
    @SerialName("parameters")
    var attributes: List<Params>
) : Parcelable {

    constructor(parcel: Parcel) : this(
        imei = parcel.readString() ?: "",
        amount = parcel.readString() ?: "",
        serviceId = parcel.readInt(),
        inquiryTransactionId = parcel.readInt(),
        attributes = parcel.createTypedArrayList(Params.CREATOR) ?: emptyList()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(imei)
        parcel.writeString(amount)
        parcel.writeInt(serviceId)
        parcel.writeInt(inquiryTransactionId)
        parcel.writeTypedList(attributes)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<TotalAmountPojoModel> {
        override fun createFromParcel(parcel: Parcel): TotalAmountPojoModel {
            return TotalAmountPojoModel(parcel)
        }

        override fun newArray(size: Int): Array<TotalAmountPojoModel?> {
            return arrayOfNulls(size)
        }
    }
}

@Serializable
data class Params(
    @SerialName("key")
    val key: String,
    @SerialName("value")
    val value: String
) : Parcelable {

    constructor(parcel: Parcel) : this(
        key = parcel.readString() ?: "",
        value = parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(key)
        parcel.writeString(value)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Params> {
        override fun createFromParcel(parcel: Parcel): Params {
            return Params(parcel)
        }

        override fun newArray(size: Int): Array<Params?> {
            return arrayOfNulls(size)
        }
    }
}

