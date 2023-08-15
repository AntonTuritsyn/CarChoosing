package com.turitsynanton.android.carchoosing.carlist

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.doOnLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.turitsynanton.android.carchoosing.database.Car
import com.turitsynanton.android.carchoosing.R
import com.turitsynanton.android.carchoosing.adapter.CarListAdapter
import com.turitsynanton.android.carchoosing.databinding.FragmentCarListBinding
import com.turitsynanton.android.carchoosing.databinding.ListItemCarBinding
import com.turitsynanton.android.carchoosing.getScaledBitmap
import kotlinx.coroutines.launch
import java.io.File
import java.util.UUID

//      Фрагмет для вывода списка
class CarListFragment : Fragment() {

    //    Безопасное создание и доступ к фрагменту
    private var _binding: FragmentCarListBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "Ошибка вывода View"
        }

    //      Инициализация ViewModel
    private val carListViewModel: CarListViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCarListBinding.inflate(inflater, container, false)
//        Создание RecyclerView в виде простого вертикального списка
        binding.carRecyclerView.layoutManager = LinearLayoutManager(context)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        /*      С помощью функции repeatOnLifecycle(...) выполняется сопрограммный код, пока фрагмент находится в заданном состоянии ЖЦ.
                Например, этот код выполняется только тогда, когда фрагмент находится в запущенном или возобновленном состоянии.*/
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                carListViewModel.cars.collect { cars ->
                    binding.carRecyclerView.adapter = CarListAdapter(cars,
                        { carId ->
//                            Переход в другой фрагмент при нажатии с помощью Navigation
                            findNavController().navigate(
                                CarListFragmentDirections.showCarDetail(carId)
                            )
                        },
                        { carId ->
//                             Вызов функции удаления элемента
                            showDeleteConfirmationDialog(carId)
                        })
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    //      Добавление меню в Фрагмент (устаревший способ)
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_car_list, menu)

    }

    //      Назначение действий на элементы выпадающего списка меню (тоже устарело)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.new_car -> {
                showNewCar()
                true
            }

            R.id.sort -> {
                carListViewModel.toggleSortByPrice()
                true
            }

            R.id.filter_model -> {
                showFilterDialog(CarListViewModel.FilterType.MODEL)
                true
            }

            R.id.filter_producer -> {
                showFilterDialog(CarListViewModel.FilterType.PRODUCER)
                true
            }

            R.id.reset_filter -> {
                carListViewModel.applyFilter(CarListViewModel.FilterType.MODEL, "")
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    //      Переход в другой фрагмент при создании нового авто (для заполнения информации)
    private fun showNewCar() {
        viewLifecycleOwner.lifecycleScope.launch {
            val newCar = Car(
                id = UUID.randomUUID(),
                model = "",
                producer = "",
                price = 0,
                color = "",
                carType = "",
                year = "",
            )
            carListViewModel.addCar(newCar)
            findNavController().navigate(CarListFragmentDirections.showCarDetail(newCar.id))
        }
    }

    //      Функция для реализации фильтра в списке (Логика в ViewModel)
    private fun showFilterDialog(filterType: CarListViewModel.FilterType) {
        val filterEditText = EditText(requireContext())
        AlertDialog.Builder(requireContext())
            .setTitle("Введите значение")
            .setView(filterEditText)
            .setPositiveButton("Ок") { _, _ ->
                carListViewModel.applyFilter(filterType, filterEditText.text.toString())
                viewLifecycleOwner.lifecycleScope.launch {
                    carListViewModel.filteredCars.collect { filteredCars ->
                        (binding.carRecyclerView.adapter as? CarListAdapter)?.updateCars(
                            filteredCars
                        )
                    }
                }
            }
            .setNegativeButton("Отмена", null)
            .setNeutralButton("Сбросить") { _, _ ->
                carListViewModel.resetFilter()
            }
            .show()
    }

    //      Вызов диалога для подтверждения удаления
    private fun showDeleteConfirmationDialog(car: Car) {
        AlertDialog.Builder(requireContext())
            .setTitle("Удаление")
            .setMessage("Вы действительно хотите удалить этот автомобиль?")
            .setPositiveButton("Удалить") { _, _ ->
                // Вызываем метод удаления из ViewModel
                carListViewModel.deleteCar(car)
            }
            .setNegativeButton("Отмена", null)
            .show()
    }
}