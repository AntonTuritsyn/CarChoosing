package com.turitsynanton.android.carchoosing.repository

import android.content.Context
import androidx.room.Room
import com.turitsynanton.android.carchoosing.Car
import com.turitsynanton.android.carchoosing.database.CarDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.util.UUID

private const val DATABASE_NAME = "car-database"
class CarRepository private constructor(
    context: Context,
    private val coroutineScope: CoroutineScope = GlobalScope
){
    private val database: CarDatabase = Room.databaseBuilder(
        context.applicationContext,
        CarDatabase::class.java,
        DATABASE_NAME
    )
        .build()

    fun getCars(): Flow<List<Car>> = database.carDao().getCars()

    suspend fun getCar(id: UUID): Car = database.carDao().getCar(id)

    fun updateCar(car: Car) {
        coroutineScope.launch {
            database.carDao().updateCar(car)
        }
    }

    suspend fun addCar(car: Car) {
        database.carDao().addCar(car)
    }

    companion object {
        private var INSTANCE: CarRepository? = null

        fun initialize(context: Context) {
            if (INSTANCE == null) {
                INSTANCE = CarRepository(context)
            }
        }

        fun get(): CarRepository {
            return INSTANCE ?: throw IllegalStateException("CarRepository должен быть инициализирован")
        }
    }
}