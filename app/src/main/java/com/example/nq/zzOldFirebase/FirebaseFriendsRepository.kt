package com.example.nq.firebase

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

object FirebaseFriendsRepository {

    internal val userFriends = mutableListOf<FirebaseUserData>()

    // Function to fetch and save Users data from Firebase
    internal suspend fun fetchFriendsData(emailList: List<String>) {
        try {
            userFriends.clear() // Clear the existing list before adding new data
            for (email in emailList) {
                val userData = Firebase.firestore
                    .collection("UserData").document(email)
                    .collection("UserInfo").document("Data")
                    .get()
                    .await()
                    .toObject<FirebaseUserData>()

                if (userData != null) {
                    userFriends.add(userData)
                }
            }
        }
        catch (e: Exception) {
            throw e
        }
    }

}

