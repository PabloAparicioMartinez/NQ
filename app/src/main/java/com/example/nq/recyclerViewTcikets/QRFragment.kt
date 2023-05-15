package com.example.nq.recyclerViewTcikets

import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomnavigation.BottomNavigationView
import me.relex.circleindicator.CircleIndicator3

class QRFragment : Fragment(R.layout.fragment_qr_viewpager) {

    private lateinit var viewModel: TicketViewModel

    private lateinit var imageViewPagerQR: ViewPager2
    internal lateinit var emptyView: View
    private lateinit var buttonGoToInfo: Button
    private lateinit var bottomNavigationView: BottomNavigationView


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[TicketViewModel::class.java]
        viewModel.cacheManager = CacheManager(requireContext())

        imageViewPagerQR = view.findViewById(R.id.imageViewPagerQR)
        emptyView = view.findViewById(R.id.empty_view)
        buttonGoToInfo = view.findViewById(R.id.buttonGoToInfo)
        bottomNavigationView = requireActivity().findViewById(R.id.bottomNavigationView)

        viewModel.loadTicketData()
        viewModel.ticketDataList.observe(viewLifecycleOwner) { tickets ->
            if (tickets.isNotEmpty()) {
                showView(tickets)
            } else {
                showEmptyView()
            }
        }
    }


    private fun showView(tickets: List<Ticket>) {
        val adapter = ViewPagerAdapter(tickets)
        imageViewPagerQR.adapter = adapter
        val circleIndicatorQR = view?.findViewById<CircleIndicator3>(R.id.circleIndicatorQR)
        circleIndicatorQR?.setViewPager(imageViewPagerQR)
    }

    private fun showEmptyView() {
        emptyView.visibility = View.VISIBLE
        // Botón para volver a la pantalla de comprar tickets
        buttonGoToInfo.setOnClickListener {
            val infoFragment = InfoFragment()
            SetFragment.setCurrentFragment(requireActivity().supportFragmentManager, infoFragment)
            SetFragment.setCurrentFragmentItem(infoFragment, bottomNavigationView)
        }
    }

}
