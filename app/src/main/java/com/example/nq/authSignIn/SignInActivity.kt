package com.example.nq.authSignIn

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.lifecycleScope
import com.example.nq.MainActivity
import com.example.nq.R
import com.example.nq.authFirebase.FirebaseAuthManager
import com.example.nq.authFirebase.SignInViewModel
import com.example.nq.authFirebase.UserData
import com.example.nq.dataStore.DataStoreManager
import com.example.nq.zzOld.SignUpActivity
import com.google.android.gms.auth.api.identity.Identity
import kotlinx.android.synthetic.main.activity_sign_in.*
import kotlinx.coroutines.launch

class SignInActivity() : AppCompatActivity() {

    private val firebaseAuthManager by lazy {
        FirebaseAuthManager(
            context = applicationContext,
            oneTapClient = Identity.getSignInClient(applicationContext)
        )
    }
    private val viewModel = SignInViewModel()
    private val dataStoreManager by lazy {
        DataStoreManager(context = applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        installSplashScreen()
        //Thread.sleep(1000)

        setContentView(R.layout.activity_sign_in)

        // Inicializar INICIAR SESIÓN: si el usuario ha iniciado sesión, se le mantiene conectado
        lifecycleScope.launch {

            if (firebaseAuthManager.getSignedInUser() != null) {
                Toast.makeText(applicationContext, "¡Sesión iniciada!", Toast.LENGTH_SHORT).show()

                //

                goToMainActivity()
            } else {
                val checkIfSignInClicked = intent?.getBooleanExtra("EXTRA_SignInClicked", false)
                if (checkIfSignInClicked == true) {
                    setLayoutVisibility(View.GONE)
                } else {
                    setLayoutVisibility(View.VISIBLE)
                }
            }
        }

        // INCIAR SESIÓN con MAIL
        signIn_emailButton.setOnClickListener() {
            goToSignInEmailActivity()
        }

        // INICIAR SESIÓN con GOOGLE
        signIn_googleButton.setOnClickListener() {
            lifecycleScope.launch {

                val signInIntentSender = firebaseAuthManager.signIn()
                val intentSenderRequest = signInIntentSender?.let {
                    IntentSenderRequest.Builder(it).build()
                }

                if (intentSenderRequest != null) {
                    launcher.launch(intentSenderRequest)
                } else {
                    Toast.makeText(applicationContext, "No se pudo iniciar sesión con Google...", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // ENTRAR SIN INICIAR SESIÓN
        signIn_noSignInButton.setOnClickListener() {
            goToMainActivity()
        }

        signIn_nijaButton.setOnClickListener() {
            Intent(this, SignUpActivity::class.java).also {
                startActivity(it)
            }
        }
    }

    private val launcher = registerForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            lifecycleScope.launch {
                val signInResultGoogle = firebaseAuthManager.signInWithIntentGoogle(result.data ?: return@launch)
                viewModel.onSignInResult(signInResultGoogle)

                if (viewModel.state.value.isSignInSuccessful) {

                    val userData: UserData? = firebaseAuthManager.getSignedInUser()
                    val originalName = userData?.name ?: "Nombre"
                    val words = originalName.split(" ")

                    val firstWord = words[0]
                    val restOfText = originalName.substring(firstWord.length).trim()

                    val firstName = firstWord
                    var lastName = "Apellido(s)"
                    if (restOfText != "") lastName = restOfText

                    val firstNameKey = stringPreferencesKey("userFirstName")
                    dataStoreManager.saveStringToDataStore(firstNameKey, firstName)

                    val lastNameKey = stringPreferencesKey("userLastName")
                    dataStoreManager.saveStringToDataStore(lastNameKey, lastName)

                    Toast.makeText(applicationContext, "¡Sesión iniciada!", Toast.LENGTH_SHORT).show()
                    viewModel.resetState()

                    //

                    goToMainActivity()
                }
            }
        }
        else {
            Toast.makeText(applicationContext, "No se pudo iniciar sesión...", Toast.LENGTH_SHORT).show()
        }
    }

    private fun goToMainActivity() {
        Intent(this, MainActivity::class.java).also {
            startActivity(it)
            finish()
        }
    }

    private fun goToSignInEmailActivity() {
        Intent(this, SignInEmailActivity::class.java).also {
            startActivity(it)
        }
    }

    private fun setLayoutVisibility(visibilities: Int) {
        signIn_introLayout.visibility = visibilities
    }
}