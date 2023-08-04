package com.turitsynanton.android.carchoosing

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity
data class Car(
    @PrimaryKey val id: UUID,
    val model: String,
    val producer: String,
    val price: String, // Проработать
    val color: String, // ПРЕДВАРИТЕЛЬНО
    val carType: String,
    val year: String, // Проработать
    val photoFileName: String? = null
)