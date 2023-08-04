package com.turitsynanton.android.carchoosing.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.turitsynanton.android.carchoosing.Car

@Database(entities = [Car::class], version = 1)
abstract class CarDatabase: RoomDatabase() {

    abstract fun carDao(): CarDao
}