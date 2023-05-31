package com.example.nq.recyclerViewFriends

import com.example.nq.firebase.FirebaseUserData

interface FriendsInterface {
    fun onItemClick(usersData: FirebaseUserData)
}