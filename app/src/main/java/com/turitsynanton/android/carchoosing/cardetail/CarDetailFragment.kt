package com.turitsynanton.android.carchoosing.cardetail

import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.core.view.doOnLayout
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.turitsynanton.android.carchoosing.database.Car
import com.turitsynanton.android.carchoosing.databinding.FragmentCarDetailsBinding
import com.turitsynanton.android.carchoosing.getScaledBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.util.Date

class CarDetailFragment : Fragment() {

    //      Вот тут я пока плаваю. Но вообще инициализация Navigation и ViewModel
    private val args: CarDetailFragmentArgs by navArgs()
    private val carDetailViewModel: CarDetailViewModel by viewModels {
        CarDetailViewModelFactory(args.carId)
    }

    //      Пустая переменная для названия фото
    private var photoName: String? = null

    //      Реализация запроса для использования камеры
    private val takePhoto = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { didTakePhoto ->
        if (didTakePhoto && photoName != null) {
            carDetailViewModel.updateCar { oldCar ->
                oldCar.copy(photoFileName = photoName)
            }
        }
    }

    //      Реализация запроса для использования галереи
    private val useGallery = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            try {
                val fileName = getFileNameFromUri(uri)
                photoName = fileName
                binding.carPicture.setImageURI(uri)
                /*carDetailViewModel.updateCar { oldCar ->
                    oldCar.copy(photoFileName = photoName)
                }*/
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    /*  создание переменной, которая в аксессоре get() проверяет, является ли _binding null.
        если нет, то возвращает значение _binding, если null, то выбрасывает IllegalStateException с сообщением
        Таким образом обеспечивается безопасный доступ к свойству binding*/
    private var _binding: FragmentCarDetailsBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "Ошибка вывода View"
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCarDetailsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//      Заполнение и перезапись в БД параметров авто
        binding.apply {
            carPriceEdit.doOnTextChanged { text, _, _, _ ->
                val txt = text.toString()
                val parsed = txt.toIntOrNull()
                if (parsed != null) {
                    carDetailViewModel.updateCar { oldCar ->
                        oldCar.copy(price = parsed)
                    }
                } else {
                    carDetailViewModel.updateCar { oldCar ->
                        oldCar.copy(price = 0)
                    }
                    Toast.makeText(context, "Недопустимое значение", Toast.LENGTH_LONG).show()
                }
            }
            carModelEdit.doOnTextChanged { text, _, _, _ ->
                carDetailViewModel.updateCar { oldCar ->
                    oldCar.copy(model = text.toString())
                }
            }
            carProducerEdit.doOnTextChanged { text, _, _, _ ->
                carDetailViewModel.updateCar { oldCar ->
                    oldCar.copy(producer = text.toString())
                }
            }
            carTypeEdit.doOnTextChanged { text, _, _, _ ->
                carDetailViewModel.updateCar { oldCar ->
                    oldCar.copy(carType = text.toString())
                }
            }
            carYearEdit.doOnTextChanged { text, _, _, _ ->
                carDetailViewModel.updateCar { oldCar ->
                    oldCar.copy(year = text.toString())
                }
            }
            carColorEdit.doOnTextChanged { text, _, _, _ ->
                carDetailViewModel.updateCar { oldCar ->
                    oldCar.copy(color = text.toString())
                }
            }
//            загрузка изображения из галереи
            galleryButton.setOnClickListener {
                useGallery.launch("image/*")
            }
//            Использоваие камеры
            cameraButton.setOnClickListener {
                photoName = "IMG_${Date()}.JPG"
                val photoFile = File(requireContext().applicationContext.filesDir, photoName)
                val photoUri = FileProvider.getUriForFile(
                    requireContext(),
                    "com.turitsynanton.android.carchoosing.fileprovider",
                    photoFile
                )
                takePhoto.launch(photoUri)
            }
            val captureImageIntent =
                takePhoto.contract.createIntent(requireContext(), Uri.parse(""))
            cameraButton.isEnabled = canResolveIntent(captureImageIntent)

        }
//      Обновление UI
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                carDetailViewModel.car.collect { car ->
                    car?.let { updateUi(it) }
                }
            }

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    //      Логика функции обновления UI
    private fun updateUi(car: Car) {
        binding.apply {
            if (carPriceEdit.text.toString() != car.price.toString()) {
                carPriceEdit.setText(car.price.toString())
            }
            if (carModelEdit.text.toString() != car.model) {
                carModelEdit.setText(car.model)
            }
            if (carProducerEdit.text.toString() != car.producer) {
                carProducerEdit.setText(car.producer)
            }
            if (carTypeEdit.text.toString() != car.carType) {
                carTypeEdit.setText(car.carType)
            }
            if (carYearEdit.text.toString() != car.year) {
                carYearEdit.setText(car.year)
            }
            if (carColorEdit.text.toString() != car.color) {
                carColorEdit.setText(car.color)
            }
            if (photoNameUri.text.toString() != car.photoFileName) {
                photoNameUri.setText(car.photoFileName)
            }
            updatePhoto(car.photoFileName)
        }
    }

    //      проверка на доступность камеры на телефоне
    private fun canResolveIntent(intent: Intent): Boolean {
        val packageManager: PackageManager = requireActivity().packageManager
        val resolveActivity: ResolveInfo? = packageManager.resolveActivity(
            intent,
            PackageManager.MATCH_DEFAULT_ONLY
        )
        return resolveActivity != null
    }

    //      Логика обновления и прикрепления ФОТО в приложении
    private fun updatePhoto(photoFileName: String?) {
        if (binding.carPicture.tag != photoFileName) {
            val photoFile = photoFileName?.let {
                File(requireContext().applicationContext.filesDir, it)
            }
            if (photoFile?.exists() == true) {
                binding.carPicture.background = null
                binding.carPicture.doOnLayout { measuredView ->
                    val scaledBitmap = getScaledBitmap(
                        photoFile.path,
                        measuredView.height,
                        measuredView.width
                    )
                    binding.carPicture.setImageBitmap(scaledBitmap)
                    binding.carPicture.tag = photoFileName
                }
            } else {
                binding.carPicture.setImageBitmap(null)
                binding.carPicture.tag = null
            }
        }
    }

    //          Функция для получения названия картинки из галереи (не помогло)
    private fun getFileNameFromUri(uri: Uri): String {
        val cursor = context?.contentResolver?.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val displayNameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (displayNameIndex != -1) {
                    val pictureName = it.getString(displayNameIndex)
                    if (!pictureName.isNullOrBlank()) {
                        return pictureName
                    }
                }
            }
        }
        return "unknown_filename"
    }
    /*fun updateCarInDatabase(updatedCar: Car) {
        viewModelScope.launch(Dispatchers.IO) {
            carRepository.updateCar(updatedCar)
        }
    }*/
}