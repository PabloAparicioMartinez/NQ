package com.example.nq.recyclerViewFriendsList

import com.example.nq.firebase.FirebaseUserData

interface FriendsListInterface {
    fun onItemClick(usersData: FirebaseUserData)
}