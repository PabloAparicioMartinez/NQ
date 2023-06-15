package com.example.nq.authSignIn

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.lifecycleScope
import com.example.nq.R
import com.example.nq.dataStore.DataStoreManager
import com.example.nq.authFirebase.FirebaseAuthManager
import com.google.android.gms.auth.api.identity.Identity
import com.google.firebase.auth.ktx.actionCodeSettings
import kotlinx.android.synthetic.main.activity_sign_in_email.*
import kotlinx.coroutines.launch

class SignInEmailActivity : AppCompatActivity() {

    private val firebaseAuthManager by lazy {
        FirebaseAuthManager(
            context = applicationContext,
            oneTapClient = Identity.getSignInClient(applicationContext)
        )
    }
    private val dataStoreManager by lazy {
        DataStoreManager(context = applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in_email)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.title = ""
            actionBar.setDisplayHomeAsUpEnabled(true)
        }

        setLayoutVisibilities(listOf(View.VISIBLE, View.GONE))

        // INCIAR SESIÓN con MAIL
        signInEmail_button.setOnClickListener() {

            val theEmail = signInEmail_emailText.text.toString()

            if (theEmail.isEmpty()) {
                signInEmail_emailTextLayout.error = "Introduce una dirección de correo electrónico"
                return@setOnClickListener
            }
            if (!theEmail.contains("@")) {
                signInEmail_emailTextLayout.error = "Introduce una dirección de correo electrónico válida"
                return@setOnClickListener
            }

            lifecycleScope.launch {

                setLayoutVisibilities(listOf(View.VISIBLE, View.VISIBLE))

                val email = signInEmail_emailText.text.toString()
                val actionCodeSettings = actionCodeSettings {
                    url = "https://nqnoqueues.page.link/example"
                    handleCodeInApp = true
                    setAndroidPackageName(
                        "com.example.nq",
                        true,
                        "29",
                    )
                }

                if (firebaseAuthManager.sendSignInLink(email, actionCodeSettings)){

                    val introducedEmailKey = stringPreferencesKey("introducedEmail")
                    dataStoreManager.saveStringToDataStore(introducedEmailKey, email)

                    goToSignInEmailSentActivity()

                } else {
                    Toast.makeText(applicationContext, "No se pudo enviar el email de verificación...", Toast.LENGTH_SHORT).show()
                    setLayoutVisibilities(listOf(View.VISIBLE, View.GONE))
                }
            }
        }

        // ESCONDER TECLADO
        signInEmailLayout.setOnClickListener() {
            val inputMethodManager = this.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(signInEmailLayout.windowToken, 0)
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

    private fun setLayoutVisibilities(listOfVisibilities: List<Int>) {
        signInEmail_emailLayout.visibility = listOfVisibilities[0]
        signInEmail_loadingLayout.visibility = listOfVisibilities[1]
    }

    private fun goToSignInEmailSentActivity() {
        Intent(this, SignInEmailSentActivity::class.java).also {
            startActivity(it)
            finish()
        }
    }
}