package com.example.nq

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.nq.authFirebase.FirebaseAuthManager
import com.example.nq.authSignIn.SignInActivity
import com.example.nq.profileActivities.*
import com.example.nq.storageFirebase.FirebaseRepository
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_my_profile.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import CircleTransform as CircleTransform

class FragmentMyProfile : Fragment(R.layout.fragment_my_profile) {

    private val firebaseAuthManager by lazy {
        FirebaseAuthManager(
            context = requireContext(),
            oneTapClient = Identity.getSignInClient(requireContext())
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //SET LAYOUT DEPENDING ON USER LOGGED IN STATE
        if (firebaseAuthManager.getSignedInUser() != null) {
            setUserInfoUI()
        } else {
            setLayoutVisibilities(listOf(View.GONE, View.VISIBLE, View.GONE))
        }

        //BUTTONS
        fragMyProfile_profileButton.setOnClickListener {
            Intent(activity, ProfileActivityProfile::class.java).also {
                startActivity(it)
            }
        }

        fragMyProfile_friendsButton.setOnClickListener {
            Intent(activity, ProfileActivityFriends::class.java).also {
                startActivity(it)
            }
        }

        fragMyProfile_paymentsButton.setOnClickListener {
            Intent(activity, ProfileActivityPayments::class.java).also {
                startActivity(it)
            }
        }

        fragMyProfile_historyButton.setOnClickListener {
            Intent(activity, ProfileActivityHistory::class.java).also {
                startActivity(it)
            }
        }

        fragMyProfile_problemButton.setOnClickListener {
            Intent(activity, ProfileActivityLegal::class.java).also {
                startActivity(it)
            }
        }

        fragMyProfile_helpButton.setOnClickListener {
            Intent(activity, ProfileActivityHelp::class.java).also {
                startActivity(it)
            }
        }

        fragMyProfile_signOutButton.setOnClickListener {
            showSignOutAlertDialog()
        }

        fragMyProfile_signInButton.setOnClickListener {
            Intent(activity, SignInActivity::class.java).also {
                it.putExtra("EXTRA_SignInClicked", true)
                startActivity(it)
            }
        }
    }

    override fun onResume() {
        super.onResume()

        //SET LAYOUT DEPENDING ON USER LOGGED IN STATE
        if (firebaseAuthManager.getSignedInUser() != null) {
            setUserInfoUI()
        } else {
            setLayoutVisibilities(listOf(View.GONE, View.VISIBLE, View.GONE))
        }
    }

    private fun setUserInfoUI() {
        setLayoutVisibilities(listOf(View.VISIBLE, View.GONE, View.GONE))

        val showName = "${FirebaseRepository.userName} ${FirebaseRepository.userSurnames}"
        fragMyProfile_name.text = showName
        fragMyProfile_mail.text = FirebaseRepository.userEmail

        val userPicture = File(requireContext().filesDir, "${FirebaseRepository.userID}.png")
        if (userPicture.exists()) {
            val userPictureUrl = Uri.fromFile(userPicture).toString()
            val cacheBustingUrl = "$userPictureUrl?${System.currentTimeMillis()}"
            Picasso.get()
                .load(cacheBustingUrl)
                .memoryPolicy(MemoryPolicy.NO_CACHE)  // Disable memory cache
                .networkPolicy(NetworkPolicy.NO_CACHE)  // Disable network cache
                .transform(CircleTransform())
                .into(fragMyProfile_image)
        } else {
            Picasso.get()
                .load(R.drawable.png_nq)
                .transform(CircleTransform())
                .into(fragMyProfile_image)
        }

        /*val imageName = FirebaseRepository.userID
        val userPicture = File(requireContext().filesDir, "${imageName}.png")
        val newImageDrawable: Drawable? = if (userPicture.exists()) {
            Drawable.createFromPath(userPicture.absolutePath)
        } else {
            ResourcesCompat.getDrawable(resources, R.drawable.png_user, null)
        }

        fragMyProfile_image.setImageDrawable(newImageDrawable)*/

    }

    private fun setLayoutVisibilities(listOfVisibilities: List<Int>) {
        fragMyProfile_signedInLayout.visibility = listOfVisibilities[0]
        fragMyProfile_unsignedInLayout.visibility = listOfVisibilities[1]
        fragMyProfile_loadingLayout.visibility = listOfVisibilities[2]
    }

    private fun showSignOutAlertDialog() {

        val builder = MaterialAlertDialogBuilder(requireContext(), R.style.NQ_AlertDialogs)

        val title = TextView(requireContext())
        title.text = "CERRAR SESIÓN"
        title.setTextAppearance(R.style.NQ_AlertDialog_Title)
        title.setPadding(64, 48, 0, 0)

        builder.setCustomTitle(title)
        builder.setMessage("¿Estás seguro de que quieres cerrar sesión?")
        builder.setPositiveButton("CONFIRMAR") { _, _ ->
            lifecycleScope.launch {
                signOutUser()
            }
        }
        builder.setNegativeButton("Cancelar") { _, _ ->

        }

        builder.show()
    }

    private suspend fun signOutUser() {

        setLayoutVisibilities(listOf(View.VISIBLE, View.GONE, View.VISIBLE))
        firebaseAuthManager.signOut()

        delay(500)

        setLayoutVisibilities(listOf(View.GONE, View.VISIBLE, View.GONE))
        Toast.makeText(activity, "Sesión cerrada", Toast.LENGTH_SHORT).show()
    }
}


