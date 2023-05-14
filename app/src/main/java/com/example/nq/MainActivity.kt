package com.example.nq

import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var buyTicketsFragment : Fragment
    private lateinit var myTicketsFragment : Fragment
    private lateinit var myProfileFragment : Fragment

    lateinit var bottomMenu : Menu
    private lateinit var buyTicketsMenuItem : MenuItem
    private lateinit var myTicketsMenuItem : MenuItem
    private lateinit var myProfileMenuItem : MenuItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //VARIABLES INITIALIZATION
        buyTicketsFragment = FragmentBuyTickets()
        myTicketsFragment = FragmentMyTickets()
        myProfileFragment = FragmentMyProfile()

        bottomMenu = mainActivity_botMenu.menu
        buyTicketsMenuItem = bottomMenu.findItem(R.id.navigation_buy_tickets)
        myTicketsMenuItem = bottomMenu.findItem(R.id.navigation_my_tickets)
        myProfileMenuItem = bottomMenu.findItem(R.id.navigation_my_profile)

        //SET INITIAL FRAGMENT
        setCurrentFragment(buyTicketsFragment)
        buyTicketsMenuItem.setIcon(R.drawable.ic_loupe_full_01)
        invalidateOptionsMenu()

        mainActivity_botMenu.setOnItemSelectedListener {
            when(it.itemId){
                R.id.navigation_buy_tickets -> {
                    setCurrentFragment(buyTicketsFragment)
                    setMenuIcons(listOf(
                        ContextCompat.getDrawable(this, R.drawable.ic_loupe_full_01),
                        ContextCompat.getDrawable(this, R.drawable.ic_ticket_nofull_01),
                        ContextCompat.getDrawable(this, R.drawable.ic_profile_nofull_01),
                    ))
                    invalidateOptionsMenu()
                }
                R.id.navigation_my_tickets -> {
                    setCurrentFragment(myTicketsFragment)
                    setMenuIcons(listOf(
                        ContextCompat.getDrawable(this, R.drawable.ic_loupe_nofull_01),
                        ContextCompat.getDrawable(this, R.drawable.ic_ticket_full_01),
                        ContextCompat.getDrawable(this, R.drawable.ic_profile_nofull_01),
                    ))
                    invalidateOptionsMenu()
                }
                R.id.navigation_my_profile -> {
                    setCurrentFragment(myProfileFragment)
                    setMenuIcons(listOf(
                        ContextCompat.getDrawable(this, R.drawable.ic_loupe_nofull_01),
                        ContextCompat.getDrawable(this, R.drawable.ic_ticket_nofull_01),
                        ContextCompat.getDrawable(this, R.drawable.ic_profile_full_01),
                    ))
                    invalidateOptionsMenu()
                }
            }
            true
        }
    }

    private fun setCurrentFragment(fragment: Fragment){
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.setCustomAnimations(
            R.anim.fade_in,  // fade in animation
            R.anim.fade_out, // fade out animation
            R.anim.fade_in,  // reverse fade in animation
            R.anim.fade_out  // reverse fade out animation
        )
        fragmentTransaction.replace(R.id.main_frameLayout, fragment)
        fragmentTransaction.commit()
    }

    private fun setMenuIcons(list: List<Drawable?>){
        buyTicketsMenuItem.icon = list[0]
        myTicketsMenuItem.icon = list[1]
        myProfileMenuItem.icon = list[2]
    }

    //Called from FragmentMyTicketsUnsignedIn
    fun clickOnBuyTicketsMenuIcon(){
        buyTicketsMenuItem.isChecked = true
        setCurrentFragment(buyTicketsFragment)
        buyTicketsMenuItem.setIcon(R.drawable.ic_loupe_full_01)
        myTicketsMenuItem.setIcon(R.drawable.ic_ticket_nofull_01)
        myProfileMenuItem.setIcon(R.drawable.ic_profile_nofull_01)
        invalidateOptionsMenu()
    }

    //Permissions
    fun requestPermission() {
        if (!hasLocationForegroundPermission()){
            val permissionsToRequest = mutableListOf(android.Manifest.permission.ACCESS_COARSE_LOCATION)
            ActivityCompat.requestPermissions(this, permissionsToRequest.toTypedArray(), 0)
        }
        else {
        }
    }

    private fun hasLocationForegroundPermission() =
        ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 0 && grantResults.isNotEmpty()) {
            for (i in grantResults.indices){
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED)
                    Log.d("PermissionsRequest", "${permissions[i]} granted")
            }
        }
    }
}