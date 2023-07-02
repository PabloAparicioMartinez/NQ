package com.example.nq.authSignIn

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
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
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.activity_sign_in.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

class SignInActivity : AppCompatActivity() {

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
        //Thread.sleep(1000)

        setContentView(R.layout.activity_sign_in)

        // Inicializar INICIAR SESIÓN: si el usuario ha iniciado sesión, se le mantiene conectado
        lifecycleScope.launch {

            val signedInUser = firebaseAuthManager.getSignedInUser()

            if (signedInUser != null) {

                // SE MANTIENE INCIADA SESIÓN

                // Cargar los datos desde Firebase al repositorio de Usuario
                val signedUserEmail = signedInUser.mail
                val userData = fetchUserData(signedUserEmail!!)
                FirebaseRepository.userName = userData.name
                FirebaseRepository.userSurnames = userData.surnames
                FirebaseRepository.userID = userData.ID
                FirebaseRepository.userEmail = userData.email
                val emailList:List<String> = userData.friendEmails
                FirebaseRepository.userFriendEmails = emailList.toMutableList()
                // Cargar la lista con la info de los amigos al repositorio de Amigos
                FirebaseFriendsRepository.fetchFriendsData(emailList)
                // Cargar la lista de tickets al repositorio de Tickets
                TicketsRepository.fetchTicketData(signedUserEmail)
                // Actualizar la lista de imágenes de amigos
                fetchFriendsImages(FirebaseFriendsRepository.userFriends)

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

                    // Hay que comprobar si es la primera vez que entra o si ya estaba registrado
                    val userDataGoogle: UserData? = firebaseAuthManager.getSignedInUser()
                    val originalName = userDataGoogle?.name ?: "Nombre"
                    val words = originalName.split(" ")

                    val firstName = words[0]
                    val restOfText = originalName.substring(firstName.length).trim()

                    var surNames = "Apellido(s)"
                    if (restOfText != "") surNames = restOfText

                    val userEmail = userDataGoogle?.mail
                    val userID = userDataGoogle?.userID

                    val userIsRegistered = checkIfUserExistsOnDataStore(userEmail)

                    if (userIsRegistered) {
                        // Cargar los datos desde Firebase al repositorio de Usuario
                        val userData = fetchUserData(userEmail!!)
                        fetchUserImage(userData.ID)
                        FirebaseRepository.userName = userData.name
                        FirebaseRepository.userSurnames = userData.surnames
                        FirebaseRepository.userID = userData.ID
                        FirebaseRepository.userEmail = userData.email
                        val emailList:List<String> = userData.friendEmails
                        FirebaseRepository.userFriendEmails = emailList as MutableList<String>
                        // Cargar la lista con la info de los amigos al repositorio de Amigos
                        FirebaseFriendsRepository.fetchFriendsData(emailList)
                        // Cargar la lista de tickets al repositorio de Tickets
                        TicketsRepository.fetchTicketData(userEmail)
                        // Actualizar la lista de imágenes de amigos
                        fetchFriendsImages(FirebaseFriendsRepository.userFriends)

                    } else {
                        // Cargar los datos en Firebase
                        val newUserImageResId = resources.getIdentifier("png_nq", "drawable", packageName)
                        val newImageDrawable = ResourcesCompat.getDrawable(resources, newUserImageResId, null)
                        val userData = FirebaseUserData(firstName,surNames,userID!!,userEmail!!)
                        saveUserData(userData)
                        saveUserImage(newImageDrawable!!,userID)
                        FirebaseRepository.userName = firstName
                        FirebaseRepository.userSurnames = surNames
                        FirebaseRepository.userID = userID
                        FirebaseRepository.userEmail = userEmail
                    }

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

}