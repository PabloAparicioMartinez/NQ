package com.example.nq.recyclerViewDates

object DatesRepository {

    val dates = mutableListOf(
        DatesData("JUEVES", 6, "Julio", false),
        DatesData("VIERNES", 7, "Julio", false),
        DatesData("SÁBADO", 8, "Julio", false),

        DatesData("JUEVES", 13, "Julio", false),
        DatesData("VIERNES", 14, "Julio", false),
        DatesData("SÁBADO", 15, "Julio", false),

        DatesData("JUEVES", 20, "Julio", false),
        DatesData("VIERNES", 21, "Julio", false),
        DatesData("SÁBADO", 22, "Julio", false),
    )

    fun returnSelectedDateCalender(date: Int) : MutableList<DatesData> {

        val datesListToReturn = mutableListOf<DatesData>()
        for (i in dates.indices) {
            if (dates[i].dateNumber == date) datesListToReturn.add(dates[i])
        }

        return datesListToReturn
    }
}