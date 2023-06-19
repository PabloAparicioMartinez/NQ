package com.example.nq.recyclerViewDates

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.nq.R
import kotlinx.android.synthetic.main.item_date.view.*

class DatesAdapter(
    private var dates: List<DatesData>,
    val listener: DatesInterface
) : RecyclerView.Adapter<DatesAdapter.DatesViewHolder>() {

    // Cambio Master & Commander

    private val dateCardViews = mutableListOf<CardView>()

    inner class DatesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) , View.OnClickListener {

        val linearLayout: LinearLayout = itemView.findViewById(R.id.itemDate_layout)
        private val textViewWeek: TextView = itemView.findViewById(R.id.itemDate_week)
        private val textViewNumber: TextView = itemView.findViewById(R.id.itemDate_number)
        private val textViewMonth: TextView = itemView.findViewById(R.id.itemDate_month)
        private val textViews = listOf(textViewWeek, textViewNumber, textViewMonth)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
            val position = absoluteAdapterPosition
            if (position != RecyclerView.NO_POSITION){
                dates[position].clicked = !dates[position].clicked
                listener.onItemClick(dates[position], linearLayout, textViews)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DatesViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_date, parent, false)
        return DatesViewHolder(view)
    }

    override fun onBindViewHolder(holder: DatesViewHolder, position: Int) {
        holder.itemView.apply{
            itemDate_week.text = dates[position].dateWeek
            itemDate_number.text = dates[position].dateNumber.toString()
            itemDate_month.text = dates[position].dateMonth

            dateCardViews.add(itemDate_cardView)
        }
    }

    override fun getItemCount(): Int {
        return dates.size
    }

    fun enableClicking (shouldEnable: Boolean) {
        for (i in dateCardViews.indices){
            dateCardViews[i].isClickable = shouldEnable
        }
    }
}