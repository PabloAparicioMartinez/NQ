package com.example.nq

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        //VARIABLES INITIALIZATION
        val buyTicketsFragment = FragmentBuyTickets()
        val myTicketsFragment = FragmentMyTickets()
        val myProfileFragment = FragmentMyProfile()

        //SET INITIAL FRAGMENT
        SetFragment.setCurrentFragment(supportFragmentManager, buyTicketsFragment, mainActivity_botMenu)
        invalidateOptionsMenu()

        //SET FRAGMENT BASED ON SELECTED BOTTOM NAVIGATION MENU ITEMID
        mainActivity_botMenu.setOnItemSelectedListener {
            when(it.itemId){
                R.id.navigation_buy_tickets -> {
                    SetFragment.setCurrentFragment(supportFragmentManager, buyTicketsFragment, mainActivity_botMenu)
                    invalidateOptionsMenu()
                }
                R.id.navigation_my_tickets -> {
                    SetFragment.setCurrentFragment(supportFragmentManager, myTicketsFragment, mainActivity_botMenu)
                    invalidateOptionsMenu()
                }
                R.id.navigation_my_profile -> {
                    SetFragment.setCurrentFragment(supportFragmentManager, myProfileFragment, mainActivity_botMenu)
                    invalidateOptionsMenu()
                }
            }
            true
        }
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