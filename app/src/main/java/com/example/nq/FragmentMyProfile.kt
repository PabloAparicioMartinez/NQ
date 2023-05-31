package com.example.nq

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.nq.firebase.FirebaseManager
import com.example.nq.firebase.FirebaseRepository
import com.example.nq.profileActivities.ProfileActivityFriends
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.fragment_my_profile.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class FragmentMyProfile : Fragment(R.layout.fragment_my_profile) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity).supportActionBar?.title = "PERFIL"

        //SET LAYOUT DEPENDING ON USER LOGGED IN STATE
        if (!FirebaseManager().checkIfUserIsSignedIn()) {
            setLayoutVisibilities(listOf(View.GONE, View.VISIBLE, View.GONE))
        } else {
            setLayoutVisibilities(listOf(View.VISIBLE, View.GONE, View.GONE))

            val fragMyProfileName = "${FirebaseRepository.userName} ${FirebaseRepository.userSurnames}"
            fragMyProfile_name.text = fragMyProfileName
            fragMyProfile_image.setImageURI(FirebaseRepository.userImage)
            fragMyProfile_mail.text = FirebaseRepository.userEmail
        }

        //BUTTONS
        fragMyProfile_signInButton.setOnClickListener() {
            Intent(activity, SignInActivity::class.java).also {
                startActivity(it)
            }
        }

        fragMyProfile_signOutButton.setOnClickListener() {
            showSignOutCard()
        }

        fragMyProfile_friendsList.setOnClickListener() {
            Intent(activity, ProfileActivityFriends::class.java).also {
                startActivity(it)
            }
        }

    }

    private fun setLayoutVisibilities(listOfVisibilities: List<Int>) {
        fragMyProfile_signedInLayout.visibility = listOfVisibilities[0]
        fragMyProfile_unsignedInLayout.visibility = listOfVisibilities[1]
        fragMyProfile_loadingLayout.visibility = listOfVisibilities[2]
    }

    private fun showSignOutCard() {

        val builder = MaterialAlertDialogBuilder(requireContext(), R.style.NQ_AlertDialog_TicketCard)

        val title = TextView(requireContext())
        title.text = "Cerrar sesión"
        title.setTextAppearance(R.style.NQ_AlertDialog_Title)
        title.setPadding(64, 48, 0, 0)

        builder.setCustomTitle(title)
        builder.setMessage("¿Estás seguro de que quieres cerrar sesión?")
        builder.setPositiveButton("Confirmar") { dialog, which ->
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
        FirebaseManager().signOutUser()

        delay(2000)

        setLayoutVisibilities(listOf(View.GONE, View.VISIBLE, View.GONE))
        Toast.makeText(activity, "Sesión cerrada", Toast.LENGTH_SHORT).show()
    }
}

