package com.example.plantwise

import android.os.Parcel
import android.os.Parcelable

data class PlantData(
    val id: Int,
    val commonName: String?,
    val scientificName: String,
    val family: String?,
    val genus: String?,
    val imageUrl: String?,
    val edible: Boolean?,
    val observations: String?,
    val growthForm: String?,
    val specifications: Specifications?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString(),
        parcel.readString()!!,
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Boolean::class.java.classLoader) as? Boolean,
        parcel.readString(),
        parcel.readString(),
        parcel.readParcelable(Specifications::class.java.classLoader)
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(commonName)
        parcel.writeString(scientificName)
        parcel.writeString(family)
        parcel.writeString(genus)
        parcel.writeString(imageUrl)
        parcel.writeValue(edible)
        parcel.writeString(observations)
        parcel.writeString(growthForm)
        parcel.writeParcelable(specifications, flags)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<PlantData> {
        override fun createFromParcel(parcel: Parcel): PlantData = PlantData(parcel)
        override fun newArray(size: Int): Array<PlantData?> = arrayOfNulls(size)
    }
}

data class Specifications(
    val growth_habit: String?,
    val toxicity: String?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(growth_habit)
        parcel.writeString(toxicity)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Specifications> {
        override fun createFromParcel(parcel: Parcel): Specifications = Specifications(parcel)
        override fun newArray(size: Int): Array<Specifications?> = arrayOfNulls(size)
    }
}
