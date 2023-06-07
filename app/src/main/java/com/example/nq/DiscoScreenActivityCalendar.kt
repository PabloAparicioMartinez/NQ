package com.example.nq

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.nq.recyclerViewDates.DatesAdapter
import com.example.nq.recyclerViewDates.DatesData
import com.example.nq.recyclerViewDates.DatesInterface
import com.example.nq.recyclerViewDates.DatesRepository
import com.example.nq.recyclerViewEvents.EventsAdapter
import com.example.nq.recyclerViewEvents.EventsData
import com.example.nq.recyclerViewEvents.EventsInterface
import com.example.nq.recyclerViewEvents.EventsRepository
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.android.synthetic.main.activity_disco_screen_calendar.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

class DiscoScreenActivityCalendar : AppCompatActivity(), DatesInterface, EventsInterface {

    val datesAdapter = DatesAdapter(DatesRepository.dates, this)
    lateinit var eventsAdapter : EventsAdapter
    var selectedDatesList = mutableListOf<DatesData>()
    lateinit var discoName: String

    var selectedDay = 0
    var selectedMonth = 0
    var selectedYear = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_disco_screen_calendar)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.title = ""
            actionBar.setDisplayHomeAsUpEnabled(true)
        }

        discoName = intent.getStringExtra("EXTRA_NAME").toString()
        discoScreen_DiscoName.text = discoName
        discoScreenCalendar_discoImage.setImageResource(intent.getIntExtra("EXTRA_IMAGE", -1))
        discoScreenCalendar_discoLocation.text = intent.getStringExtra("EXTRA_LOCATION")

        // BUTTON FECHA
        discoScreenCalendar_calendar.setOnClickListener() {

            val datePicker =
                MaterialDatePicker.Builder
                    .datePicker()
                    .setTitleText("Elige una fecha")
                    .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                    .setTheme(R.style.ThemeOverlay_App_DatePicker)
                    .build()

            datePicker.addOnPositiveButtonClickListener { selection ->
                val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
                calendar.timeInMillis = selection

                selectedDay = calendar.get(Calendar.DAY_OF_MONTH)
                selectedMonth = calendar.get(Calendar.MONTH) + 1 // Months are zero-based, so add 1
                selectedYear = calendar.get(Calendar.YEAR)

                discoScreenCalendar_date.text = "Jun $selectedDay, 2023"
                discoScreenCancer_showAll.visibility = View.VISIBLE

                selectedDatesList = DatesRepository.ReturnSelectedDateCalender(selectedDay)
                lifecycleScope.launch {
                    loadEventsInDateCalendar(selectedDatesList, false)
                }
            }

            datePicker.addOnNegativeButtonClickListener {
                // Respond to negative button click.
            }

            datePicker.show(supportFragmentManager, "Tag")
        }

        // BUTTON TODAS
        discoScreenCancer_showAll.setOnClickListener() {
            discoScreenCalendar_date.text = "Todas las fechas"
            discoScreenCancer_showAll.visibility = View.GONE

            selectedDatesList = DatesRepository.ReturnSelectedDateCalender(0)
            lifecycleScope.launch {
                loadEventsInDateCalendar(selectedDatesList, true)
            }
        }

//        screenDiscoCalendar_datesRecyclerView.adapter = datesAdapter
//        screenDiscoCalendar_datesRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        when (intent.getStringExtra("EXTRA_NAME")) {
            "BACK&STAGE" -> eventsAdapter = EventsAdapter(EventsRepository.backStageEvents, this)
            "FEVER" -> eventsAdapter = EventsAdapter(EventsRepository.feverEvents, this)
            "SONORA" -> eventsAdapter = EventsAdapter(EventsRepository.sonoraEvents, this)
        }

        discoScreenCalendar_eventsRecyclerView.adapter = eventsAdapter
        discoScreenCalendar_eventsRecyclerView.layoutManager = LinearLayoutManager(this)

        lifecycleScope.launch {
            loadEventsInDateCalendar(selectedDatesList, true)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onItemClick(datesData: DatesData, linearLayout: LinearLayout, textViews: List<TextView>) {

        if (datesData.clicked) {
            linearLayout.setBackgroundResource(R.color.dark_blue)
            for (i in textViews.indices) {
                textViews[i].setTextColor(ContextCompat.getColor(this, R.color.white))
            }
        }
        else {
            linearLayout.setBackgroundResource(R.color.white)
            for (i in textViews.indices) {
                textViews[i].setTextColor(ContextCompat.getColor(this, R.color.black_01))
            }
        }

        selectedDatesList = DatesRepository.ReturnSelectedDates()
        lifecycleScope.launch {
            loadEventsInDate(selectedDatesList)
        }
    }

    private suspend fun loadEventsInDate(datesList: List<DatesData>) {

        datesAdapter.EnableClicking(false)

        setLayoutsVisibilities(listOf(View.VISIBLE, View.GONE, View.GONE))

        delay(500)

        val filteredEvents: List<EventsData>
        if (datesList.isEmpty()) filteredEvents = EventsRepository.ReturnEvents(discoName, DatesRepository.dates)
        else filteredEvents = EventsRepository.ReturnEvents(discoName, datesList)
        eventsAdapter.setFilteredList(filteredEvents)

        if (filteredEvents.isNotEmpty()) setLayoutsVisibilities(listOf(View.GONE, View.VISIBLE, View.GONE))
        else setLayoutsVisibilities(listOf(View.GONE, View.VISIBLE, View.VISIBLE))

        datesAdapter.EnableClicking(true)
    }

    private suspend fun loadEventsInDateCalendar(datesList: List<DatesData>, isAllDates: Boolean) {

        setLayoutsVisibilities(listOf(View.VISIBLE, View.GONE, View.GONE))

        delay(500)

        val filteredEvents: List<EventsData>
        if (!isAllDates){
            filteredEvents = EventsRepository.ReturnEvents(discoName, datesList)
            eventsAdapter.setFilteredList(filteredEvents)
        } else {
            if (datesList.isEmpty()) filteredEvents = EventsRepository.ReturnEvents(discoName, DatesRepository.dates)
            else filteredEvents = EventsRepository.ReturnEvents(discoName, datesList)
            eventsAdapter.setFilteredList(filteredEvents)
        }

        if (filteredEvents.isNotEmpty()) setLayoutsVisibilities(listOf(View.GONE, View.VISIBLE, View.GONE))
        else setLayoutsVisibilities(listOf(View.GONE, View.VISIBLE, View.VISIBLE))
    }

    fun setLayoutsVisibilities (layoutVisibility: List<Int>){
        discoScreenCalendar_loadingLayout.visibility = layoutVisibility[0]
        discoScreenCalendar_eventsRecyclerView.visibility = layoutVisibility[1]
        discoScreenCalendar_noResultsLayout.visibility = layoutVisibility[2]
    }

    override fun onItemClick(eventData: EventsData) {

        Intent(this, EventScreenActivity::class.java).also {
            it.putExtra("EXTRA_IMAGE", eventData.eventImage)
            it.putExtra("EXTRA_NAME", eventData.eventName)
            it.putExtra("EXTRA_MUSIC", eventData.eventMusic)
            it.putExtra("EXTRA_DATE", eventData.eventDate)
            it.putExtra("EXTRA_AVAILABILITY", eventData.eventAvailability)
            startActivity(it)
        }
    }
}