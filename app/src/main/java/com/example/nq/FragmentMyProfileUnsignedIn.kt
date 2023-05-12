package com.example.nq

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.fragment_my_profile_unsigned_in.*

class FragmentMyProfileUnsignedIn : Fragment(R.layout.fragment_my_profile_unsigned_in) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity).supportActionBar?.title = "PERFIL"

        fragMyProfileUnsignedIn_button.setOnClickListener {
            Intent(activity, SignInActivity::class.java).also {
                startActivity(it)
            }
        }
    }

}