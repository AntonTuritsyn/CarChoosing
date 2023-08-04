package com.turitsynanton.android.carchoosing.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.turitsynanton.android.carchoosing.Car
import com.turitsynanton.android.carchoosing.databinding.ListItemCarBinding
import java.util.Locale
import java.util.UUID

class CarListAdapter(
    private var cars: List<Car>,
    private val onCarClicked: (crimeId: UUID) -> Unit
): RecyclerView.Adapter<CarHolder>(), Filterable {

    private var filteredCars: List<Car> = cars

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListItemCarBinding.inflate(inflater, parent, false)
        return CarHolder(binding)
    }

    override fun onBindViewHolder(holder: CarHolder, position: Int) {
        val car = cars[position]
        holder.bind(car, onCarClicked)
    }

    override fun getItemCount() = cars.size

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val query = constraint.toString().lowercase(Locale.ROOT).trim()
                filteredCars = if (query.isEmpty()) {
                    cars
                } else {
                    cars.filter { car ->
                        car.model.lowercase(Locale.ROOT).contains(query) ||
                                car.producer.lowercase(Locale.ROOT).contains(query)
                    }
                }
                val results = FilterResults()
                results.values = filteredCars
                return results
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                @Suppress("UNCHECKED_CAST")
                filteredCars = results?.values as List<Car>
                notifyDataSetChanged()
            }
        }
    }
}