package it.app.myfitnessdiary.activities

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
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
        setContentView(binding.root)

        binding.TFEmail.doOnTextChanged { _, _, _, _ ->
            binding.LTETEmail.error = null
        }

        binding.TFPassword.doOnTextChanged { _, _, _, _ ->
            binding.LTETPassword.error = null
        }

        binding.BTNLogin.setOnClickListener {

            hideKeyboard()

            val email = binding.TFEmail.text.toString().trim()
            val password = binding.TFPassword.text.toString().trim()

            //Setting the visibility for login
            setVisibilityForLogin()

            //Call to the method in FireAuth to check if the user exist in the FireAuthentication
            FireAuth.login(email, password) { result ->

                //if the auth is ok, the user is redirect on his home page
                if (result) {

                    Toast.makeText(
                        this,
                        getString(R.string.welcome_back),
                        Toast.LENGTH_SHORT
                    ).show()

                    startHomeActivity()
                    //finish()

                } else {

                    //setting blank the field and restore visibility
                    resetVisibilityForLogin()

                    //Snackbar in case of failed auth
                    Handler(Looper.getMainLooper()).postDelayed({
                        Snackbar.make(
                            binding.CNActivity,
                            getString(R.string.auth_failed),
                            Snackbar.LENGTH_SHORT
                        )
                            .setBackgroundTint(ContextCompat.getColor(this, R.color.app_foreground))
                            .setTextColor(ContextCompat.getColor(this, R.color.white))
                            .show()
                    }, 250)
                }
            }
        }

        binding.BTNCreateAccount.setOnClickListener {
            FireAuth.signOut()

            startRegistrationActivity()
            //finish()
        }
    }

    override fun onStart() {
        super.onStart()
        if (FireAuth.getCurrentUser() != null) {
            startHomeActivity()
            finish()
        }
    }

    private fun startRegistrationActivity() {
        /*val intent = Intent(this, RegistrationActivity::class.java)
        startActivity(intent)*/
    }

    private fun startHomeActivity() {
        /*val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)*/
    }

    //Visibility No FB
    private fun setVisibilityForLogin() {
        binding.PBBackground.visibility = View.VISIBLE
        binding.TVTitleApp.visibility = View.INVISIBLE
        binding.IVBackground.visibility = View.INVISIBLE
        binding.LTETEmail.visibility = View.INVISIBLE
        binding.LTETPassword.visibility = View.INVISIBLE
        binding.BTNLogin.visibility = View.INVISIBLE
        binding.BTNCreateAccount.visibility = View.INVISIBLE

        //Resetting the field
        binding.LTETEmail.error = null
        binding.LTETPassword.error = null
    }

    //Resetting the field and the visibility
    private fun resetVisibilityForLogin() {
        binding.PBBackground.visibility = View.INVISIBLE
        binding.TVTitleApp.visibility = View.VISIBLE
        binding.IVBackground.visibility = View.VISIBLE
        binding.LTETEmail.visibility = View.VISIBLE
        binding.LTETPassword.visibility = View.VISIBLE
        binding.BTNLogin.visibility = View.VISIBLE
        binding.BTNCreateAccount.visibility = View.VISIBLE

        binding.TFEmail.setText("")
        binding.TFPassword.setText("")

        binding.LTETEmail.error = getString(R.string.field_uncorrected)
        binding.LTETEmail.errorIconDrawable = null
        binding.LTETPassword.error = getString(R.string.field_uncorrected)
        binding.LTETPassword.errorIconDrawable = null

        YoYo.with(Techniques.Shake)
            .playOn(binding.LTETEmail)

        YoYo.with(Techniques.Shake)
            .playOn(binding.LTETPassword)
    }

    private fun hideKeyboard() {
        val imm = this.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager

        //Find the currently focused view, so we can grab the correct window token from it.
        var view = this.currentFocus

        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(this)
        }

        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}