package com.example.nq.profileActivities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.example.nq.R
import com.example.nq.recyclerViewDates.DatesAdapter
import com.example.nq.recyclerViewDates.DatesRepository
import com.example.nq.recyclerViewProfilePictures.ProfilePicturesAdapter
import com.example.nq.recyclerViewProfilePictures.ProfilePicturesData
import com.example.nq.recyclerViewProfilePictures.ProfilePicturesInterface
import com.example.nq.recyclerViewProfilePictures.ProfilePicturesRepository
import kotlinx.android.synthetic.main.activity_profile_profile_images.*

class ProfileProfileImages : AppCompatActivity(), ProfilePicturesInterface {

    private val profilePictues = ProfilePicturesAdapter(ProfilePicturesRepository.profilePictures, this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_profile_images)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.title = ""
            actionBar.setDisplayHomeAsUpEnabled(true)
        }

        profileProfile_recyclerView.setHasFixedSize(true)
        profileProfile_recyclerView.adapter = profilePictues
        profileProfile_recyclerView.layoutManager = GridLayoutManager(this, 2)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onItemClick(profilePicturesData: ProfilePicturesData, pictureInt: Int) {

        val intent = Intent()
            intent.putExtra("imageInt", pictureInt)
            setResult(Activity.RESULT_OK, intent)
            finish()
    }
}