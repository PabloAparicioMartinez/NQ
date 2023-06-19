package com.example.nq.recyclerViewFriendsList

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.nq.R
import com.example.nq.authFirebase.FirebaseUserData
import kotlinx.android.synthetic.main.item_friend.view.*

class FriendsListAdapter(
    private var friends: MutableList<FirebaseUserData>,
    private val listener: FriendsListInterface
) : RecyclerView.Adapter<FriendsListAdapter.FriendsViewHolder>() {

    inner class FriendsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
            val position = absoluteAdapterPosition
            if (position != RecyclerView.NO_POSITION){
                val friend = friends[position]
                listener.onItemClick(friend)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_friend, parent, false)
        return FriendsViewHolder(view)
    }

    override fun onBindViewHolder(holder: FriendsViewHolder, position: Int) {
        val friend = friends[position]
        holder.itemView.apply{
            itemFriend_image.setImageURI(Uri.parse(friend.uri))
            itemFriend_name.text = "${friend.name} ${friend.surnames}"
            itemFriend_mail.text = friend.email
        }
    }

    override fun getItemCount(): Int {
        return friends.size
    }

}
