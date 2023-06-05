package com.example.nq.authFirebase

data class SignInResult(
    val data: UserData?,
    val errorMessage: String?
)

data class UserData(
    val userID: String?,
    val name: String?,
    val mail: String?,
    val profilePictureURL: String?
)