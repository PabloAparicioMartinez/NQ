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
import com.example.nq.firebase.FirebaseFriendsRepository
import com.example.nq.firebase.FirebaseRepository
import com.example.nq.firebase.FirebaseUserData
import com.example.nq.recyclerViewFriendsList.*
import com.example.nq.recyclerViewFriendsTickets.FriendsTicketsData
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_profile_friends.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.FieldPosition

class ProfileActivityFriends : AppCompatActivity(), FriendsListProfileInterface {

    private val friendsListProfileAdapter = FirebaseFriendsRepository.userFriends
    private val friendsProfileAdapter = FriendsListProfileAdapter(friendsListProfileAdapter, this)

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
                showAddFriendAlertDialog()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun updateFriendsList() {
        if (FirebaseFriendsRepository.userFriends.isNullOrEmpty()){
            profileFriends_noFriendsLayout.visibility = View.VISIBLE
            profileFriends_yesFriendsLayout.visibility = View.GONE

        } else {
            profileFriends_recyclerView.adapter = friendsProfileAdapter
            profileFriends_recyclerView.layoutManager = LinearLayoutManager(this)

            profileFriends_noFriendsLayout.visibility = View.GONE
            profileFriends_yesFriendsLayout.visibility = View.VISIBLE
        }
    }

    private fun showAddFriendAlertDialog() {

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
                    val addFriendMailLayout = customLayout.findViewById<TextInputLayout>(R.id.addFriend_emailLayout)

                    if (isValidEmailFormat(addFriendMailText)) {
                        addFriendMailLayout.error = null

                        lifecycleScope.launch {

                            val addFriendLayout =
                                customLayout.findViewById<LinearLayout>(R.id.addFriend_addLayout)
                            val addFriendLoading =
                                customLayout.findViewById<LinearLayout>(R.id.addFriend_loadingLayout)
                            addFriendLayout.visibility = View.GONE
                            addFriendLoading.visibility = View.VISIBLE

                            delay(1000)

                            val exists = checkExistingUsers(addFriendMailText)
                            if (exists) {
                                // Email exists in the collection
                                launch {
                                    addFriend(addFriendMailText)

                                    delay(1000)

                                    addFriendLoading.visibility = View.GONE
                                    addFriendLayout.visibility = View.VISIBLE

                                    dialog.dismiss()
                                }

                            } else {
                                // Email does not exist in the collection
                                val addFriendMailLayout =
                                    customLayout.findViewById<TextInputLayout>(R.id.addFriend_emailLayout)
                                addFriendMailLayout.error = "El usuario introducido no existe"

                                addFriendLoading.visibility = View.GONE
                                addFriendLayout.visibility = View.VISIBLE
                            }
                        }
                    } else {
                        addFriendMailLayout.error = "Introduce una dirección de correo electrónico válida"
                    }
                } else {
                    val addFriendMailLayout = customLayout.findViewById<TextInputLayout>(R.id.addFriend_emailLayout)
                    addFriendMailLayout.error = "Introduce una dirección de correo electrónico"
                }
            }
        }

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "AÑADIR") { _, _ ->
            // This block will not be executed as the positive button click listener is overridden
        }

        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancelar") { dialog, _ ->
            dialog.dismiss()
        }

        alertDialog.show()
    }

    private suspend fun addFriend(newEmail: String) {

        val userEmail = FirebaseRepository.userEmail

        // Actualizar Firebase
        Firebase.firestore
        .collection("UserData").document(userEmail)
        .collection("UserInfo").document("Data")
        .update("friendEmails", FieldValue.arrayUnion(newEmail))
        .await()

        val emailList = Firebase.firestore
            .collection("UserData").document(userEmail)
            .collection("UserInfo").document("Data")
            .get()
            .await()
            .toObject<FirebaseUserData>()
            ?.friendEmails

        if (emailList != null) {

            // Actualizar el repositorio
            FirebaseFriendsRepository.fetchFriendsData(emailList)

            // Actualizar la vista
            friendsProfileAdapter.notifyDataSetChanged()

            Toast.makeText(this, "¡Amigo agregado a tu lista con éxito!", Toast.LENGTH_SHORT).show()
        }

        else Toast.makeText(this, "¡Algo ha salido mal! Por favor, vuelve a intentarlo", Toast.LENGTH_SHORT).show()

    }

    private suspend fun deleteFriend(deleteEmail: String) {

        val userEmail = FirebaseRepository.userEmail

        // Actualizar Firebase
        Firebase.firestore
            .collection("UserData").document(userEmail)
            .collection("UserInfo").document("Data")
            .update("friendEmails", FieldValue.arrayRemove(deleteEmail))
            .await()

        val emailList = Firebase.firestore
            .collection("UserData").document(userEmail)
            .collection("UserInfo").document("Data")
            .get()
            .await()
            .toObject<FirebaseUserData>()
            ?.friendEmails

        if (emailList != null) {

            // Actualizar el repositorio
            FirebaseFriendsRepository.fetchFriendsData(emailList)

            // Actualizar la vista
            friendsProfileAdapter.notifyDataSetChanged()

            Toast.makeText(this, "¡Amigo eliminado!", Toast.LENGTH_SHORT).show()
        }

        else Toast.makeText(this, "¡Algo ha salido mal! Por favor, vuelve a intentarlo", Toast.LENGTH_SHORT).show()
    }

    private suspend fun checkExistingUsers(email: String): Boolean {
        val result = FirebaseAuth.getInstance().fetchSignInMethodsForEmail(email).await()
        return result.signInMethods?.isNotEmpty() == true
    }

    private fun isValidEmailFormat(email: String): Boolean {
        val pattern = "^\\w+([\\.-]?\\w+)*@\\w+([\\.-]?\\w+)*(\\.\\w{2,3})+$"
        return email.matches(Regex(pattern))
    }

    override fun onDeleteClick(position: Int) {

        val clickedFriend = friendsListProfileAdapter[position]
        val deleteEmail = clickedFriend.email

        val builder = MaterialAlertDialogBuilder(this, R.style.NQ_AlertDialog_TicketCard)

        val title = TextView(this)
        title.text = "Eliminar amigo"
        title.setTextAppearance(R.style.NQ_AlertDialog_Title)
        title.setPadding(64, 48, 0, 0)
        builder.setCustomTitle(title)
        builder.setMessage("¿Estás seguro de que deseas eliminar a esta persona de tu lista de amigos?")

        val alertDialog = builder.create()

        alertDialog.setOnShowListener { dialog ->
            val positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)

            positiveButton.setOnClickListener {
                lifecycleScope.launch{
                    deleteFriend(deleteEmail)

                    dialog.dismiss()
                }
            }
        }

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Eliminar") { _, _ ->
            // This block will not be executed as the positive button click listener is overridden
        }

        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancelar") { dialog, _ ->
            dialog.dismiss()
        }

        alertDialog.show()
    }
}