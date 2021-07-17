package it.app.myfitnessdiary.models

import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable

/**
 * Class used to create object schedule
 */

data class ObjSchedule(
    var listOfDays: ArrayList<ObjDayOfWo>? = null,
) : Parcelable {

    @Suppress("UNCHECKED_CAST")
    constructor(parcel: Parcel) : this(
        parcel.readArrayList(null) as? ArrayList<ObjDayOfWo>
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeList(listOfDays)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ObjSchedule> {
        override fun createFromParcel(parcel: Parcel): ObjSchedule {
            return ObjSchedule(parcel)
        }

        override fun newArray(size: Int): Array<ObjSchedule?> {
            return arrayOfNulls(size)
        }
    }
}
