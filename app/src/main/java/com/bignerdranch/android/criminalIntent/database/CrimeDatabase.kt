package com.bignerdranch.android.criminalIntent.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.bignerdranch.android.criminalIntent.Crime
import com.bignerdranch.android.criminalIntent.database.CrimeDao
import com.bignerdranch.android.criminalIntent.database.CrimeTypeConverters


@Database(entities = [Crime::class], version = 1)
@TypeConverters(CrimeTypeConverters::class)
abstract class CrimeDatabase : RoomDatabase() {
    abstract fun crimeDao(): CrimeDao
}
