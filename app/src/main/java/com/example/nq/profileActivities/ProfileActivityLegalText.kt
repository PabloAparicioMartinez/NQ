package com.example.nq.profileActivities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import com.example.nq.R
import kotlinx.android.synthetic.main.activity_profile_legal_text.*

class ProfileActivityLegalText : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_legal_text)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.title = ""
            actionBar.setDisplayHomeAsUpEnabled(true)
        }

        when(intent.getStringExtra("EXTRA_LEGAL_BUTTON")){
            "Conditions" -> {
                profileLegalText_title.text = "Términos y condiciones:"
                val inputStream = resources.openRawResource(R.raw.text_conditions)
                val text = inputStream.bufferedReader().use { it.readText() }
                profileLegalText_text.text = text
            }
            "Privacy" -> {
                profileLegalText_title.text = "Política de privacidad:"
                val inputStream = resources.openRawResource(R.raw.text_privacy)
                val text = inputStream.bufferedReader().use { it.readText() }
                profileLegalText_text.text = text
            }
            "Remboursement" -> {
                profileLegalText_title.text = "Política de reembolso y cancelación:"
                val inputStream = resources.openRawResource(R.raw.text_remboursement)
                val text = inputStream.bufferedReader().use { it.readText() }
                profileLegalText_text.text = text
            }
            "Cookies" -> {
                profileLegalText_title.text = "Política de cookies:"
                val inputStream = resources.openRawResource(R.raw.text_cookies)
                val text = inputStream.bufferedReader().use { it.readText() }
                profileLegalText_text.text = text
            }
            "Age" -> {
                profileLegalText_title.text = "Restricciones de edad:"
                val inputStream = resources.openRawResource(R.raw.text_age)
                val text = inputStream.bufferedReader().use { it.readText() }
                profileLegalText_text.text = text
            }
        }

        setLayoutsVisibilities(listOf(View.VISIBLE))
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

    fun setLayoutsVisibilities (layoutVisibility: List<Int>){
        profileLegalText_layout.visibility = layoutVisibility[0]
    }
}