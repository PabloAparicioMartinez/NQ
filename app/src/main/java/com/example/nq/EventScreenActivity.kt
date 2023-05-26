package com.example.nq

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
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
import kotlinx.android.synthetic.main.activity_event_screen.*
import kotlinx.android.synthetic.main.activity_event_screen.eventScreen_buyButton
import kotlinx.android.synthetic.main.activity_event_screen.eventScreen_date
import kotlinx.android.synthetic.main.activity_event_screen.eventScreen_image
import kotlinx.android.synthetic.main.activity_event_screen.eventScreen_minus
import kotlinx.android.synthetic.main.activity_event_screen.eventScreen_music
import kotlinx.android.synthetic.main.activity_event_screen.eventScreen_name
import kotlinx.android.synthetic.main.activity_event_screen.eventScreen_number
import kotlinx.android.synthetic.main.activity_event_screen.eventScreen_plus
import kotlinx.android.synthetic.main.activity_event_screen.eventScreen_price
import kotlinx.android.synthetic.main.item_ticket_info_name_yes.view.*
import kotlinx.android.synthetic.main.item_ticket_info_name_no.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlin.random.Random

class EventScreenActivity : AppCompatActivity() {

    var ticketCount = 1
    var ticketPrice = 12
    var moneyCount = 12

    lateinit var extraTicketsLayout: LinearLayout
    lateinit var extraTicketsInflater: LayoutInflater
    lateinit var extraTicketLayoutToAdd: View

    var indexOfSelected: Int = 0

    private var datesInstance = TicketDates()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_screen)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.title = ""
            actionBar.setDisplayHomeAsUpEnabled(true)
        }

        val ticketsDisponibility = intent.getStringExtra("EXTRA_AVAILABILITY")
        setTheLayout(ticketsDisponibility)

        val eventScreenMyName = "${FirebaseRepository.userName.uppercase()} ${FirebaseRepository.userSurnames.uppercase()} (YO)"
        if (FirebaseManager().checkIfUserIsSignedIn()) eventScreen_ticketName.text = eventScreenMyName

        eventScreen_plus.setOnClickListener { changeTicketCount("Plus") }
        eventScreen_minus.setOnClickListener { changeTicketCount("Minus") }

        extraTicketsLayout = findViewById(R.id.eventScreen_extraTicketsLayout)
        extraTicketsInflater = LayoutInflater.from(this)
        extraTicketLayoutToAdd = extraTicketsInflater.inflate(R.layout.item_ticket_info_name_no, null)

        eventScreen_buyButton.setOnClickListener {
            // Comprobar si esta loggeado o no:
            // Si no está loggeado, debe hacerlo primero.
            if (!FirebaseManager().checkIfUserIsSignedIn()) {
                Toast.makeText(this@EventScreenActivity, "¡Para comprar entradas debes iniciar sesión primero!", Toast.LENGTH_SHORT).show()
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
                    val intent = Intent(this@EventScreenActivity, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                    // Mostar toast
                    Toast.makeText(this@EventScreenActivity, "¡Entrada comprada con éxito!", Toast.LENGTH_SHORT).show()
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

    fun setTheLayout(ticketsDisponibility: String?) {
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

    fun changeTicketCount(countType: String) {
        when (countType){
            "Plus" -> {
                if (ticketCount < 9){
                    ticketCount++
                    moneyCount = ticketPrice * ticketCount

                    eventScreen_number.text = ticketCount.toString()
                    eventScreen_price.text = "$moneyCount,00 €"

                    extraTicketLayoutToAdd = extraTicketsInflater.inflate(R.layout.item_ticket_info_name_no, null)
                    extraTicketsLayout.addView(extraTicketLayoutToAdd)

                    extraTicketLayoutToAdd.itemTicketNoInfo_text.text = "Entrada $ticketCount"
                    extraTicketLayoutToAdd.itemTicketNoInfo_button.setOnClickListener() {

                        val intent = Intent(this, EventScreenActivitySearch::class.java)
                        friendsSearchLauncherV1.launch(intent)
                    }
                }
            }
            "Minus" -> {
                if (ticketCount > 1){
                    ticketCount--
                    moneyCount = ticketPrice * ticketCount

                    eventScreen_number.text = ticketCount.toString()
                    eventScreen_price.text = "$moneyCount,00 €"

                    extraTicketLayoutToAdd = extraTicketsInflater.inflate(R.layout.item_ticket_info_name_no, null)
                    extraTicketsLayout.removeViewAt(extraTicketsLayout.size - 1)
                }
            }
        }

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

    private val friendsSearchLauncherV1: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val receivedData = result.data?.getStringExtra("receivedData")

            extraTicketsLayout.removeViewAt(extraTicketsLayout.size - 1)

            extraTicketLayoutToAdd = extraTicketsInflater.inflate(R.layout.item_ticket_info_name_yes, null)
            extraTicketsLayout.addView(extraTicketLayoutToAdd)

            extraTicketLayoutToAdd.itemTicketYesInfo_text.text = "Entrada $ticketCount"
            extraTicketLayoutToAdd.itemTicketYesInfo_name.text = receivedData
            extraTicketLayoutToAdd.itemTicketYesInfo_button.setOnClickListener() {
                val intent = Intent(this, EventScreenActivitySearch::class.java)
                friendsSearchLauncherV2.launch(intent)
            }
        } else {
            // If (result.resultCode == Activity.RESULT_CANCELED)
        }
    }

    private val friendsSearchLauncherV2: ActivityResultLauncher<Intent> = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val receivedData = result.data?.getStringExtra("receivedData")

            extraTicketsLayout.removeViewAt(extraTicketsLayout.size - 1)

            extraTicketLayoutToAdd = extraTicketsInflater.inflate(R.layout.item_ticket_info_name_yes, null)
            extraTicketsLayout.addView(extraTicketLayoutToAdd)

            extraTicketLayoutToAdd.itemTicketYesInfo_text.text = "Entrada $ticketCount"
            extraTicketLayoutToAdd.itemTicketYesInfo_name.text = receivedData
            extraTicketLayoutToAdd.itemTicketYesInfo_button.setOnClickListener() {
                val intent = Intent(this, EventScreenActivitySearch::class.java)
                friendsSearchLauncherV1.launch(intent)
            }
        } else {
            // If (result.resultCode == Activity.RESULT_CANCELED)
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