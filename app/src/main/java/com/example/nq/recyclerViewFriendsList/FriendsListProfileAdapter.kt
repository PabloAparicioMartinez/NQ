package com.example.nq.recyclerViewFriendsList

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.nq.R
import com.example.nq.authFirebase.FirebaseUserData
import kotlinx.android.synthetic.main.item_friend.view.itemFriend_image
import kotlinx.android.synthetic.main.item_friend.view.itemFriend_mail
import kotlinx.android.synthetic.main.item_friend.view.itemFriend_name
import kotlinx.android.synthetic.main.item_friend_profile.view.*

class FriendsListProfileAdapter(
    private var friendsListProfileAdapter: MutableList<FirebaseUserData>,
    private val listener: FriendsListProfileInterface
) : RecyclerView.Adapter<FriendsListProfileAdapter.FriendsProfileViewHolder>() {

    inner class FriendsProfileViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val deleteFriendButton: ImageView = itemView.itemDeleteFriendButton

        init {
            deleteFriendButton.setOnClickListener {
                val position = absoluteAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.onDeleteClick(position)
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendsProfileViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_friend_profile, parent, false)
        return FriendsProfileViewHolder(view)
    }

    override fun onBindViewHolder(holder: FriendsProfileViewHolder, position: Int) {
        val currentFriend = friendsListProfileAdapter[position]

        holder.itemView.apply{
            itemFriend_image.setImageURI(Uri.parse(currentFriend.uri))
            itemFriend_name.text = "${currentFriend.name} ${currentFriend.surnames}"
            itemFriend_mail.text = currentFriend.email
        }
    }

    override fun getItemCount(): Int {
        return friendsListProfileAdapter.size
    }

}
