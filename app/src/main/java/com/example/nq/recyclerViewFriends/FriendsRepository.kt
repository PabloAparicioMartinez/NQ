package com.example.nq.recyclerViewFriends

import com.example.nq.R

object FriendsRepository {

    val friends = mutableListOf<UsersData>(
        UsersData(R.drawable.png_boy_01,"Adrián Pascual", "adri@gmail.com", true),
        UsersData(R.drawable.png_boy_01,"Iñigo Perez", "iñigo@gmail.com", true),
        UsersData(R.drawable.png_boy_01,"David Maldonado", "david@gmail.com", true)
    )
}