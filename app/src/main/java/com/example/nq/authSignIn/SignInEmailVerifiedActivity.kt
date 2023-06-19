package com.example.nq.authSignIn

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.lifecycleScope
import com.example.nq.MainActivity
import com.example.nq.R
import com.example.nq.authFirebase.FirebaseAuthManager
import com.example.nq.authFirebase.SignInViewModel
import com.example.nq.dataStore.DataStoreManager
import com.example.nq.authFirebase.UserData
import com.example.nq.recyclerViewProfilePictures.ProfilePicturesRepository
import com.example.nq.recyclerViewTickets.TicketsRepository
import com.example.nq.storageFirebase.FirebaseFriendsRepository
import com.example.nq.storageFirebase.FirebaseRepository
import com.google.android.gms.auth.api.identity.Identity
import kotlinx.android.synthetic.main.activity_sign_in_email_verified.*
import kotlinx.coroutines.launch

class SignInEmailVerifiedActivity : AppCompatActivity() {

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
    var userExists = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in_email_verified)

        lifecycleScope.launch {

            setLayoutVisibilities(listOf(View.GONE, View.GONE, View.VISIBLE))

            val introducedEmail = dataStoreManager.getStringFromDataStore(
                stringPreferencesKey("introducedEmail"),
                ""
            )

            val intentData: Uri? = intent.data
            if (intentData != null) {

                val signInResultMail = firebaseAuthManager.signInWithIntentMail(intent, introducedEmail)
                viewModel.onSignInResult(signInResultMail)

                if (viewModel.state.value.isSignInSuccessful) {

                    val userData: UserData? = firebaseAuthManager.getSignedInUser()
                    val userFirstName = dataStoreManager.getStringFromDataStore(stringPreferencesKey("userFirstName"), "Nombre")
                    val userLastName = dataStoreManager.getStringFromDataStore(stringPreferencesKey("userLastName"), "Apellido(s)")

                    signInEmailVerified_firstNameText.setText(userFirstName)
                    signInEmailVerified_lastNameText.setText(userLastName)

                    if (userFirstName != "Nombre") userExists = true

                    setLayoutVisibilities(listOf(View.VISIBLE, View.GONE, View.GONE))
                    viewModel.resetState()
                } else {
                    Toast.makeText(this@SignInEmailVerifiedActivity, "Email no verificado...", Toast.LENGTH_SHORT).show()
                    setLayoutVisibilities(listOf(View.GONE, View.VISIBLE, View.GONE))
                }
            } else {
                Toast.makeText(this@SignInEmailVerifiedActivity, "Hubo un problema en la verificación...", Toast.LENGTH_SHORT).show()
                setLayoutVisibilities(listOf(View.GONE, View.VISIBLE, View.GONE))
            }
        }

        // VERIFICADO, button IR A MAIN ACTIVITY
        signInEmailVerified_mainActivityButton.setOnClickListener {

            val firstName = signInEmailVerified_firstNameText.text.toString()
            val lastName = signInEmailVerified_lastNameText.text.toString()

            if (firstName.isEmpty()) {
                signInEmailVerified_firstNameLayout.error = "Introduce tu nombre"
                return@setOnClickListener
            }
            if (!firstName.isOnlyLetters()) {
                signInEmailVerified_firstNameLayout.error = "Introduce un nombre válido"
                return@setOnClickListener
            }
            if (lastName.isEmpty()) {
                signInEmailVerified_lastNameLayout.error = "Introduce tu(s) apellido(s)"
                return@setOnClickListener
            }
            if (!lastName.isOnlyLetters()) {
                signInEmailVerified_lastNameLayout.error = "Introduce un(os) apellido(s) válido(s)"
                return@setOnClickListener
            }

            lifecycleScope.launch {
                val newUserName = "$firstName $lastName"

                if (firebaseAuthManager.updateUserName(newUserName)) {

                    val firstNameKey = stringPreferencesKey("userFirstName")
                    dataStoreManager.saveStringToDataStore(firstNameKey, firstName)

                    val lastNameKey = stringPreferencesKey("userLastName")
                    dataStoreManager.saveStringToDataStore(lastNameKey, lastName)

                    // Cargar los datos desde Firebase al repositorio de Usuario
                    /*val signedUserEmail = userDataGoogle?.mail
                    val userData = fetchUserData(signedUserEmail!!)
                    FirebaseRepository.userName = userData.name
                    FirebaseRepository.userSurnames = userData.surnames
                    FirebaseRepository.userImage = Uri.parse(userData.uri)
                    FirebaseRepository.userEmail = userData.email
                    val emailList:List<String> = userData.friendEmails
                    FirebaseRepository.userFriendEmails = emailList as MutableList<String>
                    // Cargar la lista con la info de los amigos al repositorio de Amigos
                    FirebaseFriendsRepository.fetchFriendsData(emailList)
                    // Cargar la lista de tickets al repositorio de Tickets
                    TicketsRepository.fetchTicketData(signedUserEmail)*/


                    goToMainActivity()
                } else {
                    Toast.makeText(this@SignInEmailVerifiedActivity, "No se pudo actualizar el nombre...", Toast.LENGTH_SHORT).show()
                }

                if (!userExists) {
                    val newUserImageURI = Uri.parse("android.resource://com.example.nq/${R.drawable.ic_icon_04}")
                    if (firebaseAuthManager.updateUserImage(newUserImageURI))
                    else Toast.makeText(this@SignInEmailVerifiedActivity, "No se pudo actualizar la imagen...", Toast.LENGTH_SHORT).show()
                }

                goToMainActivity()
            }
        }

        // NO VERIFICADO, button IR A SIGN IN
        signInEmailVerified_signInButton.setOnClickListener() {
            goToSignInActivity()
        }
    }

    private fun setLayoutVisibilities(listOfVisibilities: List<Int>) {
        signInEmailVerified_verifiedLayout.visibility = listOfVisibilities[0]
        signInEmailVerified_notVerifiedLayout.visibility = listOfVisibilities[1]
        signInEmailVerified_loadingLayout.visibility = listOfVisibilities[2]
    }

    private fun String.isOnlyLetters() : Boolean {
        return all { it.isLetter() || it.isWhitespace() }
    }

    private fun goToMainActivity() {
        Intent(this, MainActivity::class.java).also {
            startActivity(it)
            finish()
        }
    }

    private fun goToSignInActivity() {
        Intent(this, SignInActivity::class.java).also {
            startActivity(it)
            finish()
        }
    }
}