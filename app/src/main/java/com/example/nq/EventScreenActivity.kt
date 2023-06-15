package com.example.nq

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.result.ActivityResultLauncher
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.size
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.nq.firebase.FirebaseFriendsRepository
import com.example.nq.firebase.FirebaseManager
import com.example.nq.firebase.FirebaseRepository
import com.example.nq.firebase.FirebaseUserData
import com.example.nq.recyclerViewFriendsTickets.FriendsTicketsAdapter
import com.example.nq.recyclerViewFriendsTickets.FriendsTicketsData
import com.example.nq.recyclerViewFriendsTickets.FriendsTicketsInterface
import com.example.nq.recyclerViewTickets.TicketData
import com.example.nq.recyclerViewTickets.TicketDates
import com.example.nq.recyclerViewTickets.TicketNumberMapRepository
import com.example.nq.recyclerViewTickets.TicketsRepository
import com.google.firebase.firestore.AggregateSource
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_event_screen.*
import kotlinx.android.synthetic.main.activity_event_screen.eventScreen_buyButton
import kotlinx.android.synthetic.main.activity_event_screen.eventScreen_date
import kotlinx.android.synthetic.main.activity_event_screen.eventScreen_image
import kotlinx.android.synthetic.main.activity_event_screen.eventScreen_minus
import kotlinx.android.synthetic.main.activity_event_screen.eventScreen_music
import kotlinx.android.synthetic.main.activity_event_screen.eventScreen_name
import kotlinx.android.synthetic.main.activity_event_screen.eventScreen_number
import kotlinx.android.synthetic.main.activity_event_screen.eventScreen_plus
import kotlinx.android.synthetic.main.item_ticket_for_friend.*
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import kotlin.random.Random

class EventScreenActivity : AppCompatActivity(), FriendsTicketsInterface {

    var ticketCount = 1
    var ticketPrice = 0.00f
    var moneyCount = 1.00f

    private val friendsTicketsAdapterList: ArrayList<FriendsTicketsData> = ArrayList()
    private val friendsTicketsAdapter = FriendsTicketsAdapter(friendsTicketsAdapterList,this)

    private val datesInstance = TicketDates()

    companion object {
        private const val REQUEST_FRIEND_SELECTION = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_screen)


        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.title = ""
            actionBar.setDisplayHomeAsUpEnabled(true)
        }

        // Establecer la pantalla de tickets en función de si hay entradas disponibles o no
        val ticketsDisponibility = intent.getStringExtra("EXTRA_AVAILABILITY")
        setTheLayout(ticketsDisponibility)

        // Obtener el valor de ticketPrice dentro de onCreate()
        ticketPrice = intent.getFloatExtra("EXTRA_PRICE", 0.00f)

        val eventScreenMyName = "${FirebaseRepository.userName.uppercase()} ${FirebaseRepository.userSurnames.uppercase()} (YO)"
        if (FirebaseManager().checkIfUserIsSignedIn()) eventScreen_ticketName.text = eventScreenMyName

        // BUTTON large image
        eventScreen_discoLayout.setOnClickListener() {
            showLargeImage()
        }
        // Acciones para los botnes de + y -
        if (FirebaseManager().checkIfUserIsSignedIn()) {
            eventScreen_plus.setOnClickListener { changeTicketCount("Plus") }
            eventScreen_minus.setOnClickListener { changeTicketCount("Minus") }
        } else {
            eventScreen_plus.setOnClickListener { Toast.makeText(this@EventScreenActivity, "¡Para añadir entradas debes iniciar sesión primero!", Toast.LENGTH_SHORT).show() }
            eventScreen_minus.setOnClickListener { Toast.makeText(this@EventScreenActivity, "¡Para añadir entradas debes iniciar sesión primero!", Toast.LENGTH_SHORT).show() }
        }

        eventScreen_totalPrice.text = "$ticketPrice €"
        eventScreen_ticketPrice.text = "Precio por Ticket: $ticketPrice €"

        screenEvent_friendsRecyclerView.adapter = friendsTicketsAdapter
        screenEvent_friendsRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        eventScreen_buyButton.setOnClickListener {
            // Comprobar si esta loggeado o no:
            // Si no está loggeado, debe hacerlo primero.
            if (!FirebaseManager().checkIfUserIsSignedIn()) {
                Toast.makeText(this@EventScreenActivity, "¡Para comprar entradas debes iniciar sesión primero!", Toast.LENGTH_SHORT).show()

                // Si está loggeado, se guardan las entradas en Firebase
            } else {
                // Obtener solo la lista de emails
                val countEmails: Int = friendsTicketsAdapterList.count { friendTicket ->
                    friendTicket.friendEmail != "email@gmail.com"
                }

                if (friendsTicketsAdapterList.size == countEmails) {
                    lifecycleScope.launch {
                        // Cargar de nuevo la información de los tickets en el repositorio
                        buyTickets_loadingLayout.visibility = View.VISIBLE

                        buyTickets()

                        // Delay de 3 segundos
                        delay(3000)

                        // Actualizar el repositorio de tickets después de comprar la entrada
                        TicketsRepository.fetchTicketData(FirebaseRepository.userEmail)
                        buyTickets_loadingLayout.visibility = View.GONE

                        // Finalizar las actividades y volver a la Main
                        val intent = Intent(this@EventScreenActivity, MainActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        finish()

                        // Mostar toast
                        if (countEmails > 0) Toast.makeText(this@EventScreenActivity, "¡Entradas compradas con éxito!", Toast.LENGTH_SHORT).show()
                        else Toast.makeText(this@EventScreenActivity, "¡Entrada comprada con éxito!", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@EventScreenActivity, "¡Por favor, rellena todos los campos!", Toast.LENGTH_SHORT).show()
                }
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

    // ************************************************************************
    //   BLOQUE DE CÓDIGO PARA SELECCIONAR EL LAYOUT: SI QUEDAN O NO ENTRADAS
    // ************************************************************************

    private fun setTheLayout(ticketsDisponibility: String?) {
        when (ticketsDisponibility){
            "DISPONIBLES" -> {
                eventScreen_yesLayout.visibility = View.VISIBLE
                eventScreen_noLayout.visibility = View.GONE

                eventScreen_image.setImageResource(intent.getIntExtra("EXTRA_IMAGE", -1))
                eventScreen_name.text = intent.getStringExtra("EXTRA_NAME")
                eventScreen_music.text = intent.getStringExtra("EXTRA_MUSIC")
                eventScreen_date.text = intent.getStringExtra("EXTRA_DATE")
            }
            "AGOTADAS" -> {
                eventScreen_noLayout.visibility = View.VISIBLE
                eventScreen_yesLayout.visibility = View.GONE

                eventScreenNot_image.setImageResource(intent.getIntExtra("EXTRA_IMAGE", -1))
                eventScreenNot_name.text = intent.getStringExtra("EXTRA_NAME")
                eventScreenNot_music.text = intent.getStringExtra("EXTRA_MUSIC")
                eventScreenNot_date.text = intent.getStringExtra("EXTRA_DATE")
            }
        }
    }

    // *********************************************************************
    //   BLOQUE DE CÓDIGO PARA GESTIONAR LAS ACCIONES DE LOS BOTONES + Y -
    // *********************************************************************

    private fun changeTicketCount(countType: String, removeLast:Boolean = true) {
        when (countType){
            "Plus" -> {
                if (ticketCount < 9) {
                    ticketCount++
                    moneyCount = ticketPrice * ticketCount

                    setButtonColor(ticketCount)

                    eventScreen_number.text = ticketCount.toString()
                    eventScreen_totalPrice.text = "$moneyCount €"

                    insertItem()
                }
            }

            "Minus" -> {
                if (ticketCount > 1) {
                    ticketCount--
                    moneyCount = ticketPrice * ticketCount

                    setButtonColor(ticketCount)

                    eventScreen_number.text = ticketCount.toString()
                    eventScreen_totalPrice.text = "$moneyCount €"

                    if (removeLast) removeItem(friendsTicketsAdapterList.lastIndex)
                }
            }
        }
    }

    // **********************************************************
    //   BLOQUE DE CÓDIGO PARA ADAPTAR EL COLOR DEL BOTÓN + Y -
    // **********************************************************

    private fun setButtonColor (ticketCount: Int) {

        if (ticketCount < 9) {
            eventScreen_plus.setBackgroundResource(R.drawable.shape_background_blue)
            eventScreen_plus.setColorFilter(Color.argb(255, 255, 255, 255))
        }
        else {
            eventScreen_plus.setBackgroundResource(R.drawable.shape_background_grey)
            eventScreen_plus.setColorFilter(Color.argb(255, 121, 116, 126))
        }

        if (ticketCount > 1) {
            eventScreen_minus.setBackgroundResource(R.drawable.shape_background_blue)
            eventScreen_minus.setColorFilter(Color.argb(255, 255, 255, 255))
        }
        else {
            eventScreen_minus.setBackgroundResource(R.drawable.shape_background_grey)
            eventScreen_minus.setColorFilter(Color.argb(255, 121, 116, 126))
        }
    }

    // ************************************************
    //   BLOQUE DE CÓDIGO PARA ADAPTAR LA RECYLERVIEW
    // ************************************************

    override fun onAddFriendClick(position: Int) {
        // Obtener solo la lista de emails
        val friendsEmailsList: List<String> = friendsTicketsAdapterList
            .map { friendTicket -> friendTicket.friendEmail }
            .ifEmpty { listOf() }

        // Iniciar la actividad para buscar un amigo
        val intent = Intent(this, EventScreenActivitySearch::class.java)
        intent.putExtra("POSITION", position)
        intent.putStringArrayListExtra("EMAIL_LIST", ArrayList(friendsEmailsList))

        resultLauncher.launch(intent)
    }

    override fun onDeleteFriendClick(position: Int) {
        changeTicketCount("Minus", false)
        removeItem(position)
    }

    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {

            val data: Intent? = result.data

            // Actualizar la info de item (amigo) seleccionado
            val selectedPosition: Int? = data?.getIntExtra("POSITION_BACK",0)
            val clickedItem = friendsTicketsAdapterList[selectedPosition!!]

            val selectedFriendEmail: String? = data?.getStringExtra("USER_EMAIL")
            val selectedFriendName: String? = data?.getStringExtra("USER_NAME")
            val selectedFriendSurname: String? = data?.getStringExtra("USER_SURNAMES")

            if (selectedFriendName != null) {
                clickedItem.friendName = selectedFriendName
            }
            if (selectedFriendSurname != null) {
                clickedItem.friendSurnames = selectedFriendSurname
            }
            if (selectedFriendEmail != null) {
                clickedItem.friendEmail = selectedFriendEmail
            }

            friendsTicketsAdapter.notifyItemChanged(selectedPosition)
        }
    }

    // Añadir view: View
    private fun insertItem () {
        val newItem = FriendsTicketsData()
        friendsTicketsAdapterList.add(newItem)
        val lastIndex = friendsTicketsAdapterList.lastIndex
        friendsTicketsAdapter.notifyItemInserted(lastIndex)
    }

    // Añadir view: View,
    private fun removeItem (index: Int) {
        friendsTicketsAdapterList.removeAt(index)
        friendsTicketsAdapter.notifyItemRemoved(index)

        // Update the numberTextView for the remaining items if the removed item is not the last one
        if (index < friendsTicketsAdapterList.size) {
            for (i in index until friendsTicketsAdapterList.size) {
                val viewHolder = screenEvent_friendsRecyclerView.findViewHolderForAdapterPosition(i) as? FriendsTicketsAdapter.FriendsTicketsViewHolder
                viewHolder?.let {
                    val numberToDisplay = "Entrada ${i + 2}"
                    it.numberTextView.text = numberToDisplay
                }
            }
        }
    }


    // ******************************************
    //  BLOQUE DE CÓDIGO PARA COMPRAR LA ENTRADA
    // ******************************************

    // Función para obtener el número de ticket que hay en la colección
    private suspend fun getTicketNumbersByDisco(discoName: String, firestoreInstanceByDiscoName: Query) :List<String> {
        val ticketNumbers = mutableListOf<String>()
        val countQuery = firestoreInstanceByDiscoName.count()
        val matchingFriendsCount = friendsTicketsAdapterList.size + 1 // Los que hay en la lista más la nuestra
        println(matchingFriendsCount)

        return try {
            val count = countQuery.get(AggregateSource.SERVER).await().count.plus(1)
            val preCount = TicketNumberMapRepository.returnTicketNumber(discoName)

            // ticket para cada usuario
            for (i in 0 until matchingFriendsCount) {
                val num = count+i
                val padCount = num.toString().padStart(6, '0')
                val ticketNumber = "$preCount$padCount"
                ticketNumbers.add(ticketNumber)
            }

            ticketNumbers

        } catch (e: Exception) {
            listOf(Random.nextInt(10000, 100001).toString()) // Default value if retrieval fails
        }
    }

    // Funciones para cargar la info del Ticket en Firebase
    // El path para cargar la info del ticket ordenado por discotecas será: Tickets/{discoName}/{mes-año}/{número del ticket}/{info del ticket}
    private fun saveTicketByDiscoName(ticketData: TicketData, firestoreInstanceByDisco: CollectionReference)
            = CoroutineScope(Dispatchers.IO).launch {
        try {
            firestoreInstanceByDisco.document(ticketData.ticketNumber).set(ticketData).await()
        } catch (e: Exception) {
            throw e
        }
    }
    // El path para cargar la info del ticket ordenado por emails será: Tickets/{email}/{info del ticket}
    private fun saveTicketByEmail(ticketData: TicketData, firestoreInstanceByEmail: CollectionReference)
            = CoroutineScope(Dispatchers.IO).launch {
        try {
            firestoreInstanceByEmail.document(ticketData.ticketNumber).set(ticketData).await()
        } catch (e: Exception) {
            throw  e
        }
    }

    // Función para compar la entrada
    private fun buyTickets() {
        // Info del evento
        val discoName: String = intent.getStringExtra("EXTRA_DISCO_NAME").toString()
        val eventName: String = intent.getStringExtra("EXTRA_NAME").toString()
        val eventDate: String = intent.getStringExtra("EXTRA_DATE").toString()

        // Info de los usuario a los que se va a comprar la entrada
        val matchingFriends = mutableListOf<FirebaseUserData>()

        val mySelf = FirebaseUserData(
            FirebaseRepository.userName,
            FirebaseRepository.userSurnames,
            FirebaseRepository.userImage.toString(),
            FirebaseRepository.userEmail,
            FirebaseRepository.userFriendEmails
        )

        // Añadirse a uno mismo
        matchingFriends.add(mySelf)

        // Obtener solo la lista de emails
        val friendsEmailsList: List<String> = friendsTicketsAdapterList
            .map { friendTicket -> friendTicket.friendEmail }
            .ifEmpty { listOf() }
        for (friendEmail in friendsEmailsList) {
            for (userFriend in FirebaseFriendsRepository.userFriends) {
                if (userFriend.email == friendEmail) {
                    matchingFriends.add(userFriend)
                    break
                }
            }
        }

        // Dates collection --> Mes y año actual
        val dateCollection: String = datesInstance.getCurrentMonthAndYear()
        // Instanciar la colección de Firebase ordenada por Discotecas
        // Esta solo hace falta instanciarla una vez
        val firestoreInstanceByDiscoName = Firebase.firestore
            .collection("TicketsByDiscoName").document(discoName)
            .collection(dateCollection)

        lifecycleScope.launch {
            // Get the ticket numbers
            val ticketNumbers = getTicketNumbersByDisco(discoName, firestoreInstanceByDiscoName)

            // Bucle para comprar entradas
            for ((buyer, ticketNumber) in matchingFriends.zip(ticketNumbers)) {
                // Instanciar la colección de Firebase ordenada por Usuario
                // Esta habrá que hacerla para cada persona a la que se compra una entrada
                val firestoreInstanceByEmail = Firebase.firestore
                    .collection("UserData").document(buyer.email)
                    .collection("TicketInfo")

                val creationTimestamp = datesInstance.getCurrentTimestamp()
                // Create the Ticket
                val ticketData = TicketData(
                    buyer.name,
                    buyer.surnames,
                    buyer.email,
                    discoName,
                    eventName,
                    ticketNumber,
                    eventDate,
                    creationTimestamp
                )

                // Guardar la información en Firestore
                saveTicketByDiscoName(ticketData, firestoreInstanceByDiscoName)
                saveTicketByEmail(ticketData, firestoreInstanceByEmail)
            }
        }
    }

    fun showLargeImage() {

        val builder = MaterialAlertDialogBuilder(this, R.style.NQ_AlertDialogs)
        val customLayout = LayoutInflater.from(this).inflate(R.layout.item_image_event, null)
        val eventImage = customLayout.findViewById<ImageView>((R.id.itemImageEvent_image))
        eventImage?.setImageResource(intent.getIntExtra("EXTRA_IMAGE", -1))

        builder.setView(customLayout)
        val alertDialog = builder.create()
        alertDialog.show()
    }
}