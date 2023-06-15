package com.example.nq.recyclerViewTickets

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.example.nq.FragmentMyTickets
import com.example.nq.R
import com.example.nq.SetFragment
import com.example.nq.databinding.FragmentMyTicketsViewpagerBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await


class TicketsAdapter(
    private val tickets: List<TicketData>,
    private val fragmentManager: FragmentManager,
    private val mainActivityBotMenu: BottomNavigationView
    ) : RecyclerView.Adapter<TicketsAdapter.TicketsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TicketsViewHolder {
        val binding = FragmentMyTicketsViewpagerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TicketsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TicketsViewHolder, position: Int) {
        holder.bind(tickets[position])
    }

    override fun getItemCount(): Int {
        return tickets.size
    }

    inner class TicketsViewHolder(private val binding: FragmentMyTicketsViewpagerBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            // Vender la entrada cuando se da click en el botón
            binding.buttonSellTicket.setOnClickListener {
                showSellTicketCard()
            }
        }

        private fun showSellTicketCard() {

            val builder = MaterialAlertDialogBuilder(binding.root.context, R.style.NQ_AlertDialog_TicketCard)

            val title = TextView(binding.root.context)
            title.text = "Vender tu entrada"
            title.setTextAppearance(R.style.NQ_AlertDialog_Title)
            title.setPadding(64, 48, 0, 0)

            // Valores del ticket que tenemos en pantalla
            val ticket = tickets[absoluteAdapterPosition]
            val discoName = ticket.discoName
            val email = ticket.email
            val datesInstance = TicketDates()
            val dateCollection = datesInstance.getCurrentMonthAndYear()
            val ticketNumber = ticket.ticketNumber

            // Instanciar la colección de Firebase ordenada por Discotecas
            val firestoreInstanceByDiscoName = Firebase.firestore
                .collection("TicketsByDiscoName").document(discoName)
                .collection(dateCollection).document(ticketNumber)
            // Instanciar la colección de Firebase ordenada por Usuario
            val firestoreInstanceByEmail = Firebase.firestore
                .collection("UserData").document(email)
                .collection("TicketInfo").document(ticketNumber)
            // Instanciar la colección de Firebase/Historic/Cancelled
            val firestoreInstanceSaveToCancelled = Firebase.firestore
                .collection("UserData").document(email)
                .collection("Historic").document("Historic-Cancelled")
                .collection("Cancelled")

            val fragmentMyTickets = FragmentMyTickets()

            builder.setCustomTitle(title)
            builder.setMessage("¿Estás seguro de que deseas vender tu entrada? \n" +
                    "Si confirmas que deseas vender tu entrada,  se te devolverá el 80% del importe que hayas pagado " +
                    "por ella, y esta misma cantidad será devuelta a la discoteca")
            builder.setPositiveButton("Confirmar") { _, _ ->
                cancelTicketByDiscoName(firestoreInstanceByDiscoName)
                saveTicketToCancelled(ticket,firestoreInstanceSaveToCancelled)
                deleteTicketFromUserTickets(firestoreInstanceByEmail)
                MainScope().launch {
                    binding.sellTicketsLoadingLayout.visibility = View.VISIBLE
                    TicketsRepository.fetchTicketData(email)
                    // Delay de 1 segundo
                    delay(1500)
                    // Reload Fragment
                    SetFragment.setCurrentFragment(fragmentManager, fragmentMyTickets, mainActivityBotMenu)
                    // Delay de 1 segundo
                    delay(1500)
                    binding.sellTicketsLoadingLayout.visibility = View.GONE
                    // Show Toast
                    Toast.makeText(binding.root.context, "¡Entrada vendida con éxito!", Toast.LENGTH_SHORT).show()
                }
            }
            builder.setNegativeButton("Cancelar") { _, _ ->
            }
            builder.show()
        }

        // Funciones para cambiar la info del Ticket en Firebase
        private fun cancelTicketByDiscoName(firestoreInstanceByDiscoName: DocumentReference)
                = CoroutineScope(Dispatchers.IO).launch {
            try {
                firestoreInstanceByDiscoName.update("cancelled", true).await()
            } catch (e: Exception) {
                throw e
            }
        }
        // Funciones para quitar la info del Tickete en el Usuario de Firebase
        private fun deleteTicketFromUserTickets(firestoreInstanceByEmail: DocumentReference)
                = CoroutineScope(Dispatchers.IO).launch {
            try {
                firestoreInstanceByEmail.delete().await()
            } catch (e: Exception) {
                throw  e
            }
        }
        // Función para guardar la info del Ticket en Firebase / Histórico / Cancelados
        private fun saveTicketToCancelled(ticketData: TicketData, firestoreInstanceByDisco: CollectionReference)
                = CoroutineScope(Dispatchers.IO).launch {
            try {
                ticketData.cancelled = true
                firestoreInstanceByDisco.document().set(ticketData).await()
            } catch (e: Exception) {
                throw e
            }
        }

        fun bind(ticket: TicketData) {
            // Declaramos las varibales que vamos a usar
            val discoNameToShow: String = ticket.discoName
            val eventToShow: String = ticket.eventName
            val dateToShow: String = ticket.date
            val nameToShow: String = ticket.name.uppercase()
            val surnameToShow: String = ticket.surnames.uppercase()
            val emailToShow: String = ticket.email

            // Update UI
            binding.textViewDiscoteca.text = discoNameToShow
            binding.textViewEvento.text = eventToShow
            binding.textViewFecha.text = dateToShow
            binding.textViewNombre.text = "$nameToShow $surnameToShow"
            binding.textViewEmail.text = emailToShow

            // Generate QR code asynchronously using coroutines
            CoroutineScope(Dispatchers.Default).launch {
                // Encriptar los datos que se van a utilizar para generar el QR Bitmap
                val qrData: String = "$discoNameToShow-$eventToShow-$dateToShow-$nameToShow $surnameToShow-$emailToShow".replace(" ", "")
                val encryptionUtility = EncryptionUtility()
                val qrDataEncrypted: String = encryptionUtility.encrypt(qrData)
                // Llamar a la función para generar el QR Bitmap
                val bitmapInstance = GenerateBitmap()
                val qrBitmapEncrypted: Bitmap = bitmapInstance.generateBitmap(qrDataEncrypted)

                withContext(Dispatchers.Main) {
                    binding.imageViewQR.setImageBitmap(qrBitmapEncrypted)
                }
            }
        }
    }
}