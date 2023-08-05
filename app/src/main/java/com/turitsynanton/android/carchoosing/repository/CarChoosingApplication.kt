package com.turitsynanton.android.carchoosing.repository

import android.app.Application
import com.turitsynanton.android.carchoosing.repository.CarRepository

/*      Класс для инициализации репозитория
*       Это необходимо, потому что ЖЦ репозитория == ЖЦ приложения*/
class CarChoosingApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        CarRepository.initialize(this)
    }
}