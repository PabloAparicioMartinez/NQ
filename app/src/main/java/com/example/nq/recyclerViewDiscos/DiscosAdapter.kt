package com.example.nq.recyclerViewDiscos

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.nq.R
import kotlinx.android.synthetic.main.item_disco.view.*

class DiscosAdapter(
    var discos: List<DiscosData>,
    val listener: DiscosInterface
) : RecyclerView.Adapter<DiscosAdapter.DiscosViewHolder>() {

    inner class DiscosViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
            val position = absoluteAdapterPosition
            if (position != RecyclerView.NO_POSITION){
                listener.onItemClick(discos[position])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiscosViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_disco, parent, false)
        return DiscosViewHolder(view)
    }

    override fun onBindViewHolder(holder: DiscosViewHolder, position: Int) {
        holder.itemView.apply{
            itemDiscoInfo_image.setImageResource(discos[position].discoImage)
            itemDiscoInfo_name.text = discos[position].discoName
            itemDiscoInfo_location.text = discos[position].discoLocation
            itemDiscoEvent_price.text = discos[position].discoDistance
            itemDiscoInfo_music.text = discos[position].discoMusic
        }
    }

    override fun getItemCount(): Int {
        return discos.size
    }
}