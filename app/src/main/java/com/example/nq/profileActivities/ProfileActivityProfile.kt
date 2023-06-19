package com.example.nq.profileActivities

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.lifecycleScope
import com.example.nq.R
import com.example.nq.authFirebase.FirebaseAuthManager
import com.example.nq.dataStore.DataStoreManager
import com.example.nq.authFirebase.UserData
import com.example.nq.recyclerViewProfilePictures.ProfilePicturesRepository
import com.google.android.gms.auth.api.identity.Identity
import kotlinx.android.synthetic.main.activity_profile_profile.*
import kotlinx.android.synthetic.main.activity_profile_profile.profileProfile_profileLayout
import kotlinx.coroutines.launch

class ProfileActivityProfile : AppCompatActivity() {

    private val firebaseAuthManager by lazy {
        FirebaseAuthManager(
            context = applicationContext,
            oneTapClient = Identity.getSignInClient(applicationContext)
        )
    }
    private val dataStoreManager by lazy {
        DataStoreManager(context = applicationContext)
    }

    lateinit var startName: String
    lateinit var startImage: String
    var imageChanged: Boolean = false
    var pictureInt: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_profile)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.title = ""
            actionBar.setDisplayHomeAsUpEnabled(true)
        }

        setLayoutVisibilities(listOf(View.GONE, View.VISIBLE))

        //SET LAYOUT DEPENDING ON USER LOGGED IN STATE
        if (firebaseAuthManager.getSignedInUser() != null) {

            lifecycleScope.launch {

                val userData: UserData? = firebaseAuthManager.getSignedInUser()
                val userFirstName = dataStoreManager.getStringFromDataStore(stringPreferencesKey("userFirstName"), "Nombre")
                val userLastName = dataStoreManager.getStringFromDataStore(stringPreferencesKey("userLastName"), "Apellido(s)")

                profileProfile_firstNameText.setText(userFirstName)
                profileProfile_lastNameText.setText(userLastName)

                val pictureURI = userData?.profilePictureURL
                val pictureName = pictureURI?.substringAfterLast(".") ?: ""
                val pictureID = resources.getIdentifier(pictureName, "drawable", packageName)
                profileProfile_image.setImageResource(pictureID)

                startName = "$userFirstName $userLastName"
                startImage = userData?.profilePictureURL.toString()
                setLayoutVisibilities(listOf(View.VISIBLE, View.GONE))
            }
        } else {
            // USUARIO NO CONECTADO
            setLayoutVisibilities(listOf(View.VISIBLE, View.GONE))
        }

        // CAMBIAR IMAGEN
        profileProfile_clickableImage.setOnClickListener() {
            val intent = Intent(this, ProfileActivityProfileImages::class.java)
            imagesLauncher.launch(intent)
        }

        // GUARDAR CAMBIOS
        profileProfile_saveChangesButton.setOnClickListener() {

            val firstName = profileProfile_firstNameText.text.toString()
            val lastName = profileProfile_lastNameText.text.toString()

            if (firstName.isEmpty()) {
                profileProfile_firstNameLayout.error = "Introduce tu nombre"
                return@setOnClickListener
            }
            if (!firstName.isOnlyLetters()) {
                profileProfile_firstNameLayout.error = "Introduce un nombre válido"
                return@setOnClickListener
            }
            if (lastName.isEmpty()) {
                profileProfile_lastNameLayout.error = "Introduce tu(s) apellido(s)"
                return@setOnClickListener
            }
            if (!lastName.isOnlyLetters()) {
                profileProfile_lastNameLayout.error = "Introduce un(os) apellido(s) válido(s)"
                return@setOnClickListener
            }

            val newUserName = "$firstName $lastName"
            if (newUserName != startName || imageChanged) {

                lifecycleScope.launch {

                    setLayoutVisibilities(listOf(View.VISIBLE, View.VISIBLE))

                    if (newUserName != startName) {

                        if (firebaseAuthManager.updateUserName(newUserName)) {

                            val firstNameKey = stringPreferencesKey("userFirstName")
                            dataStoreManager.saveStringToDataStore(firstNameKey, firstName)

                            val lastNameKey = stringPreferencesKey("userLastName")
                            dataStoreManager.saveStringToDataStore(lastNameKey, lastName)

                        } else {
                            Toast.makeText(this@ProfileActivityProfile, "No se pudo actualizar el nombre...", Toast.LENGTH_SHORT).show()
                        }
                    }

                    if (imageChanged) {
                        val newUserImageURI = Uri.parse(
                            "android.resource://$packageName/${ProfilePicturesRepository.returnPictureString(pictureInt)}"
                        )
                        if (firebaseAuthManager.updateUserImage(newUserImageURI)) {
                            //
                        } else {
                            Toast.makeText(this@ProfileActivityProfile, "No se pudo actualizar la imagen...", Toast.LENGTH_SHORT).show()
                        }
                    }

                    setLayoutVisibilities(listOf(View.VISIBLE, View.GONE))
                    finish()
                }

            } else {
                if (newUserName == startName) {
                    Toast.makeText(this, "Mismo NOMBRE", Toast.LENGTH_SHORT).show()
                }
                if (!imageChanged) {
                    Toast.makeText(this, "Misma IMAGEN", Toast.LENGTH_SHORT).show()
                }

                finish()
            }
        }

        // ESCONDER TECLADO
        profileLayout.setOnClickListener() {
            val inputMethodManager = this.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(profileLayout.windowToken, 0)
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

    private val imagesLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {

                val receivedData = result.data?.getIntExtra("EXTRA_pictureInt", 0)
                profileProfile_image.setImageResource(ProfilePicturesRepository.profilePictures[receivedData!!].profileImage)
                pictureInt = receivedData
                imageChanged = true

            } else {
                //
            }
        }

    private fun setLayoutVisibilities(listOfVisibilities: List<Int>) {
        profileProfile_profileLayout.visibility = listOfVisibilities[0]
        profileProfile_loadingLayout.visibility = listOfVisibilities[1]
    }

    fun String.isOnlyLetters() : Boolean {
        return all { it.isLetter() || it.isWhitespace() }
    }
}