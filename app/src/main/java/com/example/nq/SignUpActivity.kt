package com.example.nq

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_sign_up.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.lang.Exception

class SignUpActivity : AppCompatActivity() {

    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        auth = FirebaseAuth.getInstance()

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.title = ""
        }

        signUp_passwordLayout.isEndIconVisible = false
        signUp_passwordText.onFocusChangeListener = View.OnFocusChangeListener { view, hasFocus ->
            signUp_passwordLayout.isEndIconVisible = hasFocus
        }

        signUp_passwordConfirm_layout.isEndIconVisible = false
        signUp_passwordConfirmLayout.onFocusChangeListener = View.OnFocusChangeListener { view, hasFocus ->
            signUp_passwordConfirm_layout.isEndIconVisible = hasFocus
        }

        signUp_signUpButton.setOnClickListener(){
            registerUser()
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

    private fun registerUser(){
        var email = signUp_emailText.text.toString()
        var password = signUp_passwordText.text.toString()
        var passwordConfirm = signUp_passwordConfirmLayout.text.toString()

        if (email.isNotEmpty() && password.isNotEmpty() && passwordConfirm.isNotEmpty()){
            if(password == passwordConfirm){
                CoroutineScope(Dispatchers.IO).launch {
                    try{
                        auth.createUserWithEmailAndPassword(email, password).await()
                        withContext(Dispatchers.Main){
                            Toast.makeText(this@SignUpActivity, "¡Cuenta creada!", Toast.LENGTH_SHORT).show()
                            Intent(this@SignUpActivity, MainActivity::class.java).also {
                                startActivity(it)
                            }
                            finish()
                        }
                    } catch (e: Exception){
                        withContext(Dispatchers.Main){
                            Toast.makeText(this@SignUpActivity, e.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } else {
                Toast.makeText(this@SignUpActivity, "Las constraseñas no coinciden", Toast.LENGTH_SHORT).show()
            }
        }
        else if(email.isEmpty()){
            Toast.makeText(this@SignUpActivity, "Introduce tu email por favor", Toast.LENGTH_SHORT).show()
        }
        else if(password.isEmpty()){
            Toast.makeText(this@SignUpActivity, "Introduce tu contraseña por favor", Toast.LENGTH_SHORT).show()
        }
        else{
            Toast.makeText(this@SignUpActivity, "Confirma la contraseña por favor", Toast.LENGTH_SHORT).show()
        }
    }
}