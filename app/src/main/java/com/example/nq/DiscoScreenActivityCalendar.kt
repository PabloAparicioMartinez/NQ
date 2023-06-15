package com.example.nq

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.nq.recyclerViewDates.DatesData
import com.example.nq.recyclerViewDates.DatesRepository
import com.example.nq.recyclerViewEvents.EventsAdapter
import com.example.nq.recyclerViewEvents.EventsData
import com.example.nq.recyclerViewEvents.EventsInterface
import com.example.nq.recyclerViewEvents.EventsRepository
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.activity_disco_screen_calendar.*
import kotlinx.android.synthetic.main.activity_disco_screen_calendar.discoScreen_DiscoName
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

class DiscoScreenActivityCalendar : AppCompatActivity(), EventsInterface {

    lateinit var eventsAdapter : EventsAdapter
    var selectedDatesList = mutableListOf<DatesData>()
    lateinit var discoName: String

    var selectedDate: Long? = null
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

            if (selectedDate == null) selectedDate = MaterialDatePicker.todayInUtcMilliseconds()

            val datePicker =
                MaterialDatePicker.Builder
                    .datePicker()
                    .setTitleText("Elige una fecha")
                    .setSelection(selectedDate)
                    .build()

            datePicker.addOnPositiveButtonClickListener { selection ->

                selectedDate = selection

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

        discoScreenCalendar_linearLayout01.setOnClickListener() {
            showLargeImage()
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

    fun showLargeImage() {

        val builder = MaterialAlertDialogBuilder(this, R.style.NQ_AlertDialogs)
        val customLayout = LayoutInflater.from(this).inflate(R.layout.item_image_disco, null)

        val discoImage = customLayout.findViewById<ImageView>((R.id.itemImageDisco_image))
        discoImage.setImageResource(intent.getIntExtra("EXTRA_IMAGE", -1))

        builder.setView(customLayout)
        val alertDialog = builder.create()
        alertDialog.show()
    }
}