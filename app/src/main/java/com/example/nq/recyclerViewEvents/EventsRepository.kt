package com.example.nq.recyclerViewEvents

import com.example.nq.R
import com.example.nq.recyclerViewDates.DatesData

object EventsRepository {

    val backStageEvents = mutableListOf(
        EventsData(
            "06/07/2023",
            R.drawable.png_event_01,
            "SAFARI  +18",
            "Reggeaton & Latina",
            14.00f,
            "Incluye 2 copas",
            100,
            "DISPONIBLES",
        ),
        EventsData(
            "07/07/2023",
            R.drawable.png_event_02,
            "EUPHORIA  +24",
            "Comercial & Reggeaton",
            12.50f,
            "Incluye 1 copa",
            100,
            "AGOTADAS",
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
            100,
            "DISPONIBLES",
        ),
        EventsData(
            "07/07/2023",
            R.drawable.png_event_04,
            "SAUROM",
            "Folk metal",
            22.00f,
            "Incluye 1 copa",
            100,
            "DISPONIBLES",
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