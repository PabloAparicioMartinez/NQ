package com.example.nq.recyclerViewTickets

object TicketNumberMapRepository {

    private val datesInstance = TicketDates()
    private var date = datesInstance.getCurrentMonthAndYearNumberFormat()

    fun returnTicketNumber(discoName: String): String {
        return when(discoName){
            "BACK&STAGE" -> "001$date"
            "FEVER" -> "002$date"
            "SONORA" -> "003$date"
            else -> "000$date"
        }
    }

}