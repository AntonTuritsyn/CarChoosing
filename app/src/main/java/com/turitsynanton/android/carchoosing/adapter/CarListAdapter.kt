package com.turitsynanton.android.carchoosing.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.turitsynanton.android.carchoosing.database.Car
import com.turitsynanton.android.carchoosing.databinding.ListItemCarBinding
import java.util.UUID

//      Реализация адаптера для заполнения RecyclerView
class CarListAdapter(
    private var cars: List<Car>,
    private val onCarClicked: (carId: UUID) -> Unit,
    private val onCarLongClicked: (car: Car) -> Unit
) : RecyclerView.Adapter<CarHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListItemCarBinding.inflate(inflater, parent, false)
        return CarHolder(binding, onCarLongClicked)
    }

    override fun onBindViewHolder(holder: CarHolder, position: Int) {
        val car = cars[position]
        holder.bind(car, onCarClicked)
    }

    override fun getItemCount() = cars.size

    //      Обновление отображаемого списка после применения фильтров
    fun updateCars(newCars: List<Car>) {
        cars = newCars
        notifyDataSetChanged()
    }
}