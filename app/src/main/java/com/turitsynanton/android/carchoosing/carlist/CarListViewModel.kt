package com.turitsynanton.android.carchoosing.carlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.turitsynanton.android.carchoosing.Car
import com.turitsynanton.android.carchoosing.repository.CarRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class CarListViewModel: ViewModel() {
    private val carRepository = CarRepository.get()

    private var sortByPriceAsc: Boolean = true
    private val _cars: MutableStateFlow<List<Car>> = MutableStateFlow(emptyList())
    val cars: StateFlow<List<Car>>
        get() = _cars.asStateFlow()

    init {
        viewModelScope.launch {
            carRepository.getCars().collect() {
                _cars.value = it
            }
        }
    }

    suspend fun addCar(car: Car) {
        carRepository.addCar(car)
    }

    fun toggleSortByPrice() {
        sortByPriceAsc = !sortByPriceAsc
        val sortedCars = _cars.value.sortedBy { it.price }
        _cars.value = if (sortByPriceAsc) sortedCars else sortedCars.reversed()
    }
    fun setSortByPrice(sortByPrice: Boolean) {
        sortByPriceAsc = sortByPrice
    }
}