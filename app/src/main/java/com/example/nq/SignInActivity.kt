package com.example.nq

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.example.nq.firebaseAuth.FirebaseAuthManager
import com.example.nq.firebaseAuth.SignInViewModel
import com.google.android.gms.auth.api.identity.Identity
import kotlinx.android.synthetic.main.activity_sign_in.*
import kotlinx.android.synthetic.main.activity_sign_in_email.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SignInActivity() : AppCompatActivity() {

    private val firebaseAuthManager by lazy {
        FirebaseAuthManager(
            context = applicationContext,
            oneTapClient = Identity.getSignInClient(applicationContext)
        )
    }
    private val viewModel = SignInViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        installSplashScreen()
        Thread.sleep(1000)

        setContentView(R.layout.activity_sign_in)

        // Inicializar INICIAR SESIÓN: si el usuario ha iniciado sesión, se le mantiene conectado
        lifecycleScope.launch {

            if (firebaseAuthManager.getSignedInUser() != null) {
                Toast.makeText(applicationContext, "¡Sesión iniciada!", Toast.LENGTH_SHORT).show()
                goToMainActivity()
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
    }

    private val launcher = registerForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            lifecycleScope.launch {
                val signInResultGoogle = firebaseAuthManager.signInWithIntentGoogle(result.data ?: return@launch)
                viewModel.onSignInResult(signInResultGoogle)

                if (viewModel.state.value.isSignInSuccessful) {
                    Toast.makeText(applicationContext, "¡Sesión iniciada!", Toast.LENGTH_SHORT).show()
                    viewModel.resetState()
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
            finish()
        }
    }
}