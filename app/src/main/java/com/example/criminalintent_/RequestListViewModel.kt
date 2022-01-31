package com.example.criminalintent_

import android.view.MenuItem
import androidx.lifecycle.ViewModel

class RequestListViewModel: ViewModel() {

    private val requestRepository = RequestRepository.get()
    val requestListLiveData = requestRepository.getRequests()

    fun addRequest(request: Request) {
        requestRepository.addRequest(request)
    }
}