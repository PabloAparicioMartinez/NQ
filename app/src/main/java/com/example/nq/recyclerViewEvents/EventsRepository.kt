package com.example.nq.recyclerViewEvents

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

    val feverEvents = mutableListOf(
        EventsData(4, "Mayo",
            "JUEVES, 4 de Mayo", R.drawable.png_event_03,
            "LA TRINIDAD", "Rock",
            "7 €  |  Incluye 1 copa", "DISPONIBLES",
        ),
        EventsData(
            5, "Mayo",
            "VIERNES, 5 de Mayo", R.drawable.png_event_04,
            "SAUROM", "Folk metal",
            "22 €  |  Incluye 1 copa", "DISPONIBLES",
        )
    )
    val sonoraEvents = mutableListOf<EventsData>()

    fun returnEvents(discoName: String, datesList: List<DatesData>): List<EventsData> {

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
            "FEVER" -> {
                for (i in datesList.indices) {
                    for (j in 0 until feverEvents.size) {

                        if (datesList[i].dateMonth == feverEvents[j].eventDateMonth && datesList[i].dateNumber == feverEvents[j].eventDateNumber) {
                            eventsListToReturn.add(feverEvents[j])
                        }
                    }
                }
            }
            "SONORA" -> { }

            else -> println("NO DISCO NAME")
        }

        return eventsListToReturn

        //Mini
    }
}