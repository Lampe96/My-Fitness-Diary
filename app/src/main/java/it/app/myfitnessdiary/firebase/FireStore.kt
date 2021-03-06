package it.app.myfitnessdiary.firebase

import android.util.Log
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import it.app.myfitnessdiary.models.ObjDayOfWo
import it.app.myfitnessdiary.models.ObjExercise
import it.app.myfitnessdiary.models.ObjSchedule
import it.app.myfitnessdiary.models.ObjSearchExercise

/**
 * In this class we implement all the method that we
 * use to interface us on fireStore
 */

class FireStore {

    private val TAG = "FIRESTORE"

    private val db = Firebase.firestore
    private val COLLECTIONATHLETE = "Athletes"
    private val COLLECTIONEXERCISES = "Exercises"

    private lateinit var listenerNameDayScheduleChange: ListenerRegistration

    //Adding a day on the schedule
    @Suppress("UNCHECKED_CAST")
    fun updateSchedule(athleteId: String, dayOfWo: ObjDayOfWo) {
        getAthlete(athleteId) { athlete ->
            val schedule = athlete?.get("Schedule")

            if (schedule != "") {
                //If is not the first day insert, we just concatenate the new
                //day to the old one
                schedule as HashMap<String, ArrayList<Any>>
                schedule["listOfDays"]?.add(dayOfWo)

                db.collection(COLLECTIONATHLETE).document(athleteId)
                    .update("Schedule", schedule)

            } else {
                //If is the first time we add the day to an empty list
                val firstWrite = ObjSchedule(ArrayList())
                firstWrite.listOfDays?.add(dayOfWo)

                db.collection(COLLECTIONATHLETE).document(athleteId)
                    .update("Schedule", firstWrite)
            }
        }
    }

    //To manage an existing schedule
    @Suppress("UNCHECKED_CAST")
            /*fun updateExistingSchedule(athleteId: String, dayOfWo: ObjDayOfWo) {
                getAthlete(athleteId) { athlete ->
                    val schedule = athlete!!["Schedule"]

                    schedule as HashMap<String, ArrayList<HashMap<String, Any>>>

                    //Getting the array called "listOfDays" on firestore
                    schedule["listOfDays"]!!.forEach { mapDay ->

                        //Looking for the right day, on the map inside listOfDays
                        if (mapDay["nameOfDay"]!! == dayOfWo.nameOfDay) {
                            mapDay["listOfExercise"] = dayOfWo.listOfExercise

                            db.collection(COLLECTIONATHLETE).document(athleteId)
                                .update("Schedule", schedule)
                        }
                    }
                }
            }*/

    //FUN USED FOR USER ATHLETE

    //Fun used to save the trainer on firestore
            /*fun saveAthlete(documentId: String, callback: (Boolean) -> Unit) {
                db.collection(COLLECTIONATHLETE).document(documentId).set()
                    .addOnSuccessListener {
                        Log.d(TAG, "The storing of the document: success")
                        callback(true)
                    }
                    .addOnFailureListener { e ->
                        Log.w(TAG, "The storing of the document: failed", e)
                        callback(false)
                    }
            }*/

    //Getting the document of athlete
    fun getAthlete(idAthlete: String, callback: (Map<String, Any>?) -> Unit) {
        db.collection(COLLECTIONATHLETE).document(idAthlete).get()
            .addOnSuccessListener { document ->
                Log.d(TAG, "Get athlete: success -> data: ${document.data}")
                callback(document.data)
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Get athlete: failed", e)
            }
    }

    //Fun used in profile athlete, for the eventual changes.
    fun updateAthlete(
        currentUserId: String,
        height: String,
        weight: String,
        typeOfWo: String,
        goal: String,
        level: String,
        numOfWO: String,
        listCheckBox: ArrayList<String>,
    ) {
        db.collection(COLLECTIONATHLETE).document(currentUserId).update(
            "Height",
            height,
            "Weight",
            weight,
            "TypeOfWO",
            typeOfWo,
            "Goal",
            goal,
            "Level",
            level,
            "DaysOfWorkout",
            numOfWO,
            "Equipment",
            listCheckBox
        )
    }

    //Fun in the option menu, for the delete on fireStore
    fun deleteAthlete(currentUserId: String) {
        db.collection(COLLECTIONATHLETE).document(currentUserId).delete()
            .addOnSuccessListener {
                Log.d(TAG, "Athlete delete: success")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Athlete delete: failed", e)
            }
    }

    //Fun for the delete of the day in the scheduleViewAthlete
    @Suppress("UNCHECKED_CAST")
    fun deleteDaySchedule(
        athleteId: String,
        nameOfDay: String,
        dayRemained: Int,
        callback: (Boolean) -> Unit,
    ) {
        var posToRemove = -1
        getAthlete(athleteId) { mapAthlete ->
            val schedule = mapAthlete?.get("Schedule")
            //We remove the day that match with the pressed one
            schedule as HashMap<String, ArrayList<HashMap<String, Any>>>
            schedule["listOfDays"]!!.forEach { hashMapDay ->
                if (hashMapDay["nameOfDay"] == nameOfDay) {
                    posToRemove = schedule["listOfDays"]!!.indexOf(hashMapDay)
                }
            }

            // If the position has changed it means that we found the day to remove
            if (posToRemove != -1) {
                if (dayRemained > 1) {
                    schedule["listOfDays"]!!.removeAt(posToRemove)

                    db.collection(COLLECTIONATHLETE).document(athleteId)
                        .update("Schedule", schedule)

                    callback(true)
                } else {
                    db.collection(COLLECTIONATHLETE).document(athleteId)
                        .update("Schedule", "", "TrainerId", "")

                    callback(true)
                }
            } else {
                callback(false)
            }
        }
    }

    //Getting the name of the day to fill the recycle in athlete side
    @Suppress("UNCHECKED_CAST")
    fun getNameDayScheduleAthlete(athleteId: String, callback: (ArrayList<String>) -> Unit) {
        getAthlete(athleteId) { athlete ->
            val schedule = athlete?.get("Schedule")
            //Declaring the list that is gonna be fill with the days
            val listOfDays = ArrayList<String>()

            if (schedule != "") {
                schedule as HashMap<String, ArrayList<HashMap<String, Any>>>
                schedule["listOfDays"]?.forEach { hashMapDay ->
                    listOfDays.add(hashMapDay["nameOfDay"].toString())
                    callback(listOfDays)
                }
            } else {
                callback(listOfDays)
            }
        }
    }

    //Getting the exercise from fireStore, to fill the field of
//the single exercise
    @Suppress("UNCHECKED_CAST")
    fun getScheduleExercise(
        athleteId: String,
        typeOfWo: String,
        callback: (ArrayList<ObjExercise>) -> Unit,
    ) {
        getAthlete(athleteId) { athlete ->
            val schedule = athlete?.get("Schedule")

            val listOfExercises = ArrayList<ObjExercise>()

            //This is the external map
            schedule as HashMap<String, ArrayList<HashMap<String, Any>>>

            //Getting the array called "listOfDays" on firestore
            schedule["listOfDays"]?.forEach { mapDay ->

                //Looking for the right day, on the map inside listOfDays
                if (mapDay["nameOfDay"]?.equals(typeOfWo) == true) {

                    //Getting the array containing all the map with the exercise
                    val dayMap = mapDay["listOfExercise"]
                    dayMap as ArrayList<HashMap<String, Any>>
                    dayMap.forEach { arrayExercise ->
                        //Adding map by map, creating the obj
                        listOfExercises.add(
                            ObjExercise(
                                arrayExercise["nameExercise"].toString(),
                                arrayExercise["numSeries"].toString(),
                                arrayExercise["numReps"].toString(),
                                arrayExercise["recovery"].toString().toInt()
                            )
                        )
                        callback(listOfExercises)
                    }
                }
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun setListenerNameDayScheduleChange(athleteId: String, callback: (ArrayList<String>) -> Unit) {
        listenerNameDayScheduleChange = db.collection(COLLECTIONATHLETE).document(athleteId)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w(TAG, "Listen failed athlete.", e)
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    Log.d(TAG, "Current data athlete: ${snapshot.data}")

                    val athlete = snapshot.data

                    val schedule = athlete?.get("Schedule")

                    //Declaring the list that is gonna be fill with the days
                    val listOfDays = ArrayList<String>()

                    if (schedule != "") {
                        schedule as HashMap<String, ArrayList<HashMap<String, Any>>>
                        schedule["listOfDays"]?.forEach { hashMapDay ->
                            listOfDays.add(hashMapDay["nameOfDay"].toString())
                            callback(listOfDays)
                        }
                    } else {
                        callback(listOfDays)
                    }
                } else {
                    Log.d(TAG, "Current data athlete: null")
                }
            }
    }

    fun removeListenerNameDayScheduleChange() {
        listenerNameDayScheduleChange.remove()
    }

    //Fun used to fill the list of the available exercise. It could be modified.
//to activate this method you have to remove comments in ActivitySearchExercise, line 40
    fun createListExerciseIta() {
        val listExercises = arrayListOf(
            ObjSearchExercise("Petto", "cavi alti"),
            ObjSearchExercise("Petto", "cavi bassi"),
            ObjSearchExercise("Petto", "croci panca piana"),
            ObjSearchExercise("Petto", "croci cavi panca piana"),
            ObjSearchExercise("Petto", "croci inclinata 45"),
            ObjSearchExercise("Petto", "distensioni panca piana"),
            ObjSearchExercise("Petto", "distensioni inclinata 45"),
            ObjSearchExercise("Petto", "panca piana"),
            ObjSearchExercise("Petto", "chest press"),
            ObjSearchExercise("Petto", "chest press inclinata"),
            ObjSearchExercise("Petto", "panca inclinata"),
            ObjSearchExercise("Petto", "pectoral machine"),
            ObjSearchExercise("Petto", "croci TRX"),
            ObjSearchExercise("Petto", "piegamenti"),
            ObjSearchExercise("Petto", "dips (Petto)"),
            ObjSearchExercise("Petto", "pullover"),
            ObjSearchExercise("Petto", "dips anelli (Petto)"),
            ObjSearchExercise("Petto", "piegamenti diamond (Petto)"),
            ObjSearchExercise("Petto", "panca declinata manubri"),
            ObjSearchExercise("Petto", "panca declinata bilanciere"),
            ObjSearchExercise("Petto", "piegamenti TRX"),
            ObjSearchExercise("Petto", "panca piana isometrica"),

            ObjSearchExercise("Gambe", "squat"),
            ObjSearchExercise("Gambe", "leg extensions"),
            ObjSearchExercise("Gambe", "affondi corpo libero"),
            ObjSearchExercise("Gambe", "affondi manubri"),
            ObjSearchExercise("Gambe", "affondi bilanciere"),
            ObjSearchExercise("Gambe", "pistol squat"),
            ObjSearchExercise("Gambe", "step up libero (Gambe)"),
            ObjSearchExercise("Gambe", "step up manubri (Gambe)"),
            ObjSearchExercise("Gambe", "box jump"),
            ObjSearchExercise("Gambe", "pressa"),
            ObjSearchExercise("Gambe", "pressa inclinata 45"),
            ObjSearchExercise("Gambe", "pressa 90"),
            ObjSearchExercise("Gambe", "pressa verticale"),
            ObjSearchExercise("Gambe", "hack squat"),
            ObjSearchExercise("Gambe", "pistol squat kettlebell"),
            ObjSearchExercise("Gambe", "hypetrust"),
            ObjSearchExercise("Gambe", "sumo squat"),
            ObjSearchExercise("Gambe", "stacco (Gambe)"),
            ObjSearchExercise("Gambe", "adduttori machine"),
            ObjSearchExercise("Gambe", "affondo inverso"),
            ObjSearchExercise("Gambe", "adduttori elastico"),
            ObjSearchExercise("Gambe", "squat frontale"),
            ObjSearchExercise("Gambe", "jump squat (Gambe)"),
            ObjSearchExercise("Gambe", "ponte glutei"),
            ObjSearchExercise("Gambe", "farmer walk"),
            ObjSearchExercise("Gambe", "squat statico"),

            ObjSearchExercise("Bicipiti", "curl manubri"),
            ObjSearchExercise("Bicipiti", "curl bilanciere"),
            ObjSearchExercise("Bicipiti", "hammer manubri"),
            ObjSearchExercise("Bicipiti", "curl concentrato"),
            ObjSearchExercise("Bicipiti", "curl cavi"),
            ObjSearchExercise("Bicipiti", "curl cavi corda"),
            ObjSearchExercise("Bicipiti", "curl elastici"),
            ObjSearchExercise("Bicipiti", "hammer elastici"),
            ObjSearchExercise("Bicipiti", "trazioni (Bicipiti)"),
            ObjSearchExercise("Bicipiti", "spider curl"),
            ObjSearchExercise("Bicipiti", "curl panca 45"),
            ObjSearchExercise("Bicipiti", "zottman Curl"),
            ObjSearchExercise("Bicipiti", "curl bilanciere ez"),
            ObjSearchExercise("Bicipiti", "curl TRX"),
            ObjSearchExercise("Bicipiti", "curl alternato"),
            ObjSearchExercise("Bicipiti", "chin-up"),
            ObjSearchExercise("Bicipiti", "chin-up isometrico"),

            ObjSearchExercise("Dorso", "stacchi regular"),
            ObjSearchExercise("Dorso", "stacchi sumo"),
            ObjSearchExercise("Dorso", "stacchi rumeni"),
            ObjSearchExercise("Dorso", "lat machine"),
            ObjSearchExercise("Dorso", "lat machine T-Bar"),
            ObjSearchExercise("Dorso", "rematore manubri"),
            ObjSearchExercise("Dorso", "rematore cavi"),
            ObjSearchExercise("Dorso", "trazioni (Dorso)"),
            ObjSearchExercise("Dorso", "rematore bilanciere"),
            ObjSearchExercise("Dorso", "rematore su panca"),
            ObjSearchExercise("Dorso", "rematore bilanciere presa stretta"),
            ObjSearchExercise("Dorso", "push-down corda"),
            ObjSearchExercise("Dorso", "butterflies (Dorso)"),
            ObjSearchExercise("Dorso", "rematore manubri singolo"),
            ObjSearchExercise("Dorso", "rematore manubri inverso"),
            ObjSearchExercise("Dorso", "rematore bilanciere presa stretta"),
            ObjSearchExercise("Dorso", "rematore renegade"),
            ObjSearchExercise("Dorso", "rematore TRX"),
            ObjSearchExercise("Dorso", "hyperextension"),
            ObjSearchExercise("Dorso", "rematore T-Bar"),

            ObjSearchExercise("Tricipiti", "panca piana presa stretta"),
            ObjSearchExercise("Tricipiti", "panca inclinata presa stretta"),
            ObjSearchExercise("Tricipiti", "french press bilanciere"),
            ObjSearchExercise("Tricipiti", "tricipiti cavi"),
            ObjSearchExercise("Tricipiti", "french press cavi"),
            ObjSearchExercise("Tricipiti", "tricipiti cavi con corda"),
            ObjSearchExercise("Tricipiti", "tricipiti TRX"),
            ObjSearchExercise("Tricipiti", "dips (Tricipiti)"),
            ObjSearchExercise("Tricipiti", "piegamenti diamonds"),
            ObjSearchExercise("Tricipiti", "tricipiti alla panca"),
            ObjSearchExercise("Tricipiti", "press manubri"),
            ObjSearchExercise("Tricipiti", "piegamenti tiger (Tricipiti)"),
            ObjSearchExercise("Tricipiti", "french press manubri"),
            ObjSearchExercise("Tricipiti", "piegamenti diamond rialzo (Tricipiti)"),

            ObjSearchExercise("Spalle", "alzate laterali"),
            ObjSearchExercise("Spalle", "alzate frontali"),
            ObjSearchExercise("Spalle", "spinte manubri"),
            ObjSearchExercise("Spalle", "arnold con manubri"),
            ObjSearchExercise("Spalle", "tirate al mento manubri"),
            ObjSearchExercise("Spalle", "tirate al mento bilanciere"),
            ObjSearchExercise("Spalle", "alzate laterali cavi"),
            ObjSearchExercise("Spalle", "alzate frontali cavi"),
            ObjSearchExercise("Spalle", "military press bilanciere"),
            ObjSearchExercise("Spalle", "alzate frontali disco"),
            ObjSearchExercise("Spalle", "butterfly (Spalle)"),
            ObjSearchExercise("Spalle", "alzate frontali elastici"),
            ObjSearchExercise("Spalle", "alzate laterali elastici"),
            ObjSearchExercise("Spalle", "pressa spalle macchinario"),
            ObjSearchExercise("Spalle", "military press manubri"),
            ObjSearchExercise("Spalle", "military press isometrico"),

            ObjSearchExercise("Polpacci", "polpacci su disco"),
            ObjSearchExercise("Polpacci", "calf machine"),
            ObjSearchExercise("Polpacci", "camminata punta di piedi"),
            ObjSearchExercise("Polpacci", "jump squat (Polpacci)"),
            ObjSearchExercise("Polpacci", "alzate in punta di piedi bilanciere"),
            ObjSearchExercise("Polpacci", "alzate in punta di piedi manubri"),


            ObjSearchExercise("Addominali", "crunch"),
            ObjSearchExercise("Addominali", "bicicletta (Addominali)"),
            ObjSearchExercise("Addominali", "addominali laterali"),
            ObjSearchExercise("Addominali", "plank"),
            ObjSearchExercise("Addominali", "plank laterale"),
            ObjSearchExercise("Addominali", "plank con salto"),
            ObjSearchExercise("Addominali", "sit-up"),
            ObjSearchExercise("Addominali", "super-man plank"),
            ObjSearchExercise("Addominali", "plank con rotazione bacino"),
            ObjSearchExercise("Addominali", "mountain-climber"),

            ObjSearchExercise("Cardio", "tapis roulant"),
            ObjSearchExercise("Cardio", "cyclette"),
            ObjSearchExercise("Cardio", "corda"),
            ObjSearchExercise("Cardio", "ellittica"),
            ObjSearchExercise("Cardio", "step-up (Cardio)"),
            ObjSearchExercise("Cardio", "corsa"),
            ObjSearchExercise("Cardio", "jogging"),
            ObjSearchExercise("Cardio", "spinning"),
            ObjSearchExercise("Cardio", "vogatore"),
            ObjSearchExercise("Cardio", "marcia"),
            ObjSearchExercise("Cardio", "skip"),
            ObjSearchExercise("Cardio", "corsa calciata"),
            ObjSearchExercise("Cardio", "suicidio"),
            ObjSearchExercise("Cardio", "burpees"),
            ObjSearchExercise("Cardio", "shadow boxe"),
            ObjSearchExercise("Cardio", "jumping jacks"),
            ObjSearchExercise("Cardio", "jab-diretto"),
            ObjSearchExercise("Cardio", "gancio-montante"),
            ObjSearchExercise("Cardio", "tiro al sacco")
        )
        db.collection(COLLECTIONEXERCISES).document("ITA")
            .update("ListaEsercizi", listExercises)
            .addOnSuccessListener {
                Log.d(TAG, "Storing document exercises: success")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Storing document exercises: failed", e)
            }

    }

    //Fun used to fill the list of the available exercise. It could be modified.
//to activate this method you have to remove comments in ActivitySearchExercise, line 40
    fun createListExerciseEng() {
        val listExercises = arrayListOf(
            ObjSearchExercise("Chest", "high cables"),
            ObjSearchExercise("Chest", "bass cables"),
            ObjSearchExercise("Chest", "flat bench crosses"),
            ObjSearchExercise("Chest", "cables flat bench crosses"),
            ObjSearchExercise("Chest", "inclined bench crosses 45"),
            ObjSearchExercise("Chest", "flat bench press"),
            ObjSearchExercise("Chest", "inclined bench press 45"),
            ObjSearchExercise("Chest", "flat bench press barbell"),
            ObjSearchExercise("Chest", "chest press"),
            ObjSearchExercise("Chest", "inclined chest press"),
            ObjSearchExercise("Chest", "inclined bench press barbell"),
            ObjSearchExercise("Chest", "pectoral machine"),
            ObjSearchExercise("Chest", "trx crosses"),
            ObjSearchExercise("Chest", "push-ups"),
            ObjSearchExercise("Chest", "dips (Chest)"),
            ObjSearchExercise("Chest", "pullover"),
            ObjSearchExercise("Chest", "dips rings (Chest)"),
            ObjSearchExercise("Chest", "diamonds push-ups (Chest)"),
            ObjSearchExercise("Chest", "barbell decline bench"),
            ObjSearchExercise("Chest", "dumbbells decline bench"),
            ObjSearchExercise("Chest", "push-up TRX"),
            ObjSearchExercise("Chest", "isometric bench press barbell"),

            ObjSearchExercise("Legs", "squat"),
            ObjSearchExercise("Legs", "leg extensions"),
            ObjSearchExercise("Legs", "bodyweight lunges"),
            ObjSearchExercise("Legs", "barbell lunges"),
            ObjSearchExercise("Legs", "dumbbells lunges"),
            ObjSearchExercise("Legs", "pistol squat"),
            ObjSearchExercise("Legs", "step up bodyweight (Legs)"),
            ObjSearchExercise("Legs", "step up dumbbells (Legs)"),
            ObjSearchExercise("Legs", "box jump"),
            ObjSearchExercise("Legs", "press"),
            ObjSearchExercise("Legs", "inclined press 45"),
            ObjSearchExercise("Legs", "90 press"),
            ObjSearchExercise("Legs", "vertical press"),
            ObjSearchExercise("Legs", "hack squat"),
            ObjSearchExercise("Legs", "pistol squat kettlebell"),
            ObjSearchExercise("Legs", "hypetrust"),
            ObjSearchExercise("Legs", "sumo squat"),
            ObjSearchExercise("Legs", "dead-lift (Legs)"),
            ObjSearchExercise("Legs", "abductors machine"),
            ObjSearchExercise("Legs", "reverse lunge"),
            ObjSearchExercise("Legs", "abductors elastic"),
            ObjSearchExercise("Legs", "frontal squat"),
            ObjSearchExercise("Legs", "jump squat (Legs)"),
            ObjSearchExercise("Legs", "glute bridge"),
            ObjSearchExercise("Legs", "farmer walk"),
            ObjSearchExercise("Legs", "static squat"),

            ObjSearchExercise("Biceps", "curl barbell"),
            ObjSearchExercise("Biceps", "curl dumbbells"),
            ObjSearchExercise("Biceps", "hammer dumbbells"),
            ObjSearchExercise("Biceps", "concentrated curl"),
            ObjSearchExercise("Biceps", "cables curl"),
            ObjSearchExercise("Biceps", "cables rope curl"),
            ObjSearchExercise("Biceps", "curl elastic"),
            ObjSearchExercise("Biceps", "hammer elastic"),
            ObjSearchExercise("Biceps", "pull-ups (Biceps)"),
            ObjSearchExercise("Biceps", "spider curl"),
            ObjSearchExercise("Biceps", "curl bench 45"),
            ObjSearchExercise("Biceps", "zottman curl"),
            ObjSearchExercise("Biceps", "curl ez barbell"),
            ObjSearchExercise("Biceps", "curl TRX"),
            ObjSearchExercise("Biceps", "alternate curl"),
            ObjSearchExercise("Biceps", "chin-up"),
            ObjSearchExercise("Biceps", "isometric chin-up"),

            ObjSearchExercise("Back", "regular dead-lift"),
            ObjSearchExercise("Back", "sumo dead-lift"),
            ObjSearchExercise("Back", "romanian dead-lift"),
            ObjSearchExercise("Back", "lat machine"),
            ObjSearchExercise("Back", "lat machine T-Bar"),
            ObjSearchExercise("Back", "dumbbells row"),
            ObjSearchExercise("Back", "cable row"),
            ObjSearchExercise("Back", "pull-ups (Back)"),
            ObjSearchExercise("Back", "barbell row"),
            ObjSearchExercise("Back", "bench row"),
            ObjSearchExercise("Back", "close grip barbell rowing machine"),
            ObjSearchExercise("Back", "push-down rope"),
            ObjSearchExercise("Back", "butterflies (Back)"),
            ObjSearchExercise("Back", "single dumbbell row"),
            ObjSearchExercise("Back", "inverted dumbbells rowing"),
            ObjSearchExercise("Back", "close grip barbell rowing machine"),
            ObjSearchExercise("Back", "renegade oarsman"),
            ObjSearchExercise("Back", "trx oarsman"),
            ObjSearchExercise("Back", "hyperextension"),
            ObjSearchExercise("Back", "t-bar oarsman"),

            ObjSearchExercise("Triceps", "close grip flat bench"),
            ObjSearchExercise("Triceps", "close grip incline bench"),
            ObjSearchExercise("Triceps", "french press barbell"),
            ObjSearchExercise("Triceps", "rope triceps"),
            ObjSearchExercise("Triceps", "french press cables"),
            ObjSearchExercise("Triceps", "cable triceps with rope"),
            ObjSearchExercise("Triceps", "trx triceps"),
            ObjSearchExercise("Triceps", "dips (Triceps)"),
            ObjSearchExercise("Triceps", "diamonds push-ups"),
            ObjSearchExercise("Triceps", "bench press triceps"),
            ObjSearchExercise("Triceps", "press dumbbells"),
            ObjSearchExercise("Triceps", "tiger push-ups (Triceps)"),
            ObjSearchExercise("Triceps", "french press dumbbells"),
            ObjSearchExercise("Triceps", "diamond raise push-ups (Triceps)"),

            ObjSearchExercise("Shoulders", "lateral raises"),
            ObjSearchExercise("Shoulders", "front raises"),
            ObjSearchExercise("Shoulders", "dumbbell thrusts"),
            ObjSearchExercise("Shoulders", "arnold"),
            ObjSearchExercise("Shoulders", "chin pull-up dumbbell"),
            ObjSearchExercise("Shoulders", "chin pull-up barbell"),
            ObjSearchExercise("Shoulders", "lateral cable raises"),
            ObjSearchExercise("Shoulders", "cables front raises"),
            ObjSearchExercise("Shoulders", "military press barbell"),
            ObjSearchExercise("Shoulders", "front disc raises"),
            ObjSearchExercise("Shoulders", "butterfly (Shoulders)"),
            ObjSearchExercise("Shoulders", "elastic front lifts"),
            ObjSearchExercise("Shoulders", "elastic side raises"),
            ObjSearchExercise("Shoulders", "machine shoulders press"),
            ObjSearchExercise("Shoulders", "military press dumbbell"),
            ObjSearchExercise("Shoulders", "isometric military press"),

            ObjSearchExercise("Calves", "calves on disc"),
            ObjSearchExercise("Calves", "calf machine"),
            ObjSearchExercise("Calves", "tiptoe walking"),
            ObjSearchExercise("Calves", "jump squat (Calves)"),
            ObjSearchExercise("Calves", "tiptoe up the barbell"),
            ObjSearchExercise("Calves", "raise toe of dumbbells"),

            ObjSearchExercise("Abdominals", "crunch"),
            ObjSearchExercise("Abdominals", "bicycle (Abs)"),
            ObjSearchExercise("Abdominals", "lateral abs"),
            ObjSearchExercise("Abdominals", "plank"),
            ObjSearchExercise("Abdominals", "side plank"),
            ObjSearchExercise("Abdominals", "jump plank"),
            ObjSearchExercise("Abdominals", "sit-up"),
            ObjSearchExercise("Abdominals", "super-man plank"),
            ObjSearchExercise("Abdominals", "plank with pelvis rotation"),
            ObjSearchExercise("Abdominals", "mountain-climber"),

            ObjSearchExercise("Cardio", "tapis roulant"),
            ObjSearchExercise("Cardio", "cyclette"),
            ObjSearchExercise("Cardio", "rope"),
            ObjSearchExercise("Cardio", "elliptical"),
            ObjSearchExercise("Cardio", "step-up (Cardio)"),
            ObjSearchExercise("Cardio", "running"),
            ObjSearchExercise("Cardio", "jogging"),
            ObjSearchExercise("Cardio", "spinning"),
            ObjSearchExercise("Cardio", "rowing machine"),
            ObjSearchExercise("Cardio", "march"),
            ObjSearchExercise("Cardio", "skip"),
            ObjSearchExercise("Cardio", "run kicked"),
            ObjSearchExercise("Cardio", "suicide"),
            ObjSearchExercise("Cardio", "burpees"),
            ObjSearchExercise("Cardio", "shadow boxe"),
            ObjSearchExercise("Cardio", "jumping jacks"),
            ObjSearchExercise("Cardio", "jab-direct"),
            ObjSearchExercise("Cardio", "hook-upper"),
            ObjSearchExercise("Cardio", "bag shooting")
        )

        db.collection(COLLECTIONEXERCISES).document("ENG")
            .update("ListExercises", listExercises)
            .addOnSuccessListener {
                Log.d(TAG, "Storing document exercises: success")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Storing document exercises: failed", e)
            }
    }

    //Downloading the exercise from firestore
    @Suppress("UNCHECKED_CAST")
    fun downloadAvailableExerciseEng(callback: (ArrayList<ObjSearchExercise>) -> Unit) {
        db.collection(COLLECTIONEXERCISES).document("ENG").get()
            .addOnSuccessListener { document ->
                Log.d(TAG, "Downloading document exercises: success")

                val doc =
                    document.data?.get("ListExercises") as ArrayList<HashMap<String, String>>

                val listExercise = ArrayList<ObjSearchExercise>()

                doc.forEach { exercise ->

                    listExercise.add(
                        ObjSearchExercise(
                            exercise["muscle"],
                            exercise["nameExercise"]
                        )
                    )
                    callback(listExercise)
                }

            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Downloading document exercises: failed", e)
            }
    }

    //Downloading the exercise from firestore
    @Suppress("UNCHECKED_CAST")
    fun downloadAvailableExerciseIta(callback: (ArrayList<ObjSearchExercise>) -> Unit) {
        db.collection(COLLECTIONEXERCISES).document("ITA").get()
            .addOnSuccessListener { document ->
                Log.d(TAG, "Downloading document exercises: success")

                val doc =
                    document.data?.get("ListaEsercizi") as ArrayList<HashMap<String, String>>

                val listExercise = ArrayList<ObjSearchExercise>()

                doc.forEach { exercise ->

                    listExercise.add(
                        ObjSearchExercise(
                            exercise["muscle"],
                            exercise["nameExercise"]
                        )
                    )
                    callback(listExercise)
                }
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Downloading document exercises: failed", e)
            }
    }
}