package com.turitsynanton.android.carchoosing.cardetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.turitsynanton.android.carchoosing.Car
import com.turitsynanton.android.carchoosing.repository.CarRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

class CarDetailViewModel(carId: UUID): ViewModel() {

    private val carRepository = CarRepository.get()
    private val _car: MutableStateFlow<Car?> = MutableStateFlow(null)
    val car: StateFlow<Car?> = _car.asStateFlow()

    init {
        viewModelScope.launch {
            _car.value = carRepository.getCar(carId)
        }
    }

    fun updateCar(onUpdate: (Car) -> Car) {
        _car.update { oldCar ->
            oldCar?.let { onUpdate(it) }
        }
    }

    override fun onCleared() {
        super.onCleared()
        car.value?.let {
            carRepository.updateCar(it)
        }
    }
}
