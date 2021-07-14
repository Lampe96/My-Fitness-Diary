package it.app.myfitnessdiary.models

import java.io.Serializable

/**
 * Class used to create object schedule
 */

data class ObjSchedule(
    var listOfDays: ArrayList<ObjDayOfWo>,
) : Serializable
