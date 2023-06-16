package com.example.nq.profileActivities

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.nq.R
import com.example.nq.recyclerViewCards.CardsAdapter
import com.example.nq.recyclerViewCards.CardsData
import com.example.nq.recyclerViewCards.CardsRepository
import kotlinx.android.synthetic.main.activity_profile_friends.*
import kotlinx.android.synthetic.main.activity_profile_payments.*
import kotlinx.android.synthetic.main.activity_profile_profile.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ProfileActivityPayments : AppCompatActivity() {

    val cardAdapter = CardsAdapter(CardsRepository.cards)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_payments)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.title = ""
            actionBar.setDisplayHomeAsUpEnabled(true)
        }

        updateCardsList()

        // BUTTON intro add card
        profilePayments_introButton.setOnClickListener() {
            setLayoutVisibilities(listOf(View.GONE, View.VISIBLE, View.GONE, View.GONE))
        }

        // BUTTON save card
        profilePayments_addCardButton.setOnClickListener() {

            if (profilePayments_cardNumberText.text?.isEmpty() == true || profilePayments_cardNumberText.text?.length != 16) {
                profilePayments_cardNumberLayout.error = "Introduce el número de la tarjeta"
                return@setOnClickListener
            }
            if (profilePayments_cardMonthText.text?.isEmpty() == true  || profilePayments_cardMonthText.text?.length != 2) {
                profilePayments_cardMonthLayout.error = "Formato incorrecto"
                return@setOnClickListener
            }
            if (profilePayments_cardYearText.text?.isEmpty() == true  || profilePayments_cardYearText.text?.length != 2) {
                profilePayments_cardYearLayout.error = "Formato incorrecto"
                return@setOnClickListener
            }
            if (profilePayments_cardCVVText.text?.isEmpty() == true   || profilePayments_cardYearText.text?.length != 2) {
                profilePayments_cardCVVLayout.error = "Formato incorrecto"
                return@setOnClickListener
            }
            if (profilePayments_cardNameText.text?.isEmpty() == true) {
                profilePayments_cardNameLayout.error = "Introduce el nombre del titular"
                return@setOnClickListener
            }

            val cardNumber = profilePayments_cardNumberText.text.toString().toLong()
            val cardMonth = profilePayments_cardMonthText.text.toString().toInt()
            val cardYear = profilePayments_cardYearText.text.toString().toInt()
            val cardCVV = profilePayments_cardCVVText.text.toString().toInt()
            val cardName = profilePayments_cardNameText.text.toString()

            val cardData = CardsData(cardNumber, cardMonth, cardYear, cardCVV, cardName)

            lifecycleScope.launch {
                saveCreditCard(cardData)
            }
        }

        // BUTTON add another card
        profilePayments_addOtherCardButton.setOnClickListener() {
            setLayoutVisibilities(listOf(View.GONE, View.VISIBLE, View.GONE, View.GONE))
        }

        // HIDE keyboard
        paymentsLayout.setOnClickListener() {
            val inputMethodManager = this.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(paymentsLayout.windowToken, 0)
        }

        // FORMAT card number
//        profilePayments_cardNumberLayout.editText?.let { editText ->
//            editText.setOnFocusChangeListener { _, hasFocus ->
//                if (!hasFocus) {
//                    if (profilePayments_cardNumberText.text?.length == 16) {
//                        val firstFourNumbers = profilePayments_cardNumberText.text?.substring(0, 4)
//                        val secondFourNumbers = profilePayments_cardNumberText.text?.substring(4, 8)
//                        val thirdFourNumbers = profilePayments_cardNumberText.text?.substring(8, 12)
//                        val fourthFourNumbers = profilePayments_cardNumberText.text?.substring(12, 16)
//
//                        profilePayments_cardNumberText.setText(
//                            "$firstFourNumbers $secondFourNumbers $thirdFourNumbers $fourthFourNumbers")
//                    }
//                }
//            }
//        }

        // FORMAT expiration date
//        profilePayments_cardExpirationLayout.editText?.let { editText ->
//            editText.setOnFocusChangeListener { _, hasFocus ->
//                if (!hasFocus) {
//                    if (profilePayments_cardExpirationText.text?.length == 4) {
//                        val firstTwoNumbers = profilePayments_cardExpirationText.text?.substring(0, 2)
//                        val secondTwoNumbers = profilePayments_cardExpirationText.text?.substring(2, 4)
//
//                        profilePayments_cardNumberText.setText(
//                            "$firstTwoNumbers/$secondTwoNumbers")
//                    }
//                }
//            }
//        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private suspend fun saveCreditCard(cardData: CardsData) {

        profilePayments_loadingLayout.visibility = View.VISIBLE

        delay(200)

        CardsRepository.cards.add(cardData)
        updateCardsList()

        setLayoutVisibilities(listOf(View.GONE, View.GONE, View.VISIBLE, View.GONE))
    }

    private fun updateCardsList() {
        if (CardsRepository.cards.isEmpty()){

            setLayoutVisibilities(listOf(View.VISIBLE, View.GONE, View.GONE, View.GONE))

        } else {

            profilePayments_recyclerView.adapter = cardAdapter
            profilePayments_recyclerView.layoutManager = LinearLayoutManager(this)

            setLayoutVisibilities(listOf(View.GONE, View.GONE, View.VISIBLE, View.GONE))
        }
    }

    private fun setLayoutVisibilities(listOfVisibilities: List<Int>) {
        profilePayments_introLayout.visibility = listOfVisibilities[0]
        profilePayments_addCardLayout.visibility = listOfVisibilities[1]
        profilePayments_myCardsLayout.visibility = listOfVisibilities[2]
        profilePayments_loadingLayout.visibility = listOfVisibilities[3]
    }
}