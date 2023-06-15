package com.example.nq

import android.view.Menu
import android.view.MenuItem
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomnavigation.BottomNavigationView

class SetFragment {

    companion object {

        private lateinit var bottomMenu : Menu
        private lateinit var buyTicketsMenuItem : MenuItem
        private lateinit var myTicketsMenuItem : MenuItem
        private lateinit var myProfileMenuItem : MenuItem

        // Función para seleccionar
        private fun setMenuIcons(key: String){
            val list = when(key) {
                "icon_buy_tickets" -> listOf(
                R.drawable.ic_loupe_full_01,
                R.drawable.ic_ticket_nofull_01,
                R.drawable.ic_profile_nofull_01
                )
                "icon_my_tickets" -> listOf(
                    R.drawable.ic_loupe_nofull_01,
                    R.drawable.ic_ticket_full_01,
                    R.drawable.ic_profile_nofull_01
                )
                "icon_my_profile" -> listOf(
                R.drawable.ic_loupe_nofull_01,
                R.drawable.ic_ticket_nofull_01,
                R.drawable.ic_profile_full_01
                )
                else -> listOf(
                    R.drawable.ic_loupe_full_01,
                    R.drawable.ic_ticket_nofull_01,
                    R.drawable.ic_profile_nofull_01
                )
            }

            buyTicketsMenuItem.setIcon(list[0])
            myTicketsMenuItem.setIcon(list[1])
            myProfileMenuItem.setIcon(list[2])
        }


        // Declare the function as a public static method using @JvmStatic
        @JvmStatic
        internal fun setCurrentFragment(fragmentManager: FragmentManager,
                                        fragment: Fragment,
                                        bottomNavigationView: BottomNavigationView) {

            // Declarar las variables
            bottomMenu = bottomNavigationView.menu
            buyTicketsMenuItem = bottomMenu.findItem(R.id.navigation_buy_tickets)
            myTicketsMenuItem = bottomMenu.findItem(R.id.navigation_my_tickets)
            myProfileMenuItem = bottomMenu.findItem(R.id.navigation_my_profile)

            // Create a new transaction and replace the current fragment with the new one
            fragmentManager.beginTransaction().apply {
                // Remove the previously added fragment
                fragmentManager.fragments.forEach { remove(it) }
                // Animations to be more smooth
                setCustomAnimations(
                    R.anim.fade_in,  // fade in animation
                    R.anim.fade_out, // fade out animation
                    R.anim.fade_in,  // reverse fade in animation
                    R.anim.fade_out  // reverse fade out animation
                )
                replace(R.id.main_frameLayout, fragment)
                commit()
            }

            // Seleccionar el botón del menú correspondiente y la ilustración que se muestra
            when (fragment) {
                is FragmentBuyTickets -> {
                    if (!buyTicketsMenuItem.isChecked) buyTicketsMenuItem.isChecked = true
                    setMenuIcons("icon_buy_tickets")
                }
                is FragmentMyTickets -> {
                    if (!myTicketsMenuItem.isChecked) myTicketsMenuItem.isChecked = true
                    setMenuIcons("icon_my_tickets")
                }
                is FragmentMyProfile -> {
                    if (!myProfileMenuItem.isChecked) myProfileMenuItem.isChecked = true
                    setMenuIcons("icon_my_profile")
                }
            }

        }
    }
}