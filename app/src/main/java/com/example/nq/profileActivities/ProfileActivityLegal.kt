package com.example.nq.profileActivities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.example.nq.EventScreenActivity
import com.example.nq.R
import kotlinx.android.synthetic.main.activity_profile_legal.*

class ProfileActivityLegal : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_legal)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.title = ""
            actionBar.setDisplayHomeAsUpEnabled(true)
        }

        profileLegal_conditions.setOnClickListener() {
            Intent(this, ProfileActivityLegalText::class.java).also {
                it.putExtra("EXTRA_LEGAL_BUTTON", "Conditions")
                startActivity(it)
            }
        }

        profileLegal_privacy.setOnClickListener() {
            Intent(this, ProfileActivityLegalText::class.java).also {
                it.putExtra("EXTRA_LEGAL_BUTTON", "Privacy")
                startActivity(it)
            }
        }

        profileLegal_remboursement.setOnClickListener() {
            Intent(this, ProfileActivityLegalText::class.java).also {
                it.putExtra("EXTRA_LEGAL_BUTTON", "Remboursement")
                startActivity(it)
            }
        }

        profileLegal_cookies.setOnClickListener() {
            Intent(this, ProfileActivityLegalText::class.java).also {
                it.putExtra("EXTRA_LEGAL_BUTTON", "Cookies")
                startActivity(it)
            }
        }

        profileLegal_age.setOnClickListener() {
            Intent(this, ProfileActivityLegalText::class.java).also {
                it.putExtra("EXTRA_LEGAL_BUTTON", "Age")
                startActivity(it)
            }
        }
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
}