package com.example.nq.firebase

import com.google.firebase.auth.FirebaseAuth

class FirebaseManager {

    fun checkIfUserIsSignedInToFirebase() : Boolean {
        return FirebaseAuth.getInstance().currentUser != null
    }

}