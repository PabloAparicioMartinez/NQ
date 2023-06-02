package com.example.nq.profileActivities

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.lifecycleScope
import com.example.nq.R
import com.example.nq.dataStore.DataStoreManager
import com.example.nq.firebaseAuth.FirebaseAuthManager
import com.example.nq.firebaseAuth.UserData
import com.google.android.gms.auth.api.identity.Identity
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_profile_profile.*
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
                Picasso.get().load(userData?.profilePictureURL).into(profileProfile_image)

                setLayoutVisibilities(listOf(View.VISIBLE, View.GONE))
            }
        } else {
            // USUARIO NO CONECTADO
            setLayoutVisibilities(listOf(View.VISIBLE, View.GONE))
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

            lifecycleScope.launch {
                val newUserName = "$firstName $lastName"

                if (firebaseAuthManager.updateUser(newUserName)) {

                    val firstNameKey = stringPreferencesKey("userFirstName")
                    dataStoreManager.saveStringToDataStore(firstNameKey, firstName)

                    val lastNameKey = stringPreferencesKey("userLastName")
                    dataStoreManager.saveStringToDataStore(lastNameKey, lastName)

                    finish()
                } else {
                    Toast.makeText(this@ProfileActivityProfile, "Ha ocurrido un error...", Toast.LENGTH_SHORT).show()
                }
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

    private fun setLayoutVisibilities(listOfVisibilities: List<Int>) {
        profileProfile_profileLayout.visibility = listOfVisibilities[0]
        profileProfile_loadingLayout.visibility = listOfVisibilities[1]
    }

    fun String.isOnlyLetters() : Boolean {
        return all { it.isLetter() || it.isWhitespace() }
    }
}