package com.example.nq

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_event_screen_not_available.*

class EventScreenActivityNotAvailable : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_screen_not_available)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.title = ""
            actionBar.setDisplayHomeAsUpEnabled(true)
        }

        eventScreenNot_image.setImageResource(intent.getIntExtra("EXTRA_IMAGE", -1))
        eventScreenNot_name.text = intent.getStringExtra("EXTRA_NAME")
        eventScreenNot_music.text = intent.getStringExtra("EXTRA_MUSIC")
        eventScreenNot_date.text = intent.getStringExtra("EXTRA_DATE")
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