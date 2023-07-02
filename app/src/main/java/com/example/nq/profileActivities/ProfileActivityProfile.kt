package com.example.nq.profileActivities

import CircleTransform
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.lifecycleScope
import com.example.nq.R
import com.example.nq.authFirebase.FirebaseAuthManager
import com.example.nq.recyclerViewProfilePictures.ProfilePicturesRepository
import com.example.nq.storageFirebase.FirebaseRepository
import com.google.android.gms.auth.api.identity.Identity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_profile_profile.*
import kotlinx.android.synthetic.main.activity_profile_profile.profileProfile_profileLayout
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

class ProfileActivityProfile : AppCompatActivity() {

    private val firebaseAuthManager by lazy {
        FirebaseAuthManager(
            context = applicationContext,
            oneTapClient = Identity.getSignInClient(applicationContext)
        )
    }

    private var imageChanged: Boolean = false
    private var pictureInt: Int = 0

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

            // USUARIO CONECTADO
            profileProfile_firstNameText.setText(FirebaseRepository.userName)
            profileProfile_lastNameText.setText(FirebaseRepository.userSurnames)

            val imageName = FirebaseRepository.userID
            val userPicture = File(this.filesDir, "${imageName}.png")
            if (userPicture.exists()) {
                val userPictureUrl = Uri.fromFile(userPicture).toString()
                val cacheBustingUrl = "$userPictureUrl?${System.currentTimeMillis()}"
                Picasso.get()
                    .load(cacheBustingUrl)
                    .transform(CircleTransform())
                    .into(profileProfile_image)
            } else {
                Picasso.get()
                    .load(R.drawable.png_nq)
                    .transform(CircleTransform())
                    .into(profileProfile_image)
            }

            /*val imageName = FirebaseRepository.userID
            val userPicture = File(this.filesDir, "${imageName}.png")
            val newImageDrawable: Drawable? = if (userPicture.exists()) {
                Drawable.createFromPath(userPicture.absolutePath)
            } else {
                ResourcesCompat.getDrawable(resources, R.drawable.png_user, null)
            }

            profileProfile_image.setImageDrawable(newImageDrawable)*/

            setLayoutVisibilities(listOf(View.VISIBLE, View.GONE))

        } else {
            // USUARIO NO CONECTADO
            setLayoutVisibilities(listOf(View.VISIBLE, View.GONE))
        }

        // CAMBIAR IMAGEN
        profileProfile_clickableImage.setOnClickListener {
            val intent = Intent(this, ProfileActivityProfileImages::class.java)
            imagesLauncher.launch(intent)
        }

        // GUARDAR CAMBIOS
        profileProfile_saveChangesButton.setOnClickListener {

            val newName = profileProfile_firstNameText.text.toString()
            val newSurnames = profileProfile_lastNameText.text.toString()

            if (newName.isEmpty()) {
                profileProfile_firstNameLayout.error = "Introduce tu nombre"
                return@setOnClickListener
            }
            if (!newName.isOnlyLetters()) {
                profileProfile_firstNameLayout.error = "Introduce un nombre válido"
                return@setOnClickListener
            }
            if (newSurnames.isEmpty()) {
                profileProfile_lastNameLayout.error = "Introduce tu(s) apellido(s)"
                return@setOnClickListener
            }
            if (!newSurnames.isOnlyLetters()) {
                profileProfile_lastNameLayout.error = "Introduce un(os) apellido(s) válido(s)"
                return@setOnClickListener
            }

            val oldName = FirebaseRepository.userName
            val oldSurnames = FirebaseRepository.userSurnames

            val oldCompleteName = "$oldName $oldSurnames"
            val newCompleteName = "$newName $newSurnames"

            val nameChanged = oldCompleteName != newCompleteName

            if (nameChanged || imageChanged) {

                lifecycleScope.launch {

                    setLayoutVisibilities(listOf(View.VISIBLE, View.VISIBLE))

                    if (nameChanged) {

                        if (firebaseAuthManager.updateUserName(newCompleteName)) {
                            updateFirebaseUserName(newName, newSurnames)
                            FirebaseRepository.userName = newName
                            FirebaseRepository.userSurnames = newSurnames
                        } else {
                            Toast.makeText(this@ProfileActivityProfile, "No se pudo actualizar el nombre...", Toast.LENGTH_SHORT).show()
                        }
                    }

                    if (imageChanged) {
                        val newUserImageString = "android.resource://$packageName/${ProfilePicturesRepository.returnPictureString(pictureInt)}"
                        val resourceString = ProfilePicturesRepository.returnPictureString(pictureInt)
                        val resourceName = resourceString.substringAfterLast(".")
                        val newUserImageResId = resources.getIdentifier(resourceName, "drawable", packageName)
                        val newImageDrawable = ResourcesCompat.getDrawable(resources, newUserImageResId, null)

                        if (firebaseAuthManager.updateUserImage(Uri.parse(newUserImageString))) {
                            updateUserImage(newImageDrawable!!)
                        } else {
                            Toast.makeText(this@ProfileActivityProfile, "No se pudo actualizar la imagen...", Toast.LENGTH_SHORT).show()
                        }
                    }

                    if (nameChanged && imageChanged) {
                        Toast.makeText(this@ProfileActivityProfile, "¡Tu datos se han actualizado correctamente!", Toast.LENGTH_SHORT).show()
                        imageChanged = false
                    }

                    else if (nameChanged) {
                        Toast.makeText(this@ProfileActivityProfile, "¡Tu nombre y apellidos se han actualizado correctamente!", Toast.LENGTH_SHORT).show()
                    }
                    else if (imageChanged) {
                        Toast.makeText(this@ProfileActivityProfile, "¡Tu imagen se ha actualizado correctamente!", Toast.LENGTH_SHORT).show()
                        imageChanged = false
                    }

                    setLayoutVisibilities(listOf(View.VISIBLE, View.GONE))

                    // finish()
                }

            } else {
                if (!nameChanged && !imageChanged) {
                    Toast.makeText(this, "¡Tu datos son los mismos que antes!", Toast.LENGTH_SHORT).show()
                }

                else if (!nameChanged) {
                    Toast.makeText(this, "¡Tu nombre y apellidos son los mismos que antes!", Toast.LENGTH_SHORT).show()
                }
                else if (!imageChanged) {
                    Toast.makeText(this, "¡La imagen seleccionada es la misma que antes!", Toast.LENGTH_SHORT).show()
                }

            }
        }

        // ESCONDER TECLADO
        profileLayout.setOnClickListener {
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

                val newImage = ProfilePicturesRepository.profilePictures[receivedData!!].profileImage
                val newImageDrawable = ResourcesCompat.getDrawable(resources, newImage, null)
                profileProfile_image.setImageDrawable(newImageDrawable)

                pictureInt = receivedData
                imageChanged = true

            } else {
                // Pass
            }
        }

    private fun setLayoutVisibilities(listOfVisibilities: List<Int>) {
        profileProfile_profileLayout.visibility = listOfVisibilities[0]
        profileProfile_loadingLayout.visibility = listOfVisibilities[1]
    }

    private fun String.isOnlyLetters() : Boolean {
        return all { it.isLetter() || it.isWhitespace() }
    }

    private suspend fun updateFirebaseUserName(newName: String, newSurnames: String) {
        val userEmail = FirebaseRepository.userEmail

        val userDataRef = Firebase.firestore
            .collection("UserData")
            .document(userEmail)
            .collection("UserInfo")
            .document("Data")

        val updateCompleteName = hashMapOf<String, Any>(
            "name" to newName,
            "surnames" to newSurnames
        )

        userDataRef.update(updateCompleteName).await()
    }

    private fun updateUserImage(image: Drawable) {
        // Update the image in Firebase Storage
        val imageName = FirebaseRepository.userID
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