package com.example.nq.recyclerViewDates

import android.widget.LinearLayout
import android.widget.TextView

interface DatesInterface {
    fun onItemClick(datesData: DatesData, linearLayout: LinearLayout, textViews: List<TextView>)
}