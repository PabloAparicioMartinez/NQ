package com.example.nq.recyclerViewTickets

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

object TicketsRepository {

    internal val ticketList = mutableListOf<TicketData>()

    // Function to fetch and save ticket data from Firebase
    internal suspend fun fetchTicketData(email: String) {
        try {
            val querySnapshot = Firebase.firestore
                .collection("UserData")
                .document(email)
                .collection("TicketInfo")
                .get()
                .await()

            ticketList.clear() // Clear the existing list before adding new data

            for (document in querySnapshot.documents) {
                val ticketData = document.toObject<TicketData>()
                if (ticketData != null) {
                    ticketList.add(ticketData)
                }
            }
        } catch (e: Exception) {
            throw e
        }
    }

}