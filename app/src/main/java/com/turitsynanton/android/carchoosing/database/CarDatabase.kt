package com.turitsynanton.android.carchoosing.database

import androidx.room.Database
import androidx.room.RoomDatabase

/*      Данный класс предоставляет БД в приложении*/
@Database(entities = [Car::class], version = 1)
abstract class CarDatabase: RoomDatabase() {
    abstract fun carDao(): CarDao
}