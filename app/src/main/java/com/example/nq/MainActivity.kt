package com.example.nq

import android.content.pm.PackageManager
import android.content.pm.PathPermission
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import com.example.nq.firebase.FirebaseManager
import kotlinx.android.synthetic.main.activity_main.*
import java.util.jar.Manifest

class MainActivity : AppCompatActivity() {

    private lateinit var buyTicketsFragment : Fragment
    private lateinit var myTicketsFragmentUnsignedIn : Fragment
    private lateinit var myProfileFragmentUnsignedIn: Fragment
    private lateinit var myTicketsFragmentSignedIn : Fragment
    private lateinit var myProfileFragmentSignedIn: Fragment

    lateinit var bottomMenu : Menu
    private lateinit var buyTicketsMenuItem : MenuItem
    private lateinit var myTicketsMenuItem : MenuItem
    private lateinit var myProfileMenuItem : MenuItem

    private lateinit var firebaseManager : FirebaseManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Eliminar luego

        //Variables initialization
        buyTicketsFragment = FragmentBuyTickets()
        myTicketsFragmentUnsignedIn = FragmentMyTicketsUnsignedIn()
        myProfileFragmentUnsignedIn = FragmentMyProfileUnsignedIn()
        myTicketsFragmentSignedIn = FragmentMyTicketsSignedIn()
        myProfileFragmentSignedIn = FragmentMyProfileSignedIn()

        bottomMenu = mainActivity_botMenu.menu
        buyTicketsMenuItem = bottomMenu.findItem(R.id.navigation_buy_tickets)
        myTicketsMenuItem = bottomMenu.findItem(R.id.navigation_my_tickets)
        myProfileMenuItem = bottomMenu.findItem(R.id.navigation_my_profile)

        firebaseManager = FirebaseManager()

        //Set initial fragment
        setCurrentFragment(buyTicketsFragment)
        buyTicketsMenuItem.setIcon(R.drawable.ic_loupe_full_01)
        invalidateOptionsMenu()

        mainActivity_botMenu.setOnItemSelectedListener {
            when(it.itemId){
                R.id.navigation_buy_tickets -> {
                    setCurrentFragment(returnFragmentName(it.itemId))
                    setMenuIcons(listOf(
                        ContextCompat.getDrawable(this, R.drawable.ic_loupe_full_01),
                        ContextCompat.getDrawable(this, R.drawable.ic_ticket_nofull_01),
                        ContextCompat.getDrawable(this, R.drawable.ic_profile_nofull_01),
                    ))
                    invalidateOptionsMenu()
                }
                R.id.navigation_my_tickets -> {
                    setCurrentFragment(returnFragmentName(it.itemId))
                    setMenuIcons(listOf(
                        ContextCompat.getDrawable(this, R.drawable.ic_loupe_nofull_01),
                        ContextCompat.getDrawable(this, R.drawable.ic_ticket_full_01),
                        ContextCompat.getDrawable(this, R.drawable.ic_profile_nofull_01),
                    ))
                    invalidateOptionsMenu()
                }
                R.id.navigation_my_profile -> {
                    setCurrentFragment(returnFragmentName(it.itemId))
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

    private fun returnFragmentName(fragmentId : Int) : Fragment {
        when (fragmentId) {
            R.id.navigation_buy_tickets -> {
                return buyTicketsFragment
            }
            R.id.navigation_my_tickets -> {
                if (!firebaseManager.checkIfUserIsSignedInToFirebase())
                    return myTicketsFragmentUnsignedIn
                else
                    return myTicketsFragmentSignedIn
            }
            else -> {
                if (!firebaseManager.checkIfUserIsSignedInToFirebase())
                    return myProfileFragmentUnsignedIn
                else
                    return myProfileFragmentSignedIn
            }
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
        setCurrentFragment(returnFragmentName(R.id.navigation_buy_tickets))
        buyTicketsMenuItem.setIcon(R.drawable.ic_loupe_full_01)
        myTicketsMenuItem.setIcon(R.drawable.ic_ticket_nofull_01)
        myProfileMenuItem.setIcon(R.drawable.ic_profile_nofull_01)
        invalidateOptionsMenu()
    }

    //Called from ProfileActivities
    fun clickOnMyProfileMenuIcon(){
        myProfileMenuItem.isChecked = true
        setCurrentFragment(returnFragmentName(R.id.navigation_my_profile))
        buyTicketsMenuItem.setIcon(R.drawable.ic_loupe_nofull_01)
        myTicketsMenuItem.setIcon(R.drawable.ic_ticket_nofull_01)
        myProfileMenuItem.setIcon(R.drawable.ic_profile_full_01)
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