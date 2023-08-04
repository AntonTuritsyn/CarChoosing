package com.turitsynanton.android.carchoosing.cardetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.util.UUID

class CarDetailViewModelFactory(
    private val carId: UUID
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CarDetailViewModel(carId) as T
    }
}