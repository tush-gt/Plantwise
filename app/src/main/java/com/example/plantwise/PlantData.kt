package com.example.plantwise

import android.os.Parcel
import android.os.Parcelable

data class PlantData(
    val commonName: String?,
    val scientificName: String,
    val image: String?, // matches "image" field from JSON
    val type: List<String>?,
    val use: String?,
    val waterNeeds: String?,
    val sunlightNeeds: String?,
    val careTips: String?,
    val storageTips: String?,
    val growthForm: String?,
    val specification: String?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString()!!,
        parcel.readString(),
        parcel.createStringArrayList(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(commonName)
        parcel.writeString(scientificName)
        parcel.writeString(image)
        parcel.writeStringList(type)
        parcel.writeString(use)
        parcel.writeString(waterNeeds)
        parcel.writeString(sunlightNeeds)
        parcel.writeString(careTips)
        parcel.writeString(storageTips)
        parcel.writeString(growthForm)
        parcel.writeString(specification)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<PlantData> {
        override fun createFromParcel(parcel: Parcel): PlantData = PlantData(parcel)
        override fun newArray(size: Int): Array<PlantData?> = arrayOfNulls(size)
    }
}
