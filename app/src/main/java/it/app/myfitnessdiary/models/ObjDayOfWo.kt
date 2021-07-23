package it.app.myfitnessdiary.models

import android.os.Parcel
import android.os.Parcelable

/**
 * Class used to create the objDayOfWO
 */

data class ObjDayOfWo(
    var nameOfDay: String? = null,
    var listOfExercise: ArrayList<ObjExercise?>? = null,
) : Parcelable {

    @Suppress("UNCHECKED_CAST")
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readArrayList(null) as? ArrayList<ObjExercise?>
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(nameOfDay)
        parcel.writeList(listOfExercise)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ObjDayOfWo> {
        override fun createFromParcel(parcel: Parcel): ObjDayOfWo {
            return ObjDayOfWo(parcel)
        }

        override fun newArray(size: Int): Array<ObjDayOfWo?> {
            return arrayOfNulls(size)
        }
    }
}
