package com.example.nq

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.nq.firebase.FirebaseManager
import com.example.nq.firebase.FirebaseRepository
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
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

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.title = ""
            actionBar.setDisplayHomeAsUpEnabled(true)
        }

        signIn_passwordLayout.isEndIconVisible = false
        signIn_passwordText.onFocusChangeListener = View.OnFocusChangeListener { view, hasFocus ->
            signIn_passwordLayout.isEndIconVisible = hasFocus
        }

        //BUTTONS
        signInLayout.setOnClickListener() {
            val inputMethodManager = this.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(signInLayout.windowToken, 0)
        }

        signIn_signInButton.setOnClickListener() {
            normalSignIn()
        }

        signIn_forgotPasswordButton.setOnClickListener() {
            Intent(this, SignInForgetActivity::class.java).also {
                startActivity(it)
            }

            signIn_emailText.text?.clear()
            signIn_passwordText.text?.clear()
        }

        signIn_signUpButton.setOnClickListener() {
            Intent(this, SignUpActivity::class.java).also {
                startActivity(it)
            }

            signIn_emailText.text?.clear()
            signIn_passwordText.text?.clear()
        }

        signIn_googleButton.setOnClickListener() {
            googleSignIn()
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

    private fun normalSignIn(){
        val email = signIn_emailText.text.toString()
        val password = signIn_passwordText.text.toString()

        if (email.isNotEmpty() && password.isNotEmpty()){
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    auth.signInWithEmailAndPassword(email, password).await()
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@SignInActivity, "¡Sesión iniciada correctamente!", Toast.LENGTH_SHORT).show()
                        Intent(this@SignInActivity, MainActivity::class.java).also {
                            startActivity(it)
                        }
                    }
                } catch (error: Exception) {
                    println("ERRORAAA: $error")
                    withContext(Dispatchers.Main) {

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
                            3, 4 -> {
                                signIn_passwordLayout.error = errorMessage
                                signIn_passwordText.text?.clear()
                            }
                            else -> {
                                signIn_emailLayout.error = errorMessage
                                signIn_emailText.text?.clear()
                            }
                        }
                    }
                }
            }
        }
        else if(email.isEmpty()){
            signIn_emailLayout.error = "Introduce una dirección de correo electrónico"
        }
        else{
            signIn_passwordLayout.error = "Introduce la contraseña"
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
            } catch (error: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@SignInActivity, error.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}