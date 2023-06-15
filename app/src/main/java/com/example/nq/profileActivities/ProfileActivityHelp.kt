package com.example.nq.profileActivities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.example.nq.R
import com.example.nq.listHelpQuestions.HelpAdapter
import com.example.nq.listHelpQuestions.HelpData
import com.example.nq.listHelpQuestions.HelpRepository
import kotlinx.android.synthetic.main.activity_profile_help.*

class ProfileActivityHelp : AppCompatActivity() {

    private lateinit var expandableListAdapter: HelpAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_help)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.title = ""
            actionBar.setDisplayHomeAsUpEnabled(true)
        }

        expandableListAdapter = HelpAdapter(this, HelpRepository.returnHelpList())
        profileHelp_expandableList.setAdapter(expandableListAdapter)
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