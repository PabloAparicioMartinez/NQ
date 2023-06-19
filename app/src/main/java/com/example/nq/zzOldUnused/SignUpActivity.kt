package com.example.nq.zzOld

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.nq.MainActivity
import com.example.nq.R
import com.example.nq.authFirebase.FirebaseAuthManager
import com.example.nq.authFirebase.SignInViewModel
import com.google.android.gms.auth.api.identity.Identity
import kotlinx.android.synthetic.main.zz_old_activity_sign_up.*
import kotlinx.coroutines.launch

class SignUpActivity : AppCompatActivity() {

    private val firebaseAuthManager by lazy {
        FirebaseAuthManager(
            context = applicationContext,
            oneTapClient = Identity.getSignInClient(applicationContext)
        )
    }
    private val viewModel = SignInViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.zz_old_activity_sign_up)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.title = ""
        }

        signUp_signInButton.setOnClickListener() {
            signInUser()
        }

        signUp_signUpButton.setOnClickListener(){
            signUpUser()
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

    private fun signUpUser() {
        val email = signUp_emailText.text.toString()
        val password = signUp_passwordText.text.toString()

        if (email.isNotEmpty() && password.isNotEmpty()) {
            lifecycleScope.launch {
                if (firebaseAuthManager.signUpWithEmailAndPassword(email, password)) {
                    Toast.makeText(applicationContext, "Cuenta creada", Toast.LENGTH_SHORT).show()
                    goToMainActivity()
                } else {
                    Toast.makeText(applicationContext, "No se ha podido crear la cuenta", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun signInUser() {
        val email = signIn_emailText.text.toString()
        val password = signIn_passwordText.text.toString()

        if (email.isNotEmpty() && password.isNotEmpty()) {
            lifecycleScope.launch {
                if (firebaseAuthManager.signInWithEmailAndPassword(email, password)) {
                    Toast.makeText(applicationContext, "Sesión iniciada", Toast.LENGTH_SHORT).show()
                    goToMainActivity()
                } else {
                    Toast.makeText(applicationContext, "No se ha podido iniciar sesión", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun goToMainActivity() {
        Intent(this, MainActivity::class.java).also {
            startActivity(it)
            finish()
        }
    }
}