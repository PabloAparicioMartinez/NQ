package com.example.nq.recyclerViewProfilePictures

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.nq.R
import kotlinx.android.synthetic.main.item_profile_picture.view.*

class ProfilePicturesAdapter(
    var profilePictures: List<ProfilePicturesData>,
    val listener: ProfilePicturesInterface
) : RecyclerView.Adapter<ProfilePicturesAdapter.ProfilePicturesViewHolder>() {

    inner class ProfilePicturesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
            val position = absoluteAdapterPosition
            if (position != RecyclerView.NO_POSITION){
                listener.onItemClick(profilePictures[position], position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfilePicturesAdapter.ProfilePicturesViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_profile_picture, parent, false)
        return ProfilePicturesViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProfilePicturesAdapter.ProfilePicturesViewHolder, position: Int) {
        holder.itemView.apply{
            itemProfilePicture_image.setImageResource(profilePictures[position].profileImage)
        }
    }

    override fun getItemCount(): Int {
        return profilePictures.size
    }
}