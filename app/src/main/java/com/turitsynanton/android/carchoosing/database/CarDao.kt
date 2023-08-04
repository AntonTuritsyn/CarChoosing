package com.turitsynanton.android.carchoosing.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.turitsynanton.android.carchoosing.Car
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
interface CarDao {
    @Query("SELECT * FROM car")
    fun getCars(): Flow<List<Car>>

    @Query("SELECT * FROM car WHERE id = (:id)")
    suspend fun getCar(id: UUID): Car

    @Update
    suspend fun updateCar(car: Car)

    @Insert
    suspend fun addCar(car: Car)
}