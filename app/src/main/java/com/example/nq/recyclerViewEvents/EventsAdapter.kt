package com.example.nq.recyclerViewEvents

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.nq.R
import kotlinx.android.synthetic.main.item_event.view.*

class EventsAdapter(
    var events: List<EventsData>,
    val listener: EventsInterface
) : RecyclerView.Adapter<EventsAdapter.EventsViewHolder>(){

    inner class EventsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
            val position = absoluteAdapterPosition
            if (position != RecyclerView.NO_POSITION){
                listener.onItemClick(events[position])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_event, parent, false)
        return EventsViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventsViewHolder, position: Int) {
        holder.itemView.apply{
            val priceDescription = "${events[position].eventPrice} € | ${events[position].eventIncludedWithTicket}"
            itemDiscoEvent_image.setImageResource(events[position].eventImage)
            itemDiscoEvent_date.text = events[position].eventDate
            itemDiscoEvent_name.text = events[position].eventName
            itemDiscoEvent_music.text = events[position].eventMusic
            itemDiscoEvent_price.text = priceDescription
            val ticketNumber = events[position].eventTicketNumber
            val ticketDisponibility = setTicketDisponibility(ticketNumber)
            itemDiscoEvent_availability.text = ticketDisponibility
            itemDiscoEvent_availability.setTextColor(returnAvailabilityColor(ticketDisponibility))
            itemDiscoEvent_arrow.setColorFilter(returnArrowColor(ticketDisponibility))
        }
    }

    override fun getItemCount(): Int {
        return events.size
    }

    internal fun setFilteredList (events: List<EventsData>){
        this.events = events
        notifyDataSetChanged()
    }

    private fun setTicketDisponibility(ticketNumber: Int): String {
        val ticketsDisponibility: String = if (ticketNumber > 0) {
            if (ticketNumber > 30) {
                "DISPONIBLES"
            } else {
                "ÚLTIMAS ENTRADAS"
            }
        } else {
            "AGOTADAS"
        }

        return ticketsDisponibility
    }

    private fun returnAvailabilityColor(availabiliy: String): Int {
        return when(availabiliy){
            "DISPONIBLES" -> Color.rgb(0, 175, 0)
            "ÚLTIMAS ENTRADAS" -> Color.rgb(204, 102, 0)
            "AGOTADAS" -> Color.rgb(175, 0, 0)
            else -> Color.rgb(0, 0, 0)
        }
    }

    private fun returnArrowColor(availabiliy: String): Int {
        return when(availabiliy){
            "DISPONIBLES" -> Color.rgb(15, 76, 117)
            "ÚLTIMAS ENTRADAS" -> Color.rgb(15, 76, 117)
            "AGOTADAS" -> Color.rgb(237, 239, 236)
            else -> Color.rgb(0, 0, 0)
        }
    }
}