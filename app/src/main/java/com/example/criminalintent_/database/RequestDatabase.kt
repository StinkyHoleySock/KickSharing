package com.example.criminalintent_.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.criminalintent_.Request

@Database(entities = [Request::class], version=2)
@TypeConverters(TypeClassConverters::class)

abstract class RequestDatabase: RoomDatabase() {

    abstract fun requestDao(): RequestDao
}

val migration_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            "ALTER TABLE Request ADD COLUMN suspect TEXT NOT NULL DEFAULT ''"
        )
    }

}