package com.example.nq.zzOldFirebase

import com.google.firebase.auth.FirebaseAuth

class FirebaseManager {

    fun checkIfUserIsSignedIn() : Boolean {
        return FirebaseAuth.getInstance().currentUser != null
    }

    fun signOutUser() {
        val auth = FirebaseAuth.getInstance()
        auth.signOut()
    }
}