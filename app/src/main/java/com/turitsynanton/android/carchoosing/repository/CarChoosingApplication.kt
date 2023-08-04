package com.turitsynanton.android.carchoosing.repository

import android.app.Application
import com.turitsynanton.android.carchoosing.repository.CarRepository

class CarChoosingApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        CarRepository.initialize(this)
    }
}