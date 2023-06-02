package com.example.nq.firebaseAuth

import android.content.Context
import android.content.Intent
import android.content.IntentSender
import com.example.nq.R
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_sign_up.*
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.util.concurrent.CancellationException

class FirebaseAuthManager(private val context: Context, private val oneTapClient: SignInClient) {

    private val auth = Firebase.auth

    // GOOGLE: devuelve un IntentSender que contiene información del usuario que va a Iniciar Sesión
    suspend fun signIn(): IntentSender? {

        val result = try {
            oneTapClient.beginSignIn(
                buildSignInRequest()
            ).await()
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) throw e
            null
        }
        return result?.pendingIntent?.intentSender
    }

    // GOOGLE: prepara la Request que se mandará para intentar Iniciar Sesión
    private fun buildSignInRequest() : BeginSignInRequest {

        return BeginSignInRequest.Builder().setGoogleIdTokenRequestOptions(
            BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                .setSupported(true)
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(context.getString(R.string.web_client_ID))
                .build()
        )
            .setAutoSelectEnabled(false)
            .build()
    }

    // GOOGLE: Intenta iniciar sesión y devuelve el resultado de este intento con SignInResult
    suspend fun signInWithIntentGoogle(intent: Intent) : SignInResult {

        // Son funciones de GOOGLE, se encarga de recibir los Credentials del user según el Intent
        val credential = oneTapClient.getSignInCredentialFromIntent(intent)
        val googleIDToken = credential.googleIdToken
        val googleCredentials = GoogleAuthProvider.getCredential(googleIDToken, null)

        return try {
            val user = auth.signInWithCredential(googleCredentials).await().user
            SignInResult(
                data = user?.run {
                    UserData(userID = uid, name = displayName, mail = email, profilePictureURL = photoUrl?.toString()) },
                errorMessage = null
            )
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) throw e
            SignInResult(data = null, errorMessage = e.message)
        }
    }

    // MAIL: envía un correo de verificación al email
    suspend fun sendSignInLink(email: String, actionCodeSettings: ActionCodeSettings) : Boolean {

        return try {
            auth.sendSignInLinkToEmail(email, actionCodeSettings).await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) throw e
            false
        }
    }

    // MAIL: verifica el correo electrónico y inicia sesión si se verifica
    suspend fun signInWithIntentMail(intent: Intent, introducedEmail: String) : SignInResult {

        val emailLink = intent.data.toString()

        //
        if (!auth.isSignInWithEmailLink(emailLink)) {
            return SignInResult(data = null, errorMessage = null)
        }

        return try {
            val user = auth.signInWithEmailLink(introducedEmail, emailLink).await().user
            SignInResult(
                data = user?.run {
                    UserData(userID = uid, name = displayName, mail = email, profilePictureURL = photoUrl?.toString()) },
                errorMessage = null
            )
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) throw e
            SignInResult(data = null, errorMessage = e.message)
        }
    }

    // RECUPERAR INFORMACIÓN del usuario
    fun getSignedInUser() : UserData? = auth.currentUser?.run {
        UserData(userID = uid, name = displayName, mail = email, profilePictureURL = photoUrl?.toString())
    }

    // UPDATEA el NOMBRE del USUARIO
    suspend fun updateUser(newUserName: String) : Boolean {

        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(newUserName)
            .build()

        return try {
            auth.currentUser?.updateProfile(profileUpdates)?.await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) throw e
            false
        }
    }

    // CERRAR SESIÓN
    suspend fun signOut() {

        try {
            oneTapClient.signOut().await()
            auth.signOut()
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) throw e
        }
    }
}