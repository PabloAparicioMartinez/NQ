package com.example.nq

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.nq.recyclerViewDiscos.DiscosAdapter
import com.example.nq.recyclerViewDiscos.DiscosData
import com.example.nq.recyclerViewDiscos.DiscosInterface
import com.example.nq.recyclerViewDiscos.DiscosRepository
import kotlinx.android.synthetic.main.fragment_buy_tickets.*

class FragmentBuyTickets : Fragment(R.layout.fragment_buy_tickets), MenuProvider, DiscosInterface {

    private val discosAdapter = DiscosAdapter(DiscosRepository.discos, this)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.top_menu_buytickets, menu)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fragmentBuyTicketsBasic_recyclerView.adapter = discosAdapter
        fragmentBuyTicketsBasic_recyclerView.layoutManager = LinearLayoutManager(activity)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.topMenuBuyTickets_location -> {
                (activity as MainActivity).requestPermission()
            }
        }
        return false
    }

    override fun onItemClick(discoData: DiscosData) {

        Intent(activity, DiscoScreenActivityCalendar::class.java).also {
            it.putExtra("EXTRA_IMAGE", discoData.discoImage)
            it.putExtra("EXTRA_DISCO_NAME", discoData.discoName)
            it.putExtra("EXTRA_LOCATION", discoData.discoLocation)
            it.putExtra("EXTRA_MUSIC", discoData.discoMusic)
            startActivity(it)
        }
    }
}