package com.example.nq

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.nq.recyclerViewDiscos.DiscosAdapter
import com.example.nq.recyclerViewDiscos.DiscosData
import com.example.nq.recyclerViewDiscos.DiscosInterface
import com.example.nq.recyclerViewDiscos.DiscosRepository
import kotlinx.android.synthetic.main.fragment_buy_tickets.*

class FragmentBuyTickets : Fragment(R.layout.fragment_buy_tickets), DiscosInterface {

    val discosAdapter = DiscosAdapter(DiscosRepository.discos, this)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fragmentBuyTicketsBasic_recyclerView.adapter = discosAdapter
        fragmentBuyTicketsBasic_recyclerView.layoutManager = LinearLayoutManager(activity)
    }

    override fun onItemClick(discoData: DiscosData) {
        Intent(activity, DiscoScreenActivity::class.java).also {
            it.putExtra("EXTRA_IMAGE", discoData.discoImage)
            it.putExtra("EXTRA_NAME", discoData.discoName)
            it.putExtra("EXTRA_LOCATION", discoData.discoLocation)
            it.putExtra("EXTRA_DISTANCE", discoData.discoDistance)
            it.putExtra("EXTRA_MUSIC", discoData.discoMusic)
            startActivity(it)
        }
    }
}