package com.example.criminalintent_

import android.app.Application

class CriminalIntentApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        RequestRepository.initialize(this)
    }
}