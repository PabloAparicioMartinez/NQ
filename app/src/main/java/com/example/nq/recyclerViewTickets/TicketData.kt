package com.example.nq.recyclerViewTickets

data class TicketData(
    var name: String = "NOMBRE",
    var surnames: String = "APELLIDOS",
    var email: String = "EMAIL",
    var discoName: String = "DISCOTECA",
    var eventName: String = "EVENTO",
    var ticketType: String = "ENTRADA SIMPLE",
    var ticketNumber: String = "NUMERO DE TICKET",
    var date: String = "FECHA",
    var creationTimestamp: String = "FECHA DE COMPRA",
    var access: Boolean = true,
    var cancelled: Boolean = false
)
