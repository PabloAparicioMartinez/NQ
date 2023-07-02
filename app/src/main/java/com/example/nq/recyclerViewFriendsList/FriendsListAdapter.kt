package com.example.nq.recyclerViewFriendsList

import CircleTransform
import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.nq.R
import com.example.nq.storageFirebase.FirebaseUserData
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_friend.view.*
import java.io.File

class FriendsListAdapter(
    private var friends: MutableList<FirebaseUserData>,
    private val listener: FriendsListInterface,
    private val context: Context
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
        val imageName = friends[position].ID
        val userPicture = File(context.filesDir, "${imageName}.png")
        
        holder.itemView.apply{
            if (userPicture.exists()) {
                val userPictureUrl = Uri.fromFile(userPicture).toString()
                val cacheBustingUrl = "$userPictureUrl?${System.currentTimeMillis()}"
                Picasso.get()
                    .load(cacheBustingUrl)
                    .transform(CircleTransform())
                    .into(itemFriend_image)
            } else {
                Picasso.get()
                    .load(R.drawable.png_nq)
                    .transform(CircleTransform())
                    .into(itemFriend_image)
            }

            itemFriend_name.text = "${friend.name} ${friend.surnames}"
            itemFriend_mail.text = friend.email
        }
    }

    override fun getItemCount(): Int {
        return friends.size
    }

}
