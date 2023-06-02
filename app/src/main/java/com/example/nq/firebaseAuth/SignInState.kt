package com.example.nq.firebaseAuth

data class SignInState(
    val isSignInSuccessful: Boolean = false,
    val signInError: String? = null
)
