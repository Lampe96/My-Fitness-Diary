package it.app.myfitnessdiary.models

import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable

/**
 * Class used to create the object for the
 * searching of the exercises
 */

data class ObjSearchExercise(
    var muscle: String? = null,
    var nameExercise: String? = null,
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(muscle)
        parcel.writeString(nameExercise)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ObjSearchExercise> {
        override fun createFromParcel(parcel: Parcel): ObjSearchExercise {
            return ObjSearchExercise(parcel)
        }

        override fun newArray(size: Int): Array<ObjSearchExercise?> {
            return arrayOfNulls(size)
        }
    }
}