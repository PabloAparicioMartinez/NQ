package com.example.nq

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.nq.firebase.FirebaseManager
import kotlinx.android.synthetic.main.fragment_my_tickets.*

class FragmentMyTickets : Fragment(R.layout.fragment_my_tickets) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity).supportActionBar?.title = "ENTRADAS"

        //SET LAYOUT DEPENDING ON USER LOGGED IN STATE
        if (!FirebaseManager().checkIfUserIsSignedIn()) {
            setLayoutVisibilities(listOf(View.GONE, View.VISIBLE))
        } else {
            setLayoutVisibilities(listOf(View.VISIBLE, View.GONE))
        }

        //BUTTONS
        fragMyTickets_buyTicketsSignedInButton.setOnClickListener {
            (activity as MainActivity).clickOnBuyTicketsMenuIcon()
        }

        fragMyTickets_signInButton.setOnClickListener() {
            Intent(activity, SignInActivity::class.java).also {
                startActivity(it)
            }
        }

        fragMyTickets_buyTicketsUnsignedInButton.setOnClickListener {
            (activity as MainActivity).clickOnBuyTicketsMenuIcon()
        }
    }

    private fun setLayoutVisibilities(listOfVisibilities: List<Int>) {
        fragMyTickets_signedInLayout.visibility = listOfVisibilities[0]
        fragMyTickets_unsignedInLayout.visibility = listOfVisibilities[1]
    }
}