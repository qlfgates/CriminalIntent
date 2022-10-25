package com.bignerdranch.android.criminalIntent

import androidx.lifecycle.ViewModel
import java.util.*

class CrimeListViewModel: ViewModel() {

    val crimes = mutableListOf<Crime>()

    init {
        for ( i in 0 until 10){
            val crime = Crime(
                id = UUID.randomUUID(),
                title = "Crime #$i",
                date = Date(),
                isSolved = i%2 == 0
            )

            crimes += crime
        }
    }
}