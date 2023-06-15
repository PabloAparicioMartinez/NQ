package com.example.nq.authFirebase

data class SignInState(
    val isSignInSuccessful: Boolean = false,
    val signInError: String? = null
)
