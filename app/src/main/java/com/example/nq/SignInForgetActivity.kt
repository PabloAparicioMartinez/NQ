package com.example.nq

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_sign_in_forget.*

class SignInForgetActivity : AppCompatActivity() {

    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in_forget)

        auth = FirebaseAuth.getInstance()

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.title = ""
            actionBar.setDisplayHomeAsUpEnabled(true)
        }

        //Buttons
        signInForgetLayout.setOnClickListener() {
            val inputMethodManager = this.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(signInForgetLayout.windowToken, 0)
        }

        signInForget_button.setOnClickListener() {
            sendEmail()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun sendEmail() {
        val email = signInForget_emailText.text.toString()

        if (email.isNotEmpty()){
            FirebaseAuth.getInstance().fetchSignInMethodsForEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val signInMethods = task.result?.signInMethods
                        if (signInMethods.isNullOrEmpty()) {
                            signInForget_emailLayout.error = "El correo electrónico introducido no existe"
                        } else {
                            Toast.makeText(this@SignInForgetActivity, "¡Correo de verificación enviado!", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                    } else {
                        val exception = task.exception
                        signInForget_emailLayout.error = "Ha ocurrido un error con el correo introducido"
                        Toast.makeText(this@SignInForgetActivity, "$exception", Toast.LENGTH_SHORT).show()
                    }
                }
        }
        else {
            signInForget_emailLayout.error = "Introduce una dirección de correo electrónico"
        }
    }
}