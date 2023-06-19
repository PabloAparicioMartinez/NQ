package com.example.nq.authSignIn

import android.content.Intent
import android.net.Uri
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
import com.example.nq.authFirebase.FirebaseUserData
import com.example.nq.authFirebase.SignInViewModel
import com.example.nq.authFirebase.UserData
import com.example.nq.dataStore.DataStoreManager
import com.example.nq.recyclerViewTickets.TicketsRepository
import com.example.nq.storageFirebase.FirebaseFriendsRepository
import com.example.nq.storageFirebase.FirebaseRepository
import com.google.android.gms.auth.api.identity.Identity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_sign_in.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

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

            val signedInUser = firebaseAuthManager.getSignedInUser()

            if (signedInUser != null) {

                // Cargar los datos desde Firebase al repositorio de Usuario
                val signedUserEmail = signedInUser.mail
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
                TicketsRepository.fetchTicketData(signedUserEmail)

                Toast.makeText(applicationContext, "¡Sesión iniciada!", Toast.LENGTH_SHORT).show()

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
        signIn_emailButton.setOnClickListener {
            goToSignInEmailActivity()
        }

        // INICIAR SESIÓN con GOOGLE
        signIn_googleButton.setOnClickListener {
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
        signIn_noSignInButton.setOnClickListener {
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

                    val userDataGoogle: UserData? = firebaseAuthManager.getSignedInUser()
                    val originalName = userDataGoogle?.name ?: "Nombre"
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

                    // Cargar los datos desde Firebase al repositorio de Usuario
                    val signedUserEmail = userDataGoogle?.mail
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
                    TicketsRepository.fetchTicketData(signedUserEmail)


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
        }
    }

    private fun setLayoutVisibility(visibilities: Int) {
        signIn_introLayout.visibility = visibilities
    }

    // Función para recibir la info del Usuario guardada en la base de datos de Firebase
    private suspend fun fetchUserData(email:String): FirebaseUserData {
        return try {
            val querySnapshot = Firebase.firestore
                .collection("UserData")
                .document(email)
                .collection("UserInfo")
                .document("Data")
                .get()
                .await()
            val userData = querySnapshot.toObject<FirebaseUserData>()
            userData ?: FirebaseUserData() // Use default values if userData is null
        } catch (e: Exception) {
            FirebaseUserData()
        }
    }

}