package com.bignerdranch.android.criminalIntent

import androidx.room.PrimaryKey
import java.util.*

data class Crime(@PrimaryKey val id: UUID,
                 val title: String,
                 val date: Date,
                 val isSolved: Boolean) {
}