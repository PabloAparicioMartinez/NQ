package com.example.nq.recyclerViewEvents

import java.util.*

data class EventsData(
    var eventDate: String,
    var eventImage: Int,
    var eventName: String,
    var eventMusic: String,
    var eventPrice: Float,
    var eventIncludedWithTicket: String,
    var eventTicketNumber: Int,
    var eventAvailability: String
)