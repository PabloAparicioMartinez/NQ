package com.example.nq

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.nq.recyclerViewFriends.FriendsAdapter
import com.example.nq.recyclerViewFriends.FriendsInterface
import com.example.nq.recyclerViewFriends.FriendsRepository
import com.example.nq.recyclerViewFriends.UsersData
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.activity_event_screen_search.*
import kotlinx.coroutines.launch

class EventScreenActivitySearch : AppCompatActivity(), FriendsInterface {

    val friendsAdapter = FriendsAdapter(FriendsRepository.friends, this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_screen_search)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.title = ""
            actionBar.setDisplayHomeAsUpEnabled(true)
        }

        updateFriendsList()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            android.R.id.home -> {
                onBackPressed()
                return true
            }
            R.id.topMenuEvent_info -> {
                showInfoAlertDialog()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.top_menu_event_info, menu)
        return super.onCreateOptionsMenu(menu)
    }

    fun showInfoAlertDialog() {
        val builder = MaterialAlertDialogBuilder(this, R.style.NQ_AlertDialog_TicketCard)

        val title = TextView(this)
        title.text = "Comprar entrada para un amigo"
        title.setTextAppearance(R.style.NQ_AlertDialog_Title)
        title.setPadding(64, 48, 0, 0)

        builder.setCustomTitle(title)
        builder.setMessage("Tu amigo recibirá una notificación con la entrada que le has comprado a su applicación NQ.")
        builder.setPositiveButton("ENTENDIDO") { dialog, which ->

        }

        builder.show()
    }

    fun updateFriendsList() {
        if (FriendsRepository.friends.isNullOrEmpty()){
            eventScreenSearch_noFriendsLayout.visibility = View.VISIBLE
            eventScreenSearch_yesFriendsLayout.visibility = View.GONE
        } else {

            eventScreenSearch_firstRecyclerView.adapter = friendsAdapter
            eventScreenSearch_firstRecyclerView.layoutManager = LinearLayoutManager(this)

            eventScreenSearch_noFriendsLayout.visibility = View.GONE
            eventScreenSearch_yesFriendsLayout.visibility = View.VISIBLE
        }
    }

    override fun onItemClick(usersData: UsersData) {
        val intent = Intent()
        intent.putExtra("receivedData", usersData.name)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }
}