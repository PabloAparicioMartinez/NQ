package com.example.nq.recyclerViewEvents

import com.example.nq.R
import com.example.nq.recyclerViewDates.DatesData
import com.example.nq.recyclerViewTickets.TicketDates

object EventsRepository {

    val backStageEvents = mutableListOf(
        EventsData(
            "06/07/2023",
            R.drawable.png_event_01,
            "SAFARI",
            "Reggeaton & Latina",
            14.50f,
            "Incluye 2 copas",
            20
        ),
        EventsData(
            "07/07/2023",
            R.drawable.png_event_02,
            "EUPHORIA",
            "Comercial & Reggeaton",
            12.50f,
            "Incluye 1 copa",
            0
        ),
        EventsData(
            "08/07/2023",
            R.drawable.png_event_05,
            "CIRCUS",
            "Comercial & Reggeaton",
            15.00f,
            "Incluye 1 copa",
            100
        )
    )

    val feverEvents = mutableListOf(
        EventsData(
            "06/07/2023",
            R.drawable.png_event_03,
            "LA TRINIDAD",
            "Rock",
            7.00f,
            "Incluye 1 copa",
            10
        ),
        EventsData(
            "07/07/2023",
            R.drawable.png_event_04,
            "SAUROM",
            "Folk metal",
            22.00f,
            "Incluye 1 copa",
            100
        )
    )

    val sonoraEvents = mutableListOf<EventsData>()

    private val ticketDatesInstance = TicketDates()

    fun returnEvents(discoName: String, datesList: List<DatesData>): List<EventsData> {

        val eventsListToReturn = mutableListOf<EventsData>()

        when(discoName){
            "BACK&STAGE" -> {
                for (i in datesList.indices) {
                    for (j in 0 until backStageEvents.size) {
                        val eventDateMonth = ticketDatesInstance.getMonthFromDate(backStageEvents[j].eventDate)
                        val eventDateDay = ticketDatesInstance.getDayFromDate(backStageEvents[j].eventDate)

                        if (datesList[i].dateMonth == eventDateMonth && datesList[i].dateNumber == eventDateDay) {
                            eventsListToReturn.add(backStageEvents[j])
                        }
                    }
                }
            }

            "FEVER" -> {
                for (i in datesList.indices) {
                    for (j in 0 until feverEvents.size) {
                        val eventDateMonth = ticketDatesInstance.getMonthFromDate(feverEvents[j].eventDate)
                        val eventDateDay = ticketDatesInstance.getDayFromDate(feverEvents[j].eventDate)

                        if (datesList[i].dateMonth == eventDateMonth && datesList[i].dateNumber == eventDateDay) {
                            eventsListToReturn.add(feverEvents[j])
                        }
                    }
                }
            }

            "SONORA" -> { }

            else -> println("NO DISCO NAME")
        }

        return eventsListToReturn
    }

}