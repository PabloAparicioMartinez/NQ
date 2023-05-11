package com.example.nq

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_sign_in.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

const val REQUEST_CODE_SIGN_IN = 0

class SignInActivity : AppCompatActivity() {

    lateinit var auth: FirebaseAuth
    lateinit var googleSignInClient: GoogleSignInClient

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_sign_in)

            auth = FirebaseAuth.getInstance()
            val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_client_ID))
                .requestEmail()
                .build()
            googleSignInClient = GoogleSignIn.getClient(this, options)
            //auth.signOut()

            val actionBar = supportActionBar
            if (actionBar != null) {
                actionBar.title = ""
                actionBar.setDisplayHomeAsUpEnabled(true)
            }

            signIn_password_layout.isEndIconVisible = false
            signIn_passwordText.onFocusChangeListener =
                View.OnFocusChangeListener { view, hasFocus ->
                    signIn_password_layout.isEndIconVisible = hasFocus
                }

            signIn_signInButton.setOnClickListener() {
                normalSignIn()
            }

            signIn_signUpButton.setOnClickListener() {
                signIn_emailText.text?.clear()
                signIn_passwordText.text?.clear()

                Intent(this, SignUpActivity::class.java).also {
                    startActivity(it)
                }
            }

            signIn_googleButton.setOnClickListener() {
                googleSignIn()
            }
        }

    fun signOutUser() {
        auth = FirebaseAuth.getInstance()
        auth.signOut()
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

    private fun normalSignIn(){
        val email = signIn_emailText.text.toString()
        val password = signIn_passwordText.text.toString()

        if (email.isNotEmpty() && password.isNotEmpty()){
            CoroutineScope(Dispatchers.IO).launch {
                try{
                    auth.signInWithEmailAndPassword(email, password).await()
                    withContext(Dispatchers.Main){
                        Toast.makeText(this@SignInActivity, "¡Sesión iniciada correctamente!", Toast.LENGTH_SHORT).show()
                        Intent(this@SignInActivity, MainActivity::class.java).also {
                            startActivity(it)
                        }
                    }
                } catch (e: Exception){
                    withContext(Dispatchers.Main){
                        signIn_passwordText.text?.clear()
                        Toast.makeText(this@SignInActivity, "Error de autentificación", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        else if(email.isEmpty()){
            Toast.makeText(this@SignInActivity, "Email vacío, introdúcelo por favor", Toast.LENGTH_SHORT).show()
        }
        else{
            Toast.makeText(this@SignInActivity, "Contraseña vacía, introdúcela por favor", Toast.LENGTH_SHORT).show()
        }
    }


    private fun googleSignIn() {
        googleSignInClient.signInIntent.also {
            googleResultContract.launch(it)
        }
    }

    private val googleResultContract = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val account = GoogleSignIn.getSignedInAccountFromIntent(result.data).result
            account?.let {
                googleSignInResult(it)
            }
        }
    }

    private fun googleSignInResult(account: GoogleSignInAccount) {
        val credentials = GoogleAuthProvider.getCredential(account.idToken, null)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                auth.signInWithCredential(credentials).await()
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@SignInActivity, "¡Sesión iniciada correctamente!", Toast.LENGTH_SHORT).show()
                    Intent(this@SignInActivity, MainActivity::class.java).also {
                        startActivity(it)
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@SignInActivity, e.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

}