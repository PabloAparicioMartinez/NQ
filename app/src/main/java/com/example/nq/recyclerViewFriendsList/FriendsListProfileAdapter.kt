package com.example.nq.recyclerViewFriendsList

import CircleTransform
import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.nq.R
import com.example.nq.storageFirebase.FirebaseUserData
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_friend.view.itemFriend_image
import kotlinx.android.synthetic.main.item_friend.view.itemFriend_mail
import kotlinx.android.synthetic.main.item_friend.view.itemFriend_name
import kotlinx.android.synthetic.main.item_friend_profile.view.*
import java.io.File

class FriendsListProfileAdapter(
    private var friendsListProfileAdapter: MutableList<FirebaseUserData>,
    private val listener: FriendsListProfileInterface,
    private val context: Context
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
        val imageName = friendsListProfileAdapter[position].ID
        val userPicture = File(context.filesDir, "${imageName}.png")

        holder.itemView.apply{
            Picasso.get().load(currentFriend.ID).into(itemFriend_image)
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

            itemFriend_name.text = "${currentFriend.name} ${currentFriend.surnames}"
            itemFriend_mail.text = currentFriend.email
        }
    }

    override fun getItemCount(): Int {
        return friendsListProfileAdapter.size
    }

}
