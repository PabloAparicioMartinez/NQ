package com.example.nq.recyclerViewEvents

import com.example.nq.R
import com.example.nq.recyclerViewDates.DatesData

object EventsRepository {

    val events = mutableListOf(
        EventsData(
            4, "Mayo",

            "JUEVES, 4 de Mayo", R.drawable.png_event_01,
            "SAFARI  +18",
            "Reggeaton & Latina",
            "14 €",
            "DISPONIBLES",
        ),
        EventsData(
            5, "Mayo",

            "VIERNES, 5 de Mayo",
            R.drawable.png_event_02,
            "EUPHORIA  +24",
            "Comercial & Reggeaton",
            "12 €",
            "AGOTADAS",
        )
    )

    fun ReturnEvents(datesList: List<DatesData>): List<EventsData> {

        val eventsListToReturn = mutableListOf<EventsData>()

        for (i in datesList.indices) {
            for (j in 0 until events.size) {

                if (datesList[i].dateMonth == events[j].eventDateMonth && datesList[i].dateNumber == events[j].eventDateNumber) {
                    eventsListToReturn.add(events[j])
                }
            }
        }

        return eventsListToReturn
    }
}