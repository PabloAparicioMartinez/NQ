package com.example.nq

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_my_tickets_signed_in.*

class FragmentMyTicketsSignedIn : Fragment(R.layout.fragment_my_tickets_signed_in) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fragMyTicketsSignedIn_button.setOnClickListener(){
            (activity as MainActivity).clickOnBuyTicketsMenuIcon()
        }
    }
}