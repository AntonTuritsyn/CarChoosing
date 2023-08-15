package com.turitsynanton.android.carchoosing.adapter

import android.content.Context
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.doOnLayout
import androidx.recyclerview.widget.RecyclerView
import com.turitsynanton.android.carchoosing.database.Car
import com.turitsynanton.android.carchoosing.databinding.ListItemCarBinding
import com.turitsynanton.android.carchoosing.getScaledBitmap
import java.io.File
import java.util.UUID

//      Контейнер представления для RecyclerView с хранением ссылок на элементы
class CarHolder(
    private val binding: ListItemCarBinding,
    private val onCarLongClicked: (car: Car) -> Unit,
    private val context: Context
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
        updatePhoto(car.photoFileName)
    }

    private fun updatePhoto(photoFileName: String?) {
        if (binding.picMini.tag != photoFileName) {
            val photoFile = photoFileName?.let {
                File(context.applicationContext.filesDir, it)
            }
            if (photoFile?.exists() == true) {
                binding.picMini.background = null
                binding.picMini.doOnLayout { measuredView ->
                    val scaledBitmap = getScaledBitmap(
                        photoFile.path,
                        measuredView.height,
                        measuredView.width
                    )
                    binding.picMini.setImageBitmap(scaledBitmap)
                    binding.picMini.tag = photoFileName
                }
            } else {
                binding.picMini.setImageBitmap(null)
                binding.picMini.tag = null
            }
        }
    }
}