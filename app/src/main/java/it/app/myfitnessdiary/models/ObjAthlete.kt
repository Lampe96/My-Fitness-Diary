package it.app.myfitnessdiary.models

import android.os.Parcel
import android.os.Parcelable

data class ObjAthlete(
    var name: String?= null,
    var surname: String? = null,
    var dateOfBirth: String? = null,
    var height: Int? = null,
    var weight: Int? = null,
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int,
    )

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