package com.example.nq.recyclerViewDates

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.view.get
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.nq.R
import kotlinx.android.synthetic.main.item_date.view.*
import kotlinx.coroutines.launch

class DatesAdapter(
    private var dates: List<DatesData>,
    val listener: DatesInterface
) : RecyclerView.Adapter<DatesAdapter.DatesViewHolder>() {

    // cambio

    val dateCardViews = mutableListOf<CardView>()

    inner class DatesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) , View.OnClickListener {

        val linearLayout: LinearLayout = itemView.findViewById(R.id.itemDate_layout)
        val textViewWeek: TextView = itemView.findViewById(R.id.itemDate_week)
        val textViewNumber: TextView = itemView.findViewById(R.id.itemDate_number)
        val textViewMonth: TextView = itemView.findViewById(R.id.itemDate_month)
        val textViews = listOf(textViewWeek, textViewNumber, textViewMonth)

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

    fun EnableClicking (shouldEnable: Boolean) {
        for (i in dateCardViews.indices){
            dateCardViews[i].isClickable = shouldEnable
        }
    }
}