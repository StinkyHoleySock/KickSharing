package com.example.criminalintent_

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import java.io.File
import java.util.*

class RequestDetailViewModel: ViewModel() {

    private val requestRepository = RequestRepository.get()
    private val requestIdLiveData = MutableLiveData<UUID>()

    val requestLiveData: LiveData<Request?> =
        Transformations.switchMap(requestIdLiveData) { requestId ->
            requestRepository.getRequest(requestId)
        }

    fun loadRequest(requestId: UUID) {
        requestIdLiveData.value = requestId
    }

    fun saveRequest(request: Request) {
        requestRepository.updateRequest(request)
    }

}