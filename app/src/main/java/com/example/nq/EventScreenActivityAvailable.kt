package com.example.nq

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.view.size
import androidx.lifecycle.lifecycleScope
import com.example.nq.firebase.FirebaseManager
import com.example.nq.firebase.FirebaseRepository
import com.example.nq.recyclerViewTickets.TicketData
import com.example.nq.recyclerViewTickets.TicketDates
import com.example.nq.recyclerViewTickets.TicketsRepository
import com.google.firebase.firestore.AggregateSource
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_event_screen_available.*
import kotlinx.android.synthetic.main.item_ticket_info.view.*
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import kotlin.random.Random

class EventScreenActivityAvailable : AppCompatActivity() {

    var ticketCount = 1
    var ticketPrice = 12
    var moneyCount = 12

    lateinit var extraTicketsLayout: LinearLayout
    lateinit var extraTicketsInflater: LayoutInflater
    lateinit var extraTicketLayoutToAdd: View

    private var datesInstance = TicketDates()

    // lateinit var mainActivityBotMenuInflater: LayoutInflater
    // lateinit var mainActivityBotMenuLayoutInflated: View
    // private lateinit var mainActivityBotMenu: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_screen_available)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.title = ""
            actionBar.setDisplayHomeAsUpEnabled(true)
        }

        eventScreen_image.setImageResource(intent.getIntExtra("EXTRA_IMAGE", -1))
        eventScreen_name.text = intent.getStringExtra("EXTRA_NAME")
        eventScreen_music.text = intent.getStringExtra("EXTRA_MUSIC")
        eventScreen_date.text = intent.getStringExtra("EXTRA_DATE")

        val eventScreenMyName = "${FirebaseRepository.userName.uppercase()} ${FirebaseRepository.userSurnames.uppercase()} (YO)"
        if (FirebaseManager().checkIfUserIsSignedIn()) eventScreen_myName.text = eventScreenMyName

        eventScreen_plus.setOnClickListener { changeTicketCount("Plus") }
        eventScreen_minus.setOnClickListener { changeTicketCount("Minus") }

        extraTicketsLayout = findViewById(R.id.eventScreen_layout)
        extraTicketsInflater = LayoutInflater.from(this)
        extraTicketLayoutToAdd = extraTicketsInflater.inflate(R.layout.item_ticket_info, null)

        // mainActivityBotMenuInflater = LayoutInflater.from(this)
        // mainActivityBotMenuLayoutInflated = mainActivityBotMenuInflater.inflate(R.layout.activity_main, null)
        // mainActivityBotMenu = mainActivityBotMenuLayoutInflated.findViewById(R.id.mainActivity_botMenu)

        eventScreen_buyButton.setOnClickListener {
            // Comprobar si esta loggeado o no:
            // Si no está loggeado, debe hacerlo primero.
            if (!FirebaseManager().checkIfUserIsSignedIn()) {
                Toast.makeText(this@EventScreenActivityAvailable, "¡Para comprar entradas debes iniciar sesión primero!", Toast.LENGTH_SHORT).show()
            // Si está loggeado, se guardan las entradas en Firebase
            } else {
                // Retrieve user input
                val name: String  = "${FirebaseRepository.userName} ${FirebaseRepository.userSurnames}"
                val email: String = FirebaseRepository.userEmail
                val discoName: String = intent.getStringExtra("EXTRA_DISCONAME").toString()
                // Dates collection --> Mes y año actual
                val dateCollection: String = datesInstance.getCurrentMonthAndYear()
                // Instanciar la colección de Firebase ordenada por Discotecas
                val firestoreInstanceByDiscoName = Firebase.firestore
                    .collection("TicketsByDiscoName").document(discoName)
                    .collection(dateCollection)
                // Instanciar la colección de Firebase ordenada por Usuario
                val firestoreInstanceByEmail = Firebase.firestore
                    .collection("UserData").document(email)
                    .collection("TicketInfo")
                // Hay que crear una corrutina para poder llamar a algunas funciones de firebase
                lifecycleScope.launch {
                    // Get the tickets number
                    val ticketNumberByDisco = getTicketNumberByDisco(firestoreInstanceByDiscoName)
                    val date: String = intent.getStringExtra("EXTRA_DATE").toString()
                    val creationTimestamp = datesInstance.getCurrentTimestamp()
                    // Create the Ticket
                    val ticketData = TicketData(name,email,discoName,ticketNumberByDisco,date,creationTimestamp)
                    // Guardar la información en Firestore
                    saveTicketByDiscoName(ticketData, firestoreInstanceByDiscoName)
                    saveTicketByEmail(ticketData, firestoreInstanceByEmail)
                    // Cargar de nuevo la información de los tickets en el repositorio
                    buyTickets_loadingLayout.visibility = View.VISIBLE
                    // Delay de 3 segundos
                    delay(3000)
                    buyTickets_loadingLayout.visibility = View.GONE
                    // Actualizar el repositorio de tickets después de comprar la entrada
                    TicketsRepository.fetchTicketData(email)
                    // Finalizar las actividades y volver a la Main
                    val intent = Intent(this@EventScreenActivityAvailable, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                    // Mostar toast
                    Toast.makeText(this@EventScreenActivityAvailable, "¡Entrada comprada con éxito!", Toast.LENGTH_SHORT).show()
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

    private fun changeTicketCount(countType: String) {
        when (countType){
            "Plus" -> {
                if (ticketCount < 9){
                    ticketCount++
                    moneyCount = ticketPrice * ticketCount

                    eventScreen_number.text = ticketCount.toString()
                    eventScreen_price.text = "$moneyCount,00 €"

                    extraTicketLayoutToAdd = extraTicketsInflater.inflate(R.layout.item_ticket_info, null)
                    extraTicketsLayout.addView(extraTicketLayoutToAdd)
                    extraTicketLayoutToAdd.itemTicketInfo_text.text = "Entrada $ticketCount:"
                }
            }
            "Minus" -> {
                if (ticketCount > 1){
                    ticketCount--
                    moneyCount = ticketPrice * ticketCount

                    eventScreen_number.text = ticketCount.toString()
                    eventScreen_price.text = "$moneyCount,00 €"

                    extraTicketLayoutToAdd = extraTicketsInflater.inflate(R.layout.item_ticket_info, null)
                    extraTicketsLayout.removeViewAt(extraTicketsLayout.size - 1)
                    extraTicketLayoutToAdd.itemTicketInfo_text.text = "Entrada $ticketCount:"
                }
            }
        }

        if (ticketCount < 9) {
            eventScreen_plus.setBackgroundResource(R.drawable.shape_background_blue)
//            screenEvent_plus.foreground = ContextCompat.getDrawable(this, R.drawable.button_ripple_yes)
            eventScreen_plus.setColorFilter(Color.argb(255, 255, 255, 255))
        }
        else {
            eventScreen_plus.setBackgroundResource(R.drawable.shape_background_grey)
//            screenEvent_plus.foreground = null
            eventScreen_plus.setColorFilter(Color.argb(255, 121, 116, 126))
        }

        if (ticketCount > 1) {
            eventScreen_minus.setBackgroundResource(R.drawable.shape_background_blue)
//            screenEvent_minus.foreground = ContextCompat.getDrawable(this, R.drawable.button_ripple_yes)
            eventScreen_minus.setColorFilter(Color.argb(255, 255, 255, 255))
        }
        else {
            eventScreen_minus.setBackgroundResource(R.drawable.shape_background_grey)
//            screenEvent_minus.foreground = null
            eventScreen_minus.setColorFilter(Color.argb(255, 121, 116, 126))
        }
    }

    // Función para obtener el número de ticket que hay en la colección
    private suspend fun getTicketNumberByDisco(firestoreInstanceByDiscoName: Query) :String {
        val countQuery = firestoreInstanceByDiscoName.count()
        return try {
            countQuery.get(AggregateSource.SERVER).await().count.plus(1).toString()
        } catch (e: Exception) {
            Random.nextInt(10000, 100000001).toString() // Default value if retrieval fails
        }
    }


    // Función para cargar la info del Ticket en Firebase
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
            firestoreInstanceByEmail.document().set(ticketData).await()
        } catch (e: Exception) {
            throw  e
        }
    }
}