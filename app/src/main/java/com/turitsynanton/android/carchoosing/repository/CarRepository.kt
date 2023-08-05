package com.turitsynanton.android.carchoosing.repository

import android.content.Context
import androidx.room.Room
import com.turitsynanton.android.carchoosing.database.Car
import com.turitsynanton.android.carchoosing.database.CarDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.util.UUID

private const val CAR_DATABASE = "car_database"

/*      Класс репозитория ( с логикой для доступа к БД
*       UI запрашивает все данные отсюда*/
class CarRepository private constructor(
    context: Context,
    private val coroutineScope: CoroutineScope = GlobalScope
) {
    //    Создание постоянной БД и ссылки для её повторного использования
    private val database: CarDatabase = Room.databaseBuilder(
        context.applicationContext,
        CarDatabase::class.java,
        CAR_DATABASE
    )
        .build()

    //      Функции для доступа к данным из БД (реализованы не в основном потоке
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

    suspend fun deleteCar(car: Car) {
        database.carDao().deleteCar(car)
    }

    //      Реализация репозитория
    companion object {
        private var INSTANCE: CarRepository? = null

        //      Инициализация
        fun initialize(context: Context) {
            if (INSTANCE == null) {
                INSTANCE = CarRepository(context)
            }
        }

        //      Получение доступа
        fun get(): CarRepository {
            return INSTANCE
                ?: throw IllegalStateException("CarRepository должен быть инициализирован")
        }
    }
}