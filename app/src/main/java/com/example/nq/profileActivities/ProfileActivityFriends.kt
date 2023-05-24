package com.example.nq.profileActivities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.nq.R
import com.example.nq.recyclerViewFriends.*
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.activity_profile_friends.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ProfileActivityFriends : AppCompatActivity(), FriendsInterface {

    val friendsAdapter = FriendsAdapter(FriendsRepository.friends, this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_friends)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.title = ""
            actionBar.setDisplayHomeAsUpEnabled(true)
        }

        updateFriendsList()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.top_menu_profile_friends, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            android.R.id.home -> {
                onBackPressed()
                return true
            }
            R.id.topMenu_addFriend -> {
                showAddFriendAlertDialong()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun updateFriendsList() {
        if (FriendsRepository.friends.isNullOrEmpty()){
            profileFriends_noFriendsLayout.visibility = View.VISIBLE
            profileFriends_yesFriendsLayout.visibility = View.GONE
        } else {

            profileFriends_recyclerView.adapter = friendsAdapter
            profileFriends_recyclerView.layoutManager = LinearLayoutManager(this)

            profileFriends_noFriendsLayout.visibility = View.GONE
            profileFriends_yesFriendsLayout.visibility = View.VISIBLE
        }
    }

    fun showAddFriendAlertDialong() {

        val builder = MaterialAlertDialogBuilder(this, R.style.NQ_AlertDialog_TicketCard)
        val customLayout = LayoutInflater.from(this).inflate(R.layout.item_add_friend, null)

        val title = TextView(this)
        title.text = "Añadir amigo"
        title.setTextAppearance(R.style.NQ_AlertDialog_Title)
        title.setPadding(64, 48, 0, 0)
        builder.setCustomTitle(title)
        builder.setMessage("Agrega a un amigo introduciendo su correo electrónico")
        builder.setView(customLayout)

        val alertDialog = builder.create()

        alertDialog.setOnShowListener { dialog ->
            val positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
            positiveButton.setOnClickListener {

                val addFriendMailEditText = customLayout.findViewById<TextInputEditText>(R.id.addFriend_emailText)
                val addFriendMailText: String = addFriendMailEditText.text?.toString() ?: ""

                if (addFriendMailText.isNotEmpty()) {

                    lifecycleScope.launch {

                        val addFriendLayout = customLayout.findViewById<LinearLayout>(R.id.addFriend_addLayout)
                        val addFriendLoading = customLayout.findViewById<LinearLayout>(R.id.addFriend_loadingLayout)
                        addFriendLayout.visibility = View.GONE
                        addFriendLoading.visibility = View.VISIBLE

                        delay(1000)

                        for (i in UsersRepository.existingUsers.indices){
                            if (addFriendMailText == UsersRepository.existingUsers[i].mail){
                                addFriend(UsersRepository.existingUsers[i])
                                addFriendLayout.visibility = View.VISIBLE
                                addFriendLoading.visibility = View.GONE
                                dialog.dismiss()
                                break
                            }
                        }

                        val addFriendMailLayout = customLayout.findViewById<TextInputLayout>(R.id.addFriend_emailLayout)
                        addFriendMailLayout.error = "El usuario introducido no existe"

                        addFriendLayout.visibility = View.VISIBLE
                        addFriendLoading.visibility = View.GONE

                    }

                } else {
                    val addFriendMailLayout = customLayout.findViewById<TextInputLayout>(R.id.addFriend_emailLayout)
                    addFriendMailLayout.error = "Introduce una dirección de correo electrónico"
                }
            }
        }

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "AÑADIR") { _, _ ->

        }

        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancelar") { dialog, _ ->
            dialog.dismiss()
        }

        alertDialog.show()
    }

    fun addFriend(usersData: UsersData) {
        Toast.makeText(this, "¡Solicitud de amistad enviada!", Toast.LENGTH_SHORT).show()

        FriendsRepository.friends.add(usersData)
        updateFriendsList()
    }

    override fun onItemClick(usersData: UsersData) {
        Toast.makeText(this, "¡Clicked!", Toast.LENGTH_SHORT).show()
    }
}