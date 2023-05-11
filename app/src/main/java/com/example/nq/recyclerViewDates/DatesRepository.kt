package com.example.nq.recyclerViewDates

object DatesRepository {

    val dates = mutableListOf(
        DatesData("JUEVES", 4, "Mayo", false),
        DatesData("VIERNES", 5, "Mayo", false),
        DatesData("SÁBADO", 6, "Mayo", false),

        DatesData("JUEVES", 11, "Mayo", false),
        DatesData("VIERNES", 12, "Mayo", false),
        DatesData("SÁBADO", 13, "Mayo", false),

        DatesData("JUEVES", 18, "Mayo", false),
        DatesData("VIERNES", 19, "Mayo", false),
        DatesData("SÁBADO", 20, "Mayo", false),
    )

    fun ReturnSelectedDates() : MutableList<DatesData> {

        val datesListToReturn = mutableListOf<DatesData>()

        for (i in dates.indices) {
            if (dates[i].clicked) datesListToReturn.add(dates[i])
        }

        return datesListToReturn
    }
}