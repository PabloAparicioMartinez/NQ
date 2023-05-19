package com.example.nq

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.size
import com.example.nq.firebase.FirebaseRepository
import kotlinx.android.synthetic.main.activity_event_screen.*
import kotlinx.android.synthetic.main.item_ticket_info_name_yes.view.*
import kotlinx.android.synthetic.main.item_ticket_info_name_no.view.*

class EventScreenActivity : AppCompatActivity() {

    var ticketCount = 1
    var ticketPrice = 12
    var moneyCount = 12

    lateinit var extraTicketsLayout: LinearLayout
    lateinit var extraTicketsInflater: LayoutInflater
    lateinit var extraTicketLayoutToAdd: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_screen)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.title = ""
            actionBar.setDisplayHomeAsUpEnabled(true)
        }

        val ticketsDisponibility = intent.getStringExtra("EXTRA_AVAILABILITY")
        setTheLayout(ticketsDisponibility)

        eventScreen_plus.setOnClickListener { ChangeTicketCount("Plus") }
        eventScreen_minus.setOnClickListener { ChangeTicketCount("Minus") }

        extraTicketsLayout = findViewById(R.id.eventScreen_extraTicketsLayout)
        extraTicketsInflater = LayoutInflater.from(this)
        extraTicketLayoutToAdd = extraTicketsInflater.inflate(R.layout.item_ticket_info_name_no, null)

        eventScreen_ticketName.text = FirebaseRepository.userName
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

    fun setTheLayout(ticketsDisponibility: String?) {
        when (ticketsDisponibility){
            "DISPONIBLES" -> {
                eventScreen_yesLayout.visibility = View.VISIBLE
                eventScreen_noLayout.visibility = View.GONE

                eventScreen_image.setImageResource(intent.getIntExtra("EXTRA_IMAGE", -1))
                eventScreen_name.text = intent.getStringExtra("EXTRA_NAME")
                eventScreen_music.text = intent.getStringExtra("EXTRA_MUSIC")
                eventScreen_date.text = intent.getStringExtra("EXTRA_DATE")
            }
            "AGOTADAS" -> {
                eventScreen_noLayout.visibility = View.VISIBLE
                eventScreen_yesLayout.visibility = View.GONE

                eventScreenNot_image.setImageResource(intent.getIntExtra("EXTRA_IMAGE", -1))
                eventScreenNot_name.text = intent.getStringExtra("EXTRA_NAME")
                eventScreenNot_music.text = intent.getStringExtra("EXTRA_MUSIC")
                eventScreenNot_date.text = intent.getStringExtra("EXTRA_DATE")
            }
        }
    }

    fun ChangeTicketCount(countType: String) {
        when (countType){
            "Plus" -> {
                if (ticketCount < 9){
                    ticketCount++
                    moneyCount = ticketPrice * ticketCount

                    eventScreen_number.text = ticketCount.toString()
                    eventScreen_price.text = "$moneyCount,00 €"

                    extraTicketLayoutToAdd = extraTicketsInflater.inflate(R.layout.item_ticket_info_name_no, null)
                    extraTicketsLayout.addView(extraTicketLayoutToAdd)
                    extraTicketLayoutToAdd.itemTicketNoInfo_text.text = "Entrada $ticketCount:"
                    extraTicketLayoutToAdd.itemTicketNoInfo_button.setOnClickListener() {
                        val intent = Intent(this, EventScreenActivitySearch::class.java)
                        friendsSearchLauncherV1.launch(intent)
                    }
                }
            }
            "Minus" -> {
                if (ticketCount > 1){
                    ticketCount--
                    moneyCount = ticketPrice * ticketCount

                    eventScreen_number.text = ticketCount.toString()
                    eventScreen_price.text = "$moneyCount,00 €"

                    extraTicketLayoutToAdd = extraTicketsInflater.inflate(R.layout.item_ticket_info_name_no, null)
                    extraTicketsLayout.removeViewAt(extraTicketsLayout.size - 1)
                }
            }
        }

        if (ticketCount < 9) {
            eventScreen_plus.setBackgroundResource(R.drawable.shape_background_blue)
            eventScreen_plus.setColorFilter(Color.argb(255, 255, 255, 255))
        }
        else {
            eventScreen_plus.setBackgroundResource(R.drawable.shape_background_grey)
            eventScreen_plus.setColorFilter(Color.argb(255, 121, 116, 126))
        }

        if (ticketCount > 1) {
            eventScreen_minus.setBackgroundResource(R.drawable.shape_background_blue)
            eventScreen_minus.setColorFilter(Color.argb(255, 255, 255, 255))
        }
        else {
            eventScreen_minus.setBackgroundResource(R.drawable.shape_background_grey)
            eventScreen_minus.setColorFilter(Color.argb(255, 121, 116, 126))
        }
    }

    private val friendsSearchLauncherV1: ActivityResultLauncher<Intent> = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val receivedData = result.data?.getStringExtra("receivedData")

            extraTicketLayoutToAdd = extraTicketsInflater.inflate(R.layout.item_ticket_info_name_no, null)
            extraTicketsLayout.removeViewAt(extraTicketsLayout.size - 1)

            extraTicketLayoutToAdd = extraTicketsInflater.inflate(R.layout.item_ticket_info_name_yes, null)
            extraTicketsLayout.addView(extraTicketLayoutToAdd)
            extraTicketLayoutToAdd.itemTicketYesInfo_text.text = "Entrada $ticketCount:"
            extraTicketLayoutToAdd.itemTicketYesInfo_name.text = receivedData
            extraTicketLayoutToAdd.itemTicketYesInfo_button.setOnClickListener() {
                val intent = Intent(this, EventScreenActivitySearch::class.java)
                friendsSearchLauncherV2.launch(intent)
            }
        } else {
            // If (result.resultCode == Activity.RESULT_CANCELED)
        }
    }

    private val friendsSearchLauncherV2: ActivityResultLauncher<Intent> = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val receivedData = result.data?.getStringExtra("receivedData")

            extraTicketLayoutToAdd = extraTicketsInflater.inflate(R.layout.item_ticket_info_name_no, null)
            extraTicketsLayout.removeViewAt(extraTicketsLayout.size - 1)

            extraTicketLayoutToAdd = extraTicketsInflater.inflate(R.layout.item_ticket_info_name_yes, null)
            extraTicketsLayout.addView(extraTicketLayoutToAdd)
            extraTicketLayoutToAdd.itemTicketYesInfo_name.text = receivedData
            extraTicketLayoutToAdd.itemTicketYesInfo_button.setOnClickListener() {
                val intent = Intent(this, EventScreenActivitySearch::class.java)
                friendsSearchLauncherV1.launch(intent)
            }
        } else {
            // If (result.resultCode == Activity.RESULT_CANCELED)
        }
    }
}