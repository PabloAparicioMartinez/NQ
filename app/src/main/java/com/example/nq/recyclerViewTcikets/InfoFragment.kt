package com.example.nq.recyclerViewTcikets

import androidx.fragment.app.Fragment
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class InfoFragment : Fragment(R.layout.fragment_info) {

    private lateinit var editTextName: EditText
    private lateinit var editTextEmail: EditText
    private lateinit var editTextDiscoName: EditText
    private lateinit var editTextDate: EditText
    private lateinit var editTextTicketNumber: EditText
    private lateinit var buttonGenerateQR: Button

    private var datesInstance = Fechas()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get references to views using findViewById()
        editTextName = view.findViewById(R.id.editTextName)
        editTextEmail = view.findViewById(R.id.editTextEmail)
        editTextDiscoName = view.findViewById(R.id.editTextDiscoName)
        editTextDate = view.findViewById(R.id.editTextDate)
        editTextTicketNumber = view.findViewById(R.id.editTextTicketNumber)
        buttonGenerateQR = view.findViewById(R.id.buttonGenerateQR)

        // Botón para enviar la info a Firebase
        buttonGenerateQR.setOnClickListener {
            // Retrieve user input
            val name: String = editTextName.text.toString()
            val email: String = editTextEmail.text.toString()
            val discoName: String = editTextDiscoName.text.toString()
            val ticketNumber: String = editTextTicketNumber.text.toString()
            val date: String = editTextDate.text.toString()
            val creationTimestamp = datesInstance.getCurrentTimestamp()
            // Create the Ticket
            val ticket = Ticket(name,email,discoName,ticketNumber,date, creationTimestamp)
            // Get a reference to the Firestore collection for the given discoName
            val personDataByDiscoName = Firebase.firestore.collection("TicketsByDiscoName").document(discoName)
            val personDataByEmail = Firebase.firestore.collection("TicketsByEmail").document("Email")
            // Call the function to save the ticket data to Firestore
            saveTicketByDiscoName(ticket, personDataByDiscoName)
            saveTicketByEmail(ticket, personDataByEmail)
        }
    }

    // Función para obtener el numero de ticket que hay en la colección
    private suspend fun getDocumentCount(query: Query): Int {
        val countQuery = query.count()
        return try {
            countQuery.get(AggregateSource.SERVER).await().count.toInt()
        } catch (e: Exception) {
            Log.e("TAG", "Error getting tickets: ${e.message}", e)
            throw e
        }
    }

    // Función para cargar la info del Ticket en Firebase
    // El path para cargar la info del ticket ordenado por discotecas será: Tickets/{discoName}/{mes-año}/{número del ticket}/{info del ticket}
    private fun saveTicketByDiscoName(ticket: Ticket, personQRData: DocumentReference)
    = CoroutineScope(Dispatchers.IO).launch {
        try {
            val dateColection: String = datesInstance.getCurrentMonthAndYear()
            val numeroTicket = getDocumentCount(personQRData.collection(dateColection))+1
            personQRData.collection(dateColection).document((numeroTicket).toString()).set(ticket).await()
            withContext(Dispatchers.Main) {
                Toast.makeText(requireActivity(), "Saved data!", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
          withContext(Dispatchers.Main) {
              Toast.makeText(requireActivity(),e.message, Toast.LENGTH_SHORT).show()
          }
        }
    }

    // El path para cargar la info del ticket ordenado por discotecas será: Tickets/{email}/{info del ticket}
    private fun saveTicketByEmail(ticket: Ticket, personQRData: DocumentReference)
            = CoroutineScope(Dispatchers.IO).launch {
        try {
            val emailColection: String = ticket.email
            val numeroTicket = getDocumentCount(personQRData.collection(emailColection))+1
            personQRData.collection(emailColection).document((numeroTicket).toString()).set(ticket).await()
        } catch (e: Exception) {
            throw  e
        }
    }
}