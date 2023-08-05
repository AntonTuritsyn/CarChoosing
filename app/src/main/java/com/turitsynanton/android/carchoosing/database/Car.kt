package com.turitsynanton.android.carchoosing.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

/*      Создание БД и определение сущности (@Entity) для Room
*       @PrimaryKey для указания первичного ключа (уникальные данные)*/
@Entity
data class Car(
    @PrimaryKey val id: UUID,
    val model: String,
    val producer: String,
    val price: Int,
    val color: String,
    val carType: String,
    val year: String,
    val photoFileName: String? = null
)