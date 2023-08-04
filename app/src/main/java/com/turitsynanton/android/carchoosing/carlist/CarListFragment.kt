package com.turitsynanton.android.carchoosing.carlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.view.doOnLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.turitsynanton.android.carchoosing.Car
import com.turitsynanton.android.carchoosing.R
import com.turitsynanton.android.carchoosing.adapter.CarListAdapter
import com.turitsynanton.android.carchoosing.databinding.FragmentCarListBinding
import com.turitsynanton.android.carchoosing.getScaledBitmap
import kotlinx.coroutines.launch
import java.io.File
import java.util.UUID

class CarListFragment : Fragment() {

    private var _binding: FragmentCarListBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "Ошибка вывода View"
        }

    private val carListViewModel: CarListViewModel by viewModels()

    private var searchView: SearchView? = null

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
        binding.carRecyclerView.layoutManager = GridLayoutManager(context, 2)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//      Правильно
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                carListViewModel.cars.collect {cars ->
                    binding.carRecyclerView.adapter = CarListAdapter(cars) { carId ->

                        findNavController().navigate(CarListFragmentDirections.showCarDetail(carId))
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_car_list, menu)
    }

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
            else -> super.onOptionsItemSelected(item)
        }
    }
    private fun showNewCar() {
        viewLifecycleOwner.lifecycleScope.launch {
            val newCar = Car(
            id = UUID.randomUUID(),
            model = "",
            producer = "",
            price = "",
            color = "",
            carType ="",
            year = "",
            )
            carListViewModel.addCar(newCar)
            findNavController().navigate(CarListFragmentDirections.showCarDetail(newCar.id))
        }
    }
}