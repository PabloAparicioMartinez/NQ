package com.example.nq.recyclerViewFriendsList

import com.example.nq.authFirebase.FirebaseUserData

interface FriendsListInterface {
    fun onItemClick(usersData: FirebaseUserData)
}