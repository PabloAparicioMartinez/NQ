package com.example.nq.recyclerViewTcikets

import android.content.Context
import com.example.nq.Ticket
import com.google.common.reflect.TypeToken
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import kotlinx.coroutines.tasks.await

class CacheManager(context: Context) {

    private val _ticketCache = "TICKET_CACHE"
    private val _ticketData = "TICKET_DATA"

    private val sharedPreferences = context.getSharedPreferences(_ticketCache, Context.MODE_PRIVATE)

    private val firestoreInstance = Firebase.firestore

    private val gson = Gson()

    internal fun deleteSharedPreferences() {
        sharedPreferences.edit().clear().apply()
    }

    internal fun getFromCacheTicketData(): List<Ticket> {
        val ticketJson = sharedPreferences.getString(_ticketData, null)
        return if (ticketJson != null) {
            val type = object : TypeToken<List<Ticket>>() {}.type
            gson.fromJson(ticketJson, type)
        } else {
            emptyList()
        }
    }

    internal fun saveToCacheTicketData(data: List<Ticket>) {
        val editor = sharedPreferences.edit()
        editor.putString(_ticketData, gson.toJson(data))
        editor.apply()
    }

    // Función para recibir la info del Ticket en Firebase y guardarla en listas
    internal suspend fun fetchTicketData(): List<Ticket> {
        return try {
            val email = "igancionija96@gmail.com"
            val querySnapshot = firestoreInstance.collection("TicketsByEmail")
                .document("Email")
                .collection(email)
                .whereEqualTo("access", true)
                .get()
                .await()
            // Creamos la lista de tickets y la rellenamos en el bucle for
            val ticketList = mutableListOf<Ticket>()
            for (document in querySnapshot.documents) {
                val ticket = document.toObject<Ticket>()
                if (ticket != null) {
                    ticketList.add(ticket)
                }
            }
            ticketList
        } catch (e: Exception) {
            emptyList()
        }
    }

}