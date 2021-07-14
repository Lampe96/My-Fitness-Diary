package it.app.myfitnessdiary.models

import android.os.Parcel
import android.os.Parcelable

data class ObjAthlete(
    val name: String?,
    val surname: String?,
    val dateOfBirth: String?,
    val height: Int?,
    val weight: Int?,
    val idAthlete: String?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(surname)
        parcel.writeString(dateOfBirth)
        parcel.writeValue(height)
        parcel.writeValue(weight)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ObjAthlete> {
        override fun createFromParcel(parcel: Parcel): ObjAthlete {
            return ObjAthlete(parcel)
        }

        override fun newArray(size: Int): Array<ObjAthlete?> {
            return arrayOfNulls(size)
        }
    }
}