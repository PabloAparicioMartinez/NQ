package com.example.nq.recyclerViewTcikets

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nq.Ticket
import kotlinx.coroutines.launch

class TicketViewModel : ViewModel() {

    lateinit var cacheManager: CacheManager

    var ticketDataList = MutableLiveData<List<Ticket>>()

    fun loadTicketData() {
        viewModelScope.launch {
            val data = cacheManager.getFromCacheTicketData()
            if (data.isEmpty()) {
                val ticketList = cacheManager.fetchTicketData()
                cacheManager.saveToCacheTicketData(data)
                ticketDataList.postValue(ticketList)
            } else {
                ticketDataList.postValue(data)
            }
        }
    }
}