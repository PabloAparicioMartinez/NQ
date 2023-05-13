package com.example.nq

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.example.nq.firebase.FirebaseRepository
import kotlinx.android.synthetic.main.fragment_my_profile_signed_in.*
import kotlinx.android.synthetic.main.fragment_my_profile_unsigned_in.*
import kotlin.math.sign

class FragmentMyProfileSignedIn : Fragment(R.layout.fragment_my_profile_signed_in) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity).supportActionBar?.title = "PERFIL"

        fragMyProfile_name.setText(FirebaseRepository.userName)
        fragMyProfile_image.setImageURI(FirebaseRepository.userImage)
        fragMyProfile_mail.setText(FirebaseRepository.userGmail)
    }
}

