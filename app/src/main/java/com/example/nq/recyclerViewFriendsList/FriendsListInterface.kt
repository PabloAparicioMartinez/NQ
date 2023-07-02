package com.example.nq.recyclerViewFriendsList

import com.example.nq.storageFirebase.FirebaseUserData

interface FriendsListInterface {
    fun onItemClick(usersData: FirebaseUserData)
}