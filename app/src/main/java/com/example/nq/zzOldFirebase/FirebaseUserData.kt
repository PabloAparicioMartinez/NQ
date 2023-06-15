package com.example.nq.zzOldFirebase

import com.example.nq.R

data class FirebaseUserData(
    var name: String = "NOMBRE",
    var surnames:String = "APELLIDOS",
    var uri: String = "android.resource://com.example.nq/${R.drawable.png_boy_01}",
    var email: String = "EMAIL",
    var friendEmails: List<String> = listOf()
)