package com.bignerdranch.android.criminalIntent.database

import androidx.room.Dao
import androidx.room.Query
import com.bignerdranch.android.criminalIntent.Crime
import java.util.UUID


@Dao
interface CrimeDao {
    @Query("SELECT * FROM crime")
    suspend fun getCrimes(): List<Crime>

    @Query("SELECT * FROM crime WHERE id=(:id)")
    suspend fun getCrimes(id: UUID): Crime
}