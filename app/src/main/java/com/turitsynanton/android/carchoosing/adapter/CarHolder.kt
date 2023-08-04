package com.turitsynanton.android.carchoosing.adapter

import androidx.recyclerview.widget.RecyclerView
import com.turitsynanton.android.carchoosing.Car
import com.turitsynanton.android.carchoosing.databinding.ListItemCarBinding
import java.util.UUID

class CarHolder(
    private val binding: ListItemCarBinding
): RecyclerView.ViewHolder(binding.root) {

    fun bind(car: Car, onCarClicked: (carId: UUID) -> Unit) {
        binding.carModel.text = car.model
        binding.carProducer.text = car.producer
        binding.carPrice.text = car.price
        binding.root.setOnClickListener {
            onCarClicked(car.id)
        }
    }
}