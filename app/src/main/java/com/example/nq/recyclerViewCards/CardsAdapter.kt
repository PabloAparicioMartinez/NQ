package com.example.nq.recyclerViewCards

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
import com.example.nq.recyclerViewDates.DatesAdapter
import com.example.nq.recyclerViewDates.DatesData
import com.example.nq.recyclerViewDates.DatesInterface
import kotlinx.android.synthetic.main.item_card.view.*
import kotlinx.coroutines.launch

class CardsAdapter(
    private var cards: List<CardsData>
) : RecyclerView.Adapter<CardsAdapter.CardsViewHolder>() {

    inner class CardsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_card, parent, false)
        return CardsViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardsViewHolder, position: Int) {
        holder.itemView.apply {
            val cardNumberString = cards[position].cardNumber.toString()
            val firstFourNumbers = cardNumberString.substring(0, 4)
            val secondFourNumbers = cardNumberString.substring(4, 8)
            val thirdFourNumbers = cardNumberString.substring(8, 12)
            val fourthFourNumbers = cardNumberString.substring(12, 16)

            itemCard_cardNumber.text = "$firstFourNumbers $secondFourNumbers $thirdFourNumbers $fourthFourNumbers"
            itemCard_cardExpiration.text = "Expira el ${cards[position].cardMonth}/${cards[position].cardYear}"
            itemCard_cardName.text = cards[position].cardName
        }
    }

    override fun getItemCount(): Int {
        return cards.size
    }
}