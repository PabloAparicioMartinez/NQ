package com.example.nq.recyclerViewDates

import android.util.Log

object DatesRepository {

    val dates = mutableListOf(
        DatesData("JUEVES", 8, "Junio", false),
        DatesData("VIERNES", 9, "Junio", false),
        DatesData("SÁBADO", 10, "Junio", false),
    )

    fun ReturnSelectedDates() : MutableList<DatesData> {

        val datesListToReturn = mutableListOf<DatesData>()

        for (i in dates.indices) {
            if (dates[i].clicked) datesListToReturn.add(dates[i])
        }

        return datesListToReturn
    }

    fun ReturnSelectedDateCalender(date: Int) : MutableList<DatesData> {

        val datesListToReturn = mutableListOf<DatesData>()
        for (i in dates.indices) {
            if (dates[i].dateNumber == date) datesListToReturn.add(dates[i])
        }

        return datesListToReturn
    }
}