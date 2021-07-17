package it.app.myfitnessdiary.activities

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.google.android.material.snackbar.Snackbar
import it.app.myfitnessdiary.R
import it.app.myfitnessdiary.databinding.ActivityLoginBinding
import it.app.myfitnessdiary.firebase.FireAuth


/**
 * Class dedicated to the login
 */

class LoginActivity : AppCompatActivity() {

    private val TAG = "ACTIVITY_LOGIN"
    private lateinit var binding: ActivityLoginBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(LayoutInflater.from(this))

        binding.emailFieldLogin.doOnTextChanged { _, _, _, _ ->
            binding.layoutLoginEditTextEmail.error = null
        }

        binding.passwordFieldLogin.doOnTextChanged { _, _, _, _ ->
            binding.layoutLoginEditTextPassword.error = null
        }

        binding.btnLogin.setOnClickListener {
            val email = binding.emailFieldLogin.text.toString().trim()
            val password = binding.passwordFieldLogin.text.toString().trim()

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
                        binding.constraintActivityLogin,
                        "Authentication failed!",
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

        binding.btnCreateAccount.setOnClickListener {
            FireAuth.signOut()
            val intent = Intent(this, RegistraionActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onStart() {
        super.onStart()
        if(FireAuth.getCurrentUserAuth()!=null){
            startHomeAthlete()
        }
    }

    //Starting the athlete activity
    private fun startHomeAthlete() {
        val intent = Intent(this, HomeActivity::class.java)
        Toast.makeText(
            this, "Welcome back athlete!",
            Toast.LENGTH_SHORT
        ).show()
        startActivity(intent)
        finish()
    }

    //Visibility No FB
    private fun setVisibilityForLoginNoFB() {
        binding.progressBarLogin.visibility = View.VISIBLE
        binding.titleAppLogin.visibility = View.INVISIBLE
        binding.imageViewBackgroundLogin.visibility = View.INVISIBLE
        binding.layoutLoginEditTextEmail.visibility = View.INVISIBLE
        binding.layoutLoginEditTextPassword.visibility = View.INVISIBLE
        binding.btnLogin.visibility = View.INVISIBLE
        binding.btnCreateAccount.visibility = View.INVISIBLE

        //Resetting the field
        binding.layoutLoginEditTextEmail.error = null
        binding.layoutLoginEditTextPassword.error = null
    }

    //Resetting the field and the visibility
    private fun resetVisibilityForLoginNoFB() {
        binding.progressBarLogin.visibility = View.INVISIBLE
        binding.titleAppLogin.visibility = View.VISIBLE
        binding.imageViewBackgroundLogin.visibility = View.VISIBLE
        binding.layoutLoginEditTextEmail.visibility = View.VISIBLE
        binding.layoutLoginEditTextPassword.visibility = View.VISIBLE
        binding.btnLogin.visibility = View.VISIBLE
        binding.btnCreateAccount.visibility = View.VISIBLE

        binding.emailFieldLogin.setText("")
        binding.passwordFieldLogin.setText("")

        binding.layoutLoginEditTextEmail.error = getString(R.string.field_uncorrected)
        binding.layoutLoginEditTextEmail.errorIconDrawable = null
        binding.layoutLoginEditTextPassword.error = getString(R.string.field_uncorrected)
        binding.layoutLoginEditTextPassword.errorIconDrawable = null

        YoYo.with(Techniques.Shake)
            .playOn(binding.layoutLoginEditTextEmail)

        YoYo.with(Techniques.Shake)
            .playOn(binding.layoutLoginEditTextPassword)
    }

}