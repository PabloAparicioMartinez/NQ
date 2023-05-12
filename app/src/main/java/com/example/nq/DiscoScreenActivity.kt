package com.example.nq

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
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
import kotlinx.android.synthetic.main.activity_disco_screen.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class DiscoScreenActivity : AppCompatActivity(), DatesInterface, EventsInterface {

    val datesAdapter = DatesAdapter(DatesRepository.dates, this)
    lateinit var eventsAdapter : EventsAdapter
    var selectedDatesList = mutableListOf<DatesData>()
    lateinit var discoName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_disco_screen)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.title = ""
            actionBar.setDisplayHomeAsUpEnabled(true)
        }

        discoName = intent.getStringExtra("EXTRA_NAME").toString()
        discoScreen_DiscoName.text = discoName
        discoScreen_discoImage.setImageResource(intent.getIntExtra("EXTRA_IMAGE", -1))
        discoScreen_discoLocation.text = intent.getStringExtra("EXTRA_LOCATION")
        discoScreen_discoDistance.text = intent.getStringExtra("EXTRA_DISTANCE")

        screenDisco_datesRecyclerView.adapter = datesAdapter
        screenDisco_datesRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        when (intent.getStringExtra("EXTRA_NAME")) {
            "BACK&STAGE" -> eventsAdapter = EventsAdapter(EventsRepository.backStageEvents, this)
            "FEVER" -> eventsAdapter = EventsAdapter(EventsRepository.feverEvents, this)
            "SONORA" -> eventsAdapter = EventsAdapter(EventsRepository.sonoraEvents, this)
        }

        screenDisco_eventsRecyclerView.adapter = eventsAdapter
        screenDisco_eventsRecyclerView.layoutManager = LinearLayoutManager(this)

        lifecycleScope.launch {
            loadEventsInDate(selectedDatesList)
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
            linearLayout.setBackgroundResource(R.color.mid_blue)
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

        delay(1000)

        val filteredEvents: List<EventsData>
        if (datesList.isEmpty()) filteredEvents = EventsRepository.ReturnEvents(discoName, DatesRepository.dates)
        else filteredEvents = EventsRepository.ReturnEvents(discoName, datesList)
        eventsAdapter.setFilteredList(filteredEvents)

        if (filteredEvents.isNotEmpty()) setLayoutsVisibilities(listOf(View.GONE, View.VISIBLE, View.GONE))
        else setLayoutsVisibilities(listOf(View.GONE, View.VISIBLE, View.VISIBLE))

        datesAdapter.EnableClicking(true)
    }

    fun setLayoutsVisibilities (layoutVisibility: List<Int>){
        screenDisco_loadingLayout.visibility = layoutVisibility[0]
        screenDisco_eventsRecyclerView.visibility = layoutVisibility[1]
        screenDisco_noResultsLayout.visibility = layoutVisibility[2]
    }

    override fun onItemClick(eventData: EventsData) {
        when (eventData.eventAvailability){
            "DISPONIBLES" -> {
                Intent(this, EventScreenActivityAvailable::class.java).also {
                    it.putExtra("EXTRA_IMAGE", eventData.eventImage)
                    it.putExtra("EXTRA_NAME", eventData.eventName)
                    it.putExtra("EXTRA_MUSIC", eventData.eventMusic)
                    it.putExtra("EXTRA_DATE", eventData.eventDate)
                    startActivity(it)
                }
            }
            "AGOTADAS" -> {
                Intent(this, EventScreenActivityNotAvailable::class.java).also {
                    it.putExtra("EXTRA_IMAGE", eventData.eventImage)
                    it.putExtra("EXTRA_NAME", eventData.eventName)
                    it.putExtra("EXTRA_MUSIC", eventData.eventMusic)
                    it.putExtra("EXTRA_DATE", eventData.eventDate)
                    startActivity(it)
                }
            }
        }
    }
}