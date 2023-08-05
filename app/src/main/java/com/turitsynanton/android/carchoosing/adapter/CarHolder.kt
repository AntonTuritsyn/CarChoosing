package com.turitsynanton.android.carchoosing.adapter

import androidx.recyclerview.widget.RecyclerView
import com.turitsynanton.android.carchoosing.database.Car
import com.turitsynanton.android.carchoosing.databinding.ListItemCarBinding
import java.util.UUID

//      Контейнер представления для RecyclerView с хранением ссылок на элементы
class CarHolder(
    private val binding: ListItemCarBinding,
    private val onCarLongClicked: (car: Car) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    /*      Ссылки на элементы, которые должны отображаться на первом экране
    *       И слушатели нажатия и долгого нажатия на элементы*/
    fun bind(car: Car, onCarClicked: (carId: UUID) -> Unit) {
        binding.carModel.text = car.model
        binding.carProducer.text = car.producer
        binding.carPrice.text = car.price.toString()
        binding.root.setOnClickListener {
            onCarClicked(car.id)
        }
        binding.root.setOnLongClickListener {
            onCarLongClicked(car)
            true
        }
    }
}