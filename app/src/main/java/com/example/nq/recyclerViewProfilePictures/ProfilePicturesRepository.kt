package com.example.nq.recyclerViewProfilePictures

import com.example.nq.R

object ProfilePicturesRepository {

    val profilePictures = mutableListOf(
        ProfilePicturesData(R.drawable.png_bird_01),
        ProfilePicturesData(R.drawable.png_cat_01),
        ProfilePicturesData(R.drawable.png_cat_02),
        ProfilePicturesData(R.drawable.png_cat_03),
        ProfilePicturesData(R.drawable.png_dog_01),
        ProfilePicturesData(R.drawable.png_dog_02),
        ProfilePicturesData(R.drawable.png_duck_01),
        ProfilePicturesData(R.drawable.png_hamster_01),
        ProfilePicturesData(R.drawable.png_snake_01),
        ProfilePicturesData(R.drawable.png_tiger_01)
    )

    fun returnPictureString(pictureInt: Int) : String {

        var pictureString = ""

        when (pictureInt) {
            0 -> pictureString = "R.drawable.png_bird_01"
            1 -> pictureString = "R.drawable.png_cat_01"
            2 -> pictureString = "R.drawable.png_cat_02"
            3 -> pictureString = "R.drawable.png_cat_03"
            4 -> pictureString = "R.drawable.png_dog_01"
            5 -> pictureString = "R.drawable.png_dog_02"
            6 -> pictureString = "R.drawable.png_duck_01"
            7 -> pictureString = "R.drawable.png_hamster_01"
            8 -> pictureString = "R.drawable.png_snake_01"
            9 -> pictureString = "R.drawable.png_tiger_01"
        }

        return pictureString
    }
}