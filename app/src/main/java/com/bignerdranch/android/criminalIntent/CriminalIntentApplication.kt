package com.bignerdranch.android.criminalIntent

import android.app.Application
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle


class CriminalIntentApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        CrimeRepository.initialize(this)
    }
}
