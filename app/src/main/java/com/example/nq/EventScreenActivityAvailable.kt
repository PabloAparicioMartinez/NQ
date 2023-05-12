package com.example.nq

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Layout
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.size
import kotlinx.android.synthetic.main.activity_event_screen_available.*
import kotlinx.android.synthetic.main.item_ticket_info.view.*

class EventScreenActivityAvailable : AppCompatActivity() {

    var ticketCount = 1
    var ticketPrice = 12
    var moneyCount = 12

    lateinit var extraTicketsLayout: LinearLayout
    lateinit var extraTicketsInflater: LayoutInflater
    lateinit var extraTicketLayoutToAdd: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_screen_available)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.title = ""
            actionBar.setDisplayHomeAsUpEnabled(true)
        }

        eventScreen_image.setImageResource(intent.getIntExtra("EXTRA_IMAGE", -1))
        eventScreen_name.text = intent.getStringExtra("EXTRA_NAME")
        eventScreen_music.text = intent.getStringExtra("EXTRA_MUSIC")
        eventScreen_date.text = intent.getStringExtra("EXTRA_DATE")

        eventScreen_plus.setOnClickListener { ChangeTicketCount("Plus") }
        eventScreen_minus.setOnClickListener { ChangeTicketCount("Minus") }

        extraTicketsLayout = findViewById(R.id.eventScreen_layout)
        extraTicketsInflater = LayoutInflater.from(this)
        extraTicketLayoutToAdd = extraTicketsInflater.inflate(R.layout.item_ticket_info, null)
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

    fun ChangeTicketCount(countType: String) {
        when (countType){
            "Plus" -> {
                if (ticketCount < 9){
                    ticketCount++
                    moneyCount = ticketPrice * ticketCount

                    eventScreen_number.text = ticketCount.toString()
                    eventScreen_price.text = "$moneyCount,00 €"

                    extraTicketLayoutToAdd = extraTicketsInflater.inflate(R.layout.item_ticket_info, null)
                    extraTicketsLayout.addView(extraTicketLayoutToAdd)
                    extraTicketLayoutToAdd.itemTicketInfo_text.text = "Entrada $ticketCount:"
                }
            }
            "Minus" -> {
                if (ticketCount > 1){
                    ticketCount--
                    moneyCount = ticketPrice * ticketCount

                    eventScreen_number.text = ticketCount.toString()
                    eventScreen_price.text = "$moneyCount,00 €"

                    extraTicketLayoutToAdd = extraTicketsInflater.inflate(R.layout.item_ticket_info, null)
                    extraTicketsLayout.removeViewAt(extraTicketsLayout.size - 1)
                    extraTicketLayoutToAdd.itemTicketInfo_text.text = "Entrada $ticketCount:"
                }
            }
        }

        if (ticketCount < 9) {
            eventScreen_plus.setBackgroundResource(R.drawable.shape_background_blue)
//            screenEvent_plus.foreground = ContextCompat.getDrawable(this, R.drawable.button_ripple_yes)
            eventScreen_plus.setColorFilter(Color.argb(255, 255, 255, 255))
        }
        else {
            eventScreen_plus.setBackgroundResource(R.drawable.shape_background_grey)
//            screenEvent_plus.foreground = null
            eventScreen_plus.setColorFilter(Color.argb(255, 121, 116, 126))
        }

        if (ticketCount > 1) {
            eventScreen_minus.setBackgroundResource(R.drawable.shape_background_blue)
//            screenEvent_minus.foreground = ContextCompat.getDrawable(this, R.drawable.button_ripple_yes)
            eventScreen_minus.setColorFilter(Color.argb(255, 255, 255, 255))
        }
        else {
            eventScreen_minus.setBackgroundResource(R.drawable.shape_background_grey)
//            screenEvent_minus.foreground = null
            eventScreen_minus.setColorFilter(Color.argb(255, 121, 116, 126))
        }
    }
}