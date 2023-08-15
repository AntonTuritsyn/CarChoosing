package com.turitsynanton.android.carchoosing.carlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.turitsynanton.android.carchoosing.database.Car
import com.turitsynanton.android.carchoosing.repository.CarRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class CarListViewModel : ViewModel() {
    //    Получение экземпляра репозитория
    private val carRepository = CarRepository.get()

    //    Определение переменной для сортировки
    private var sortByPriceAsc: Boolean = true

    //    Безопасное создание и доступ к списку авто
    private val _cars: MutableStateFlow<List<Car>> = MutableStateFlow(emptyList())
    val cars: StateFlow<List<Car>>
        get() = _cars.asStateFlow()


    //      Безопасное создание и доступ к List для хранения отфильтрованного списка авто
    private val _filteredCars: MutableStateFlow<List<Car>> = MutableStateFlow(emptyList())
    val filteredCars: StateFlow<List<Car>>
        get() = _filteredCars.asStateFlow()

    //      Создание переменных для настройки логики фильтра
    private var filterText: String = ""
    private var filterType: FilterType = FilterType.MODEL // Изначально фильтруем по модели

    //    Класс для перечисления типов фильтра
    enum class FilterType {
        MODEL, PRODUCER
    }

    //      Функция для применения фильтра
    fun applyFilter(filterType: FilterType, filterText: String) {
        this.filterType = filterType
        this.filterText = filterText
        filterCars()
    }

    //      Логика работы фильтра
    private fun filterCars() {
        val filteredList = if (filterText.isNotBlank()) {
            when (filterType) {
                FilterType.MODEL -> _cars.value.filter {
                    it.model.contains(
                        filterText,
                        ignoreCase = true
                    )
                }

                FilterType.PRODUCER -> _cars.value.filter {
                    it.producer.contains(
                        filterText,
                        ignoreCase = true
                    )
                }
            }
        } else {
            _cars.value
        }
        _filteredCars.value = filteredList
    }

    //      Функция для сброса фильтра
    fun resetFilter() {
        filterText = ""
        filterCars()
    }

    //      Подключение репозитория и его функций
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

    fun deleteCar(car: Car) {
        viewModelScope.launch {
            carRepository.deleteCar(car)
        }
    }

    //      Логика сортировки по стоимости
    fun toggleSortByPrice() {
        sortByPriceAsc = !sortByPriceAsc
        val sortedCars = _cars.value.sortedBy { it.price }
        _cars.value = if (sortByPriceAsc) sortedCars else sortedCars.reversed()
    }

}