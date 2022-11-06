package com.bignerdranch.android.criminalIntent.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.bignerdranch.android.criminalIntent.Crime
import com.bignerdranch.android.criminalIntent.database.CrimeDao
import com.bignerdranch.android.criminalIntent.database.CrimeTypeConverters

@Database(entities = [Crime::class], version = 1)
@TypeConverters(CrimeTypeConverters::class)
abstract class CrimeDatabase : RoomDatabase() {
    abstract fun crimeDao(): CrimeDao
}

val migration_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            "ALTER TABLE Crime ADD COLUMN suspect TEXT NOT NULL DEFAULT ''"
        )
    }
}

val migration_2_3 = object : Migration(2, 3){
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE Crime ADD COLUMN photoFileName TEXT")

    }
}
