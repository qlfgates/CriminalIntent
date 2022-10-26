package com.bignerdranch.android.criminalIntent

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class CriminalIntentApplication : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        CrimeRepository.initialize(this)
//        setContentView(R.layout.activity_criminal_intent_application)
    }
}