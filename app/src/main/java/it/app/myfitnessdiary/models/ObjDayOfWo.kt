package it.app.myfitnessdiary.models
import android.os.Parcel
import android.os.Parcelable

/**
 * Class used to create the objDayOfWO
 */

data class ObjDayOfWo(
    val nameOfDay: String?,
    var listOfExercise: ArrayList<ObjExercise?> = ArrayList(12),
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),

    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(nameOfDay)
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
