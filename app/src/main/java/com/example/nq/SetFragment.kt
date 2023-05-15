package com.example.nq

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomnavigation.BottomNavigationView

class SetFragment {

    companion object {

        // Declare the function as a public static method using @JvmStatic
        @JvmStatic
        // Seleccionar el Icono del fragmento
        internal fun setCurrentFragmentItem(fragment: Fragment, bottomNavigationView: BottomNavigationView) {
            bottomNavigationView.selectedItemId = when (fragment) {
                is FragmentBuyTickets -> R.id.navigation_buy_tickets
                is FragmentMyProfileSignedIn -> R.id.navigation_my_tickets
                is FragmentMyProfileSignedIn -> R.id.navigation_my_profile
                else -> R.id.navigation_buy_tickets
            }
        }

        // Declare the function as a public static method using @JvmStatic
        @JvmStatic
        // Seleccionar el fragmento
        internal fun setCurrentFragment(fragmentManager: FragmentManager, fragment: Fragment) {
            // Create a new transaction and replace the current fragment with the new one
            fragmentManager.beginTransaction().apply {
                // Remove the previously added fragment
                fragmentManager.fragments.forEach { remove(it) }
                replace(R.id.main_frameLayout, fragment)
                commit()
            }
        }
    }
}