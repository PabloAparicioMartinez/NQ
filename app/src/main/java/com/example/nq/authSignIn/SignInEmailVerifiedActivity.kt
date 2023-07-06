package com.example.nq.authSignIn

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.lifecycleScope
import com.example.nq.MainActivity
import com.example.nq.R
import com.example.nq.authFirebase.FirebaseAuthManager
import com.example.nq.storageFirebase.FirebaseUserData
import com.example.nq.authFirebase.SignInViewModel
import com.example.nq.authFirebase.UserData
import com.example.nq.dataStore.DataStoreManager
import com.example.nq.recyclerViewTickets.TicketsRepository
import com.example.nq.storageFirebase.FirebaseFriendsRepository
import com.example.nq.storageFirebase.FirebaseRepository
import com.google.android.gms.auth.api.identity.Identity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.activity_sign_in_email_verified.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in_email_verified)

        lifecycleScope.launch {

            setLayoutVisibilities(listOf(View.GONE, View.GONE, View.GONE, View.VISIBLE))

            val introducedEmail = dataStoreManager.getStringFromDataStore(
                stringPreferencesKey("introducedEmail"),
                ""
            )

            val intentData: Uri? = intent.data
            if (intentData != null) {

                val signInResultMail = firebaseAuthManager.signInWithIntentMail(intent, introducedEmail)
                viewModel.onSignInResult(signInResultMail)

                if (viewModel.state.value.isSignInSuccessful) {

                    val userIsRegistered = checkIfUserExistsOnDataStore(introducedEmail)

                    if (userIsRegistered) {
                        // EL usuario ya estaba registrado
                        setLayoutVisibilities(listOf(View.GONE, View.VISIBLE, View.GONE, View.GONE))
                    } else {
                        // Es la primera vez que se registra
                        setLayoutVisibilities(listOf(View.VISIBLE, View.GONE, View.GONE, View.GONE))
                    }

                    viewModel.resetState()

                } else {
                    Toast.makeText(this@SignInEmailVerifiedActivity, "Email no verificado...", Toast.LENGTH_SHORT).show()
                    setLayoutVisibilities(listOf(View.GONE, View.GONE, View.VISIBLE, View.GONE))
                }

            } else {
                Toast.makeText(this@SignInEmailVerifiedActivity, "Hubo un problema en la verificación...", Toast.LENGTH_SHORT).show()
                setLayoutVisibilities(listOf(View.GONE, View.GONE, View.VISIBLE, View.GONE))
            }
        }

        // VERIFICADO POR PRIMERA VEZ, button IR A MAIN ACTIVITY
        signInEmailVerified_mainActivityButton.setOnClickListener {

            val firstName = signInEmailVerified_firstNameText.text.toString()
            val surNames = signInEmailVerified_lastNameText.text.toString()

            if (firstName.isEmpty()) {
                signInEmailVerified_firstNameLayout.error = "Introduce tu nombre"
                return@setOnClickListener
            }
            if (!firstName.isOnlyLetters()) {
                signInEmailVerified_firstNameLayout.error = "Introduce un nombre válido"
                return@setOnClickListener
            }
            if (surNames.isEmpty()) {
                signInEmailVerified_lastNameLayout.error = "Introduce tu(s) apellido(s)"
                return@setOnClickListener
            }
            if (!surNames.isOnlyLetters()) {
                signInEmailVerified_lastNameLayout.error = "Introduce un(os) apellido(s) válido(s)"
                return@setOnClickListener
            }

            lifecycleScope.launch {

                val introducedEmail = dataStoreManager.getStringFromDataStore(
                    stringPreferencesKey("introducedEmail"),
                    ""
                )

                val newUserName = "$firstName $surNames"

                val userDataGoogle: UserData? = firebaseAuthManager.getSignedInUser()
                val userID = userDataGoogle?.userID

                if (firebaseAuthManager.updateUserName(newUserName)) {

                    // Cargar los datos en Firebase
                    lifecycleScope.launch {
                        val newUserImageResId = resources.getIdentifier("png_nq", "drawable", packageName)
                        val newImageDrawable = ResourcesCompat.getDrawable(resources, newUserImageResId, null)
                        val deviceToken = getDeviceToken()
                        val userData = FirebaseUserData(firstName, surNames, userID!!, introducedEmail, emptyList() , deviceToken)
                        saveUserData(userData)
                        saveUserImage(newImageDrawable!!, userID)
                        FirebaseRepository.userName = firstName
                        FirebaseRepository.userSurnames = surNames
                        FirebaseRepository.userID = userID
                        FirebaseRepository.userEmail = introducedEmail
                        FirebaseRepository.userDeviceToken = deviceToken
                    }

                } else {
                    Toast.makeText(this@SignInEmailVerifiedActivity, "No se pudo actualizar el nombre...", Toast.LENGTH_SHORT).show()
                }

                goToMainActivity()
            }
        }

        // VERIFICADO Y EXISTÍA EN LA BASE DE DATOS, button IR A MAIN ACTIVITY
        signInEmailAlreadyVerified_mainActivityButton.setOnClickListener {

            lifecycleScope.launch {

                val introducedEmail = dataStoreManager.getStringFromDataStore(
                    stringPreferencesKey("introducedEmail"),
                    ""
                )

                // Cargar los datos desde Firebase al repositorio de Usuario
                val userData = fetchUserData(introducedEmail)
                fetchUserImage(userData.ID)
                // Cargar y actualizar siempre el token del dispositivo
                val userDeviceToken = getDeviceToken()
                if (userDeviceToken == userData.deviceToken) {
                    FirebaseRepository.userDeviceToken = userData.deviceToken
                } else {
                    FirebaseRepository.userDeviceToken = userDeviceToken
                    updateDeviceToken(userDeviceToken, userData.email)
                }
                FirebaseRepository.userName = userData.name
                FirebaseRepository.userSurnames = userData.surnames
                FirebaseRepository.userID = userData.ID
                FirebaseRepository.userEmail = userData.email
                val emailList:List<String> = userData.friendEmails
                FirebaseRepository.userFriendEmails = emailList as MutableList<String>
                // Cargar la lista con la info de los amigos al repositorio de Amigos
                FirebaseFriendsRepository.fetchFriendsData(emailList)
                // Cargar la lista de tickets al repositorio de Tickets
                TicketsRepository.fetchTicketData(introducedEmail)
                // Actualizar la lista de imágenes de amigos
                fetchFriendsImages(FirebaseFriendsRepository.userFriends)

                Toast.makeText(applicationContext, "¡Sesión iniciada!", Toast.LENGTH_SHORT).show()

                goToMainActivity()
            }
        }

        // NO VERIFICADO, button IR A SIGN IN
        signInEmailVerified_signInButton.setOnClickListener {
            goToSignInActivity()
        }
    }

    private fun setLayoutVisibilities(listOfVisibilities: List<Int>) {
        signInEmailVerified_verifiedFirstTimeLayout.visibility = listOfVisibilities[0]
        signInEmailVerified_alreadyVerifiedLayout.visibility = listOfVisibilities[1]
        signInEmailVerified_notVerifiedLayout.visibility = listOfVisibilities[2]
        signInEmailVerified_loadingLayout.visibility = listOfVisibilities[3]
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

    // Función para comprbar si el Usuario está guardado en la base de datos de Firebase
    private suspend fun checkIfUserExistsOnDataStore(userEmail: String?): Boolean {
        return try {
            val documentSnapshot = Firebase.firestore
                .collection("UserData")
                .document(userEmail!!)
                .collection("UserInfo")
                .document("Data")
                .get()
                .await()

            documentSnapshot.exists()
        } catch (e: Exception) {
            false
        }
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

    // Función para cargar la info del Usuario en Firebase
    // El path para cargar la info del usuario será: UserData/UserInfo/Datos del usuario
    private fun saveUserData(userData: FirebaseUserData)
            = CoroutineScope(Dispatchers.IO).launch {
        try {
            Firebase.firestore
                .collection("UserData")
                .document(userData.email)
                .collection("UserInfo")
                .document("Data")
                .set(userData)
                .await()
        } catch (e: Exception) {
            throw e
        }
    }

    // Función para recibir la foto del Usuario guardada en Firebase Storage
    private fun fetchUserImage(imageName: String) {
        val storage = Firebase.storage
        val storageRef = storage.reference
        val imagesRef = storageRef.child("UserProfilePictures")
        val imageRef = imagesRef.child("$imageName.png")

        val localFile = File(this.filesDir, "$imageName.png")

        if (localFile.exists()) {
            // File already exists locally, no need to download
            return
        }

        imageRef.getFile(localFile)
            .addOnSuccessListener {
                // Image downloaded successfully
                // You can perform any further operations with the downloaded image here
            }
            .addOnFailureListener { exception ->
                // Handle any errors that occurred during the download
            }
    }

    // Función para recibir la foto del Usuario guardada en Firebase Storage
    private fun fetchFriendsImages(friends: List<FirebaseUserData>) {
        val userIDs: List<String> = friends.map { userData -> userData.ID }

        for (imageName in userIDs) {
            val storage = Firebase.storage
            val storageRef = storage.reference
            val imagesRef = storageRef.child("UserProfilePictures")
            val imageRef = imagesRef.child("$imageName.png")

            val localFile = File(this.filesDir, "$imageName.png")

            if (localFile.exists()) {
                // File already exists locally, no need to download
                continue
            }

            imageRef.getFile(localFile)
                .addOnSuccessListener {
                    // Image downloaded successfully
                    // You can perform any further operations with the downloaded image here
                }
                .addOnFailureListener { exception ->
                    // Handle any errors that occurred during the download
                }
        }
    }

    private fun saveUserImage(image: Drawable, imageName:String) {
        // Update the image in Firebase Storage
        val storage = Firebase.storage
        val storageRef = storage.reference
        val imagesRef = storageRef.child("UserProfilePictures")
        val imageRef = imagesRef.child("$imageName.png")

        // Convert the drawable resource to a byte array
        val bitmap = drawableToBitmap(image)
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
        val imageData = baos.toByteArray()

        val uploadTask = imageRef.putBytes(imageData)

        uploadTask.addOnSuccessListener { taskSnapshot ->
            // Image uploaded successfully

            // Update the local file
            val localFile = File(this.filesDir, "$imageName.png")
            if (localFile.exists()) {
                // Delete the existing local file
                localFile.delete()
            }

            // Save the updated image locally
            val fileOutputStream = FileOutputStream(localFile)
            fileOutputStream.write(imageData)
            fileOutputStream.close()

        }.addOnFailureListener { exception ->
            // Handle any errors that occurred during the upload
        }
    }

    private fun drawableToBitmap(drawable: Drawable): Bitmap {
        if (drawable is BitmapDrawable) {
            return drawable.bitmap
        }

        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

    private suspend fun getDeviceToken(): String {
        return try {
            FirebaseMessaging.getInstance().token.await()
        } catch (exception: Exception) {
            throw IllegalStateException("Failed to get device token", exception)
        }
    }

    private suspend fun updateDeviceToken(newDeviceToken: String, userEmail: String) {
        val userDataRef = Firebase.firestore
            .collection("UserData")
            .document(userEmail)
            .collection("UserInfo")
            .document("Data")

        val updateDeviceToken = hashMapOf<String, Any>(
            "deviceToken" to newDeviceToken
        )

        userDataRef.update(updateDeviceToken).await()
    }

}