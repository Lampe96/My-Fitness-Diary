package it.app.myfitnessdiary

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import it.app.myfitnessdiary.firebase.FireAuth


/**
 * Class dedicated to the login, FB and not FB
 */

class ActivityLogin : AppCompatActivity() {

    private val TAG = "ACTIVITY_LOGIN"
    private val auth = Firebase.auth
    private lateinit var callbackManager: CallbackManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        emailFieldLogin.doOnTextChanged { _, _, _, _ ->
            layoutLoginEditTextEmail.error = null
        }

        passwordFieldLogin.doOnTextChanged { _, _, _, _ ->
            layoutLoginEditTextPassword.error = null
        }
    }

    //Starting the athlete activity
    private fun startHomeAthlete() {
        val intent = Intent(this, ActivityHomeAthlete::class.java)
        Toast.makeText(
            this, "Welcome back athlete!",
            Toast.LENGTH_SHORT
        ).show()
        startActivity(intent)
        finish()
    }

    //On click for the login button
    fun onClickLogin(@Suppress("UNUSED_PARAMETER") v: View) {

        val email = emailFieldLogin.text.toString().trim()
        val password = passwordFieldLogin.text.toString().trim()

        //Setting the visibility for login
        setVisibilityForLoginNoFB()

        //Call to the method in FireAuth to check if the user exist in the FireAuthentication
        FireAuth.login(email, password) { result, type ->
            //if the auth is ok, the user is redirect on his home page
            if (result) {

                finish()

            } else {
                //Snackbar in case of failed auth
                Snackbar.make(
                    constraintActivityLogin,
                    getString(R.string.authentication_failed),
                    Snackbar.LENGTH_LONG
                )
                    .setBackgroundTint(ContextCompat.getColor(this, R.color.app_foreground))
                    .setTextColor(ContextCompat.getColor(this, R.color.white))
                    .show()

                //setting blank the field and restore visibility
                resetVisibilityForLoginNoFB()
            }
        }
    }

    //Visibility No FB
    private fun setVisibilityForLoginNoFB() {
        progressBarLogin.visibility = View.VISIBLE
        titleAppLogin.visibility = View.INVISIBLE
        btnLoginFacebook.visibility = View.INVISIBLE
        imageViewBackgroundLogin.visibility = View.INVISIBLE
        layoutLoginEditTextEmail.visibility = View.INVISIBLE
        layoutLoginEditTextPassword.visibility = View.INVISIBLE
        btnLogin.visibility = View.INVISIBLE
        btnCreateAccount.visibility = View.INVISIBLE

        //Resetting the field
        layoutLoginEditTextEmail.error = null
        layoutLoginEditTextPassword.error = null
    }

    //Resetting the field and the visibility
    private fun resetVisibilityForLoginNoFB() {
        progressBarLogin.visibility = View.INVISIBLE
        titleAppLogin.visibility = View.VISIBLE
        btnLoginFacebook.visibility = View.VISIBLE
        imageViewBackgroundLogin.visibility = View.VISIBLE
        layoutLoginEditTextEmail.visibility = View.VISIBLE
        layoutLoginEditTextPassword.visibility = View.VISIBLE
        btnLogin.visibility = View.VISIBLE
        btnCreateAccount.visibility = View.VISIBLE

        emailFieldLogin.setText("")
        passwordFieldLogin.setText("")

        layoutLoginEditTextEmail.error = getString(R.string.fields_not_correct)
        layoutLoginEditTextEmail.errorIconDrawable = null
        layoutLoginEditTextPassword.error = getString(R.string.fields_not_correct)
        layoutLoginEditTextPassword.errorIconDrawable = null

        YoYo.with(Techniques.Shake)
            .playOn(layoutLoginEditTextEmail)

        YoYo.with(Techniques.Shake)
            .playOn(layoutLoginEditTextPassword)
    }

    //Starting the choice activity
    fun onClickRegistration(@Suppress("UNUSED_PARAMETER") v: View) {
        FireAuth.signOut()
        val intent = Intent(this, ActivityUserChoice::class.java)
        startActivity(intent)
        finish()
    }
}