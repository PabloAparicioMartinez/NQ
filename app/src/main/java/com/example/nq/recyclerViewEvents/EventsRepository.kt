package com.example.nq.recyclerViewEvents

import android.widget.Toast
import com.example.nq.R
import com.example.nq.recyclerViewDates.DatesData

object EventsRepository {

    val backStageEvents = mutableListOf(
        EventsData(4, "Mayo",
            "JUEVES, 4 de Mayo", R.drawable.png_event_01,
            "SAFARI  +18", "Reggeaton & Latina",
            "14 €  |  Incluye 2 cubatas", "DISPONIBLES",
        ),
        EventsData(
            5, "Mayo",
            "VIERNES, 5 de Mayo", R.drawable.png_event_02,
            "EUPHORIA  +24", "Comercial & Reggeaton",
            "14 €  |  Incluye 1 cubata", "AGOTADAS",
        )
    )

    val feverEvents = mutableListOf<EventsData>()
    val sonoraEvents = mutableListOf<EventsData>()

    fun ReturnEvents(discoName: String, datesList: List<DatesData>): List<EventsData> {

        println("Prueba pal Nija")

        val eventsListToReturn = mutableListOf<EventsData>()

        when(discoName){
            "BACK&STAGE" -> {
                for (i in datesList.indices) {
                    for (j in 0 until backStageEvents.size) {

                        if (datesList[i].dateMonth == backStageEvents[j].eventDateMonth && datesList[i].dateNumber == backStageEvents[j].eventDateNumber) {
                            eventsListToReturn.add(backStageEvents[j])
                        }
                    }
                }
            }
            "FEVER" -> { }
            "SONORA" -> { }

            else -> println("NO DISCO NAME")
        }

        return eventsListToReturn

        //Mini
    }
}