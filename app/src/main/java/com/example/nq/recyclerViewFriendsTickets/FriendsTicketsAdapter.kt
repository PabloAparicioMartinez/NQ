package com.example.nq.recyclerViewFriendsTickets

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.nq.R
import kotlinx.android.synthetic.main.item_ticket_for_friend.view.*

class FriendsTicketsAdapter(
    private val friendsTicketsAdapterList: List<FriendsTicketsData>,
    val listener: FriendsTicketsInterface
) : RecyclerView.Adapter<FriendsTicketsAdapter.FriendsTicketsViewHolder>(){

    inner class FriendsTicketsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        internal val addFriendButton: Button = itemView.itemTicketComprarParaUnAmigo_button
        internal val deleteFriendButton: Button = itemView.itemTicketEliminate_button
        internal val noInfoNameTextView: TextView = itemView.itemTicketNoInfo_Name
        internal val yesInfoNameTextView: TextView = itemView.itemTicketYesInfo_Name
        internal val numberTextView: TextView = itemView.itemTicketInfo_Number

        init {
            addFriendButton.setOnClickListener {
                val position = absoluteAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.onAddFriendClick(position)
                }
            }

            deleteFriendButton.setOnClickListener {
                val position = absoluteAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.onDeleteFriendClick(position)
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendsTicketsViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_ticket_for_friend, parent, false)
        return FriendsTicketsViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: FriendsTicketsViewHolder, position: Int) {
        val currentItem = friendsTicketsAdapterList[position]

        holder.apply {

            if (currentItem.friendName != "NOMBRE") {
                addFriendButton.visibility = View.GONE
                deleteFriendButton.visibility = View.VISIBLE
                noInfoNameTextView.visibility = View.GONE
                yesInfoNameTextView.visibility = View.VISIBLE
                val nameToDisplay = "${currentItem.friendName} ${currentItem.friendSurnames}"
                val numberToDisplay = "Entrada ${position+2}"
                yesInfoNameTextView.text = nameToDisplay
                numberTextView.text = numberToDisplay
            } else {
                deleteFriendButton.visibility = View.GONE
                addFriendButton.visibility = View.VISIBLE
                yesInfoNameTextView.visibility = View.GONE
                noInfoNameTextView.visibility = View.VISIBLE
                val numberToDisplay = "Entrada ${position+2}"
                numberTextView.text = numberToDisplay
            }
        }
    }

    override fun getItemCount(): Int {
        return friendsTicketsAdapterList.size
    }

}