package com.example.nq

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.nq.firebaseAuth.FirebaseAuthManager
import com.example.nq.firebaseAuth.UserData
import com.example.nq.profileActivities.ProfileActivityFriends
import com.example.nq.profileActivities.ProfileActivityHistory
import com.example.nq.profileActivities.ProfileActivityProfile
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_my_profile.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class FragmentMyProfile : Fragment(R.layout.fragment_my_profile) {

    private val firebaseAuthManager by lazy {
        FirebaseAuthManager(
            context = requireContext(),
            oneTapClient = Identity.getSignInClient(requireContext())
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity).supportActionBar?.title = "PERFIL"

        //SET LAYOUT DEPENDING ON USER LOGGED IN STATE
        if (firebaseAuthManager.getSignedInUser() != null) {
            setLayoutVisibilities(listOf(View.VISIBLE, View.GONE, View.GONE))

            val userData: UserData? = firebaseAuthManager.getSignedInUser()
            fragMyProfile_name.text = userData?.name ?: "Name"
            fragMyProfile_mail.text = userData?.mail ?: "Mail"
            Picasso.get().load(userData?.profilePictureURL).into(fragMyProfile_image)
        } else {
            setLayoutVisibilities(listOf(View.GONE, View.VISIBLE, View.GONE))
        }

        //BUTTONS
        fragMyProfile_profileButton.setOnClickListener() {
            Intent(activity, ProfileActivityProfile::class.java).also {
                startActivity(it)
            }
        }

        fragMyProfile_friendsButton.setOnClickListener() {
            Intent(activity, ProfileActivityFriends::class.java).also {
                startActivity(it)
            }
        }

        fragMyProfile_paymentsButton.setOnClickListener() {
            Intent(activity, ProfileActivityHistory::class.java).also {
                startActivity(it)
            }
        }

        fragMyProfile_signOutButton.setOnClickListener() {
            showSignOutAlertDialong()
        }

        fragMyProfile_signInButton.setOnClickListener() {
            Intent(activity, SignInActivity::class.java).also {
                startActivity(it)
            }
        }
    }

    private fun setLayoutVisibilities(listOfVisibilities: List<Int>) {
        fragMyProfile_signedInLayout.visibility = listOfVisibilities[0]
        fragMyProfile_unsignedInLayout.visibility = listOfVisibilities[1]
        fragMyProfile_loadingLayout.visibility = listOfVisibilities[2]
    }

    fun showSignOutAlertDialong() {

        val builder = MaterialAlertDialogBuilder(requireContext(), R.style.NQ_AlertDialog_TicketCard)

        val title = TextView(requireContext())
        title.text = "Cerrar sesión"
        title.setTextAppearance(R.style.NQ_AlertDialog_Title)
        title.setPadding(64, 48, 0, 0)

        builder.setCustomTitle(title)
        builder.setMessage("¿Estás seguro de que quieres cerrar sesión?")
        builder.setPositiveButton("CONFIRMAR") { dialog, which ->
            lifecycleScope.launch {
                signOutUser()
            }
        }
        builder.setNegativeButton("Cancelar") { dialog, which ->

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

