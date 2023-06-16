package com.example.nq

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.nq.authSignIn.SignInActivity
import com.example.nq.firebase.FirebaseManager
import com.example.nq.recyclerViewTickets.TicketData
import com.example.nq.recyclerViewTickets.TicketsAdapter
import com.example.nq.recyclerViewTickets.TicketsRepository
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.fragment_my_tickets.*
import me.relex.circleindicator.CircleIndicator3

class FragmentMyTickets : Fragment(R.layout.fragment_my_tickets) {

    private lateinit var mainActivityBotMenu: BottomNavigationView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainActivityBotMenu = requireActivity().findViewById(R.id.mainActivity_botMenu)

        (activity as AppCompatActivity).supportActionBar?.title = "ENTRADAS"

        //SET LAYOUT DEPENDING ON USER LOGGED IN STATE AND TICKET AVAILABILITY
        if (!FirebaseManager().checkIfUserIsSignedIn()) {
            showNotLoggedView()
        } else {
            val tickets = TicketsRepository.ticketList
            if (tickets.isNotEmpty()) {
                showTicketsAvailableView(tickets)
            } else {
                showNoTicketsAvailableView()
            }
        }
    }

    private fun showNotLoggedView() {
        // Set visibility
        fragMyTickets_signedInLayout_NoTickets.visibility = View.GONE
        fragMyTickets_signedInLayout_AvailableTickets.visibility = View.GONE
        fragMyTickets_unsignedInLayout.visibility = View.VISIBLE

        // Botón SignIn
        fragMyTickets_signInButton.setOnClickListener() {
            Intent(activity, SignInActivity::class.java).also {
                startActivity(it)
            }
        }

        // Botón comprar tickets sin estar loggeado
        fragMyTickets_buyTicketsUnsignedInButton.setOnClickListener {
            val fragmentBuyTickets = FragmentBuyTickets()
            SetFragment.setCurrentFragment(requireActivity().supportFragmentManager, fragmentBuyTickets, mainActivityBotMenu)
            requireActivity().invalidateOptionsMenu()
        }
    }

    private fun showTicketsAvailableView(tickets: List<TicketData>) {
        // Set visibility
        fragMyTickets_unsignedInLayout.visibility = View.GONE
        fragMyTickets_signedInLayout_NoTickets.visibility = View.GONE
        fragMyTickets_signedInLayout_AvailableTickets.visibility = View.VISIBLE

        // Adaptar la RecyclerView
        val adapter = TicketsAdapter(tickets, requireActivity().supportFragmentManager, mainActivityBotMenu)
        imageViewPagerAvailableTicket.adapter = adapter
        val circleIndicatorQR = view?.findViewById<CircleIndicator3>(R.id.circleIndicatorQR)
        circleIndicatorQR?.setViewPager(imageViewPagerAvailableTicket)
    }

    private fun showNoTicketsAvailableView() {
        // Set visibility
        fragMyTickets_unsignedInLayout.visibility = View.GONE
        fragMyTickets_signedInLayout_AvailableTickets.visibility = View.GONE
        fragMyTickets_signedInLayout_NoTickets.visibility = View.VISIBLE

        // Botón para volver a la pantalla de comprar tickets
        fragMyTickets_buyTicketsUnsignedInButton.setOnClickListener {
            val fragmentBuyTickets = FragmentBuyTickets()
            SetFragment.setCurrentFragment(requireActivity().supportFragmentManager, fragmentBuyTickets, mainActivityBotMenu)
            requireActivity().invalidateOptionsMenu()
        }
    }
}