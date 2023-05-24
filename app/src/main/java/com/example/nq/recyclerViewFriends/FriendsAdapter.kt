package com.example.nq.recyclerViewFriends

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.nq.R
import kotlinx.android.synthetic.main.item_friend.view.*

class FriendsAdapter(
    var friends: List<UsersData>,
    val listener: FriendsInterface
) : RecyclerView.Adapter<FriendsAdapter.FriendsViewHolder>(){

    inner class FriendsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
            val position = absoluteAdapterPosition
            if (position != RecyclerView.NO_POSITION){
                listener.onItemClick(friends[position])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_friend, parent, false)
        return FriendsViewHolder(view)
    }

    override fun onBindViewHolder(holder: FriendsViewHolder, position: Int) {
        holder.itemView.apply{
            itemFriend_image.setImageResource(friends[position].image)
            itemFriend_name.text = friends[position].name
            itemFriend_mail.text = friends[position].mail
            itemFriend_friendsBoolean.text = returnFriendsBooleanText(friends[position].friends)
        }
    }

    override fun getItemCount(): Int {
        return friends.size
    }

    private fun returnFriendsBooleanText(friendsBoolean: Boolean) : String {
        if (friendsBoolean) return ""
        else return "Solicitud enviada"
    }
}