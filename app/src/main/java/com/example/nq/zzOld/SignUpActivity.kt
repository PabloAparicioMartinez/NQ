package com.example.nq.zzOld

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.nq.MainActivity
import com.example.nq.R
import com.example.nq.firebase.FirebaseRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.android.synthetic.main.activity_sign_up.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

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

        signUp_passwordConfirmLayout.isEndIconVisible = false
        signUp_passwordConfirmText.onFocusChangeListener = View.OnFocusChangeListener { view, hasFocus ->
            signUp_passwordConfirmLayout.isEndIconVisible = hasFocus
        }

        //Buttons
        signUpLayout.setOnClickListener() {
            val inputMethodManager = this.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(signUpLayout.windowToken, 0)
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
        val email = signUp_emailText.text.toString()
        val password = signUp_passwordText.text.toString()
        val passwordConfirm = signUp_passwordConfirmText.text.toString()
        val name = signUp_nameText.text.toString()

        if (email.isNotEmpty() && password.isNotEmpty() && passwordConfirm.isNotEmpty() && name.isNotEmpty()){
            if(password == passwordConfirm){
                CoroutineScope(Dispatchers.IO).launch {
                    try{
                        auth.createUserWithEmailAndPassword(email, password).await()
                        withContext(Dispatchers.Main){
                            createProfile(name, email)
                            Intent(this@SignUpActivity, MainActivity::class.java).also {
                                startActivity(it)
                            }
                            finish()
                        }
                    } catch (error: Exception){
                        withContext(Dispatchers.Main){

                            val errorCode = (error as FirebaseAuthException).errorCode
                            val errorMessage = getString(FirebaseRepository.authErrors[errorCode] ?: R.string.error_login_default_error)

                            var errorCodeIndex = -1
                            for ((index, entry) in FirebaseRepository.authErrors.entries.withIndex()) {
                                if (entry.key == errorCode) {
                                    errorCodeIndex = index
                                    break
                                }
                            }

                            when (errorCodeIndex) {
                                1 -> {
                                    signUp_emailLayout.error = errorMessage
                                    signUp_emailText.text?.clear()
                                }
                                else -> {
                                    signUp_emailLayout.error = errorMessage
                                    Toast.makeText(this@SignUpActivity, "$errorMessage", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                }
            } else {
                signUp_passwordConfirmLayout.error = "Las contraseñas no coinciden"
            }
        }
        else if(email.isEmpty()){
            signUp_emailLayout.error = "Introduce una dirección de correo electrónico"
        }
        else if(password.isEmpty()){
            signUp_passwordLayout.error = "Introduce la contraseña"
        }
        else if(passwordConfirm.isEmpty()){
            signUp_passwordConfirmLayout.error = "Confirma la contraseña"
        }
        else{
            signUp_nameLayout.error = "Introduce tu nombre"
        }
    }

    fun createProfile(name: String, email: String) {

        auth.currentUser?.let { user ->
            val radioButtonID = signUp_radioGroup.checkedRadioButtonId
            val selectedButton = findViewById<RadioButton>(radioButtonID).text as String
            var photoURI = Uri.parse("android.resource://$packageName/${R.drawable.png_boy_01}")

            when (selectedButton) {
                "Hombre" -> photoURI = Uri.parse("android.resource://$packageName/${R.drawable.png_boy_01}")
                "Mujer" -> photoURI = Uri.parse("android.resource://$packageName/${R.drawable.png_girl_01}")
            }

            val createProfile = UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .setPhotoUri(photoURI)
                .build()

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    user.updateProfile(createProfile).await()
                    withContext(Dispatchers.Main) {
//                        FirebaseRepository.userName = name
//                        FirebaseRepository.userImage = photoURI
//                        FirebaseRepository.userGmail = email
                        Toast.makeText(this@SignUpActivity, "¡Cuenta creada!", Toast.LENGTH_SHORT).show()
                    }
                } catch (error: Exception) {
                    withContext(Dispatchers.Main){
                        Toast.makeText(this@SignUpActivity, "$error", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}