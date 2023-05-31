package com.example.nq.recyclerViewFriends

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.nq.R
import com.example.nq.firebase.FirebaseUserData
import kotlinx.android.synthetic.main.item_friend.view.*

class FriendsAdapter(
    var friends: MutableList<FirebaseUserData>,
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
            itemFriend_image.setImageURI(Uri.parse(friends[position].uri))
            itemFriend_name.text = "${friends[position].name} ${friends[position].surnames}"
            itemFriend_mail.text = friends[position].email
        }
    }

    override fun getItemCount(): Int {
        return friends.size
    }

}
