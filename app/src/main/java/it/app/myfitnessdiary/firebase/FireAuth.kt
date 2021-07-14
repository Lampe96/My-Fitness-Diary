package it.app.myfitnessdiary.firebase

import android.app.Activity
import android.util.Log
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseUser

/**
 * In this class we're gonna implements
 * all the method that we are using to
 * execute the login
 */

class FireAuth {

    companion object {

        private val auth = Firebase.auth
        private const val TAG = "FIREAUTH"

        //Fun to check email and pass
        fun login(email: String, password: String, callback: (Boolean, Int) -> Unit) {
            if (email.isBlank() || password.isBlank()) {
                callback(false, -1)
            } else {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // Sign-in success
                            val currentUserId = auth.currentUser?.uid!!
                            Log.d(TAG, "SignInWithEmail: success")

                            //Looking for user type
                            val fireStore = FireStore()

                        } else {
                            // If sign in fails
                            Log.w(TAG, "SignInWithEmail: failure", task.exception)
                            callback(false, -1)
                        }
                    }
            }
        }

        //Creating the account on fireAuth
        fun createAccount(
            email: String,
            password: String,
            activity: Activity,
            callback: (Boolean, String) -> Unit,
        ) {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity) { task ->
                    if (task.isSuccessful) {
                        // Creation on fireAuth success
                        val currentUserId = auth.currentUser?.uid.toString()
                        Log.d(TAG, "CreateUserWithEmail: success")
                        callback(true, currentUserId)
                    } else {
                        // If Creation on fireAuth fails
                        Log.w(TAG, "CreateUserWithEmail: failure", task.exception)
                        callback(false, "")
                    }
                }
        }

        //Used to enter in the app if user is already logged-in
        fun getCurrentUserAuth(): FirebaseUser? {
            return auth.currentUser
        }

        //Deleting user from fireAuth
        fun deleteCurrentUser() {
            auth.currentUser?.delete()?.addOnSuccessListener {
                Log.d(TAG, "Delete account: success")
            }?.addOnFailureListener { e ->
                Log.w(TAG, "Delete account: failure", e)
            }
        }

        fun signOut() {
            auth.signOut()
        }

        //Used for reauth of user, before deleting the account.
        //We used that to avoid the eventual fail delete from fireAuth
        fun userReauthenticate(password: String, callback: (Boolean) -> Unit) {
            if (password != "") {
                val user = auth.currentUser!!
                val credential = EmailAuthProvider.getCredential(user.email!!, password)

                user.reauthenticate(credential)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d(TAG, "User re-authenticated: success")
                            callback(true)
                        } else {
                            Log.w(TAG, "User re-authenticated: failure", task.exception)
                            callback(false)
                        }
                    }
            } else {
                callback(false)
            }
        }
    }
}