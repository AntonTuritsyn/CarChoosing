package com.turitsynanton.android.carchoosing.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import java.util.UUID

/*      Объект доступа к данным в Room*/
@Dao
interface CarDao {
//    Вывод всех столбцов для всех строк таблицы
    @Query("SELECT * FROM car")
    fun getCars(): Flow<List<Car>>
//    Вывод всех столбцов только из строки с нужным id
    @Query("SELECT * FROM car WHERE id = (:id)")
    suspend fun getCar(id: UUID): Car
//      Запись новой информации об авто в БД
    @Update
    suspend fun updateCar(car: Car)
//      Добавление авто в БД
    @Insert
    suspend fun addCar(car: Car)
//      Удаление авто из БД
    @Delete
    suspend fun deleteCar(car: Car)
}