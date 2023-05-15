package com.example.nq

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.nq.firebase.FirebaseManager
import kotlinx.android.synthetic.main.activity_start.*

class StartActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        installSplashScreen()
        Thread.sleep(2_000)

        setContentView(R.layout.activity_start)
        FirebaseManager().signOutUser()

        //BUTTONS
        startActivity_signInButton.setOnClickListener(){
            Intent(this,SignInActivity::class.java).also{
                startActivity(it)
            }
        }

        startActivity_noSignInButton.setOnClickListener(){
            Intent(this, MainActivity::class.java).also{
                startActivity(it)
            }
        }
    }
}