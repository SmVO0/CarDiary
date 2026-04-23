package com.cardiary.app.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.cardiary.app.R
import com.cardiary.app.databinding.FragmentDashboardBinding
import com.cardiary.app.ui.CarViewModel
import com.cardiary.app.ui.CarViewModelFactory

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CarViewModel by activityViewModels {
        CarViewModelFactory(requireActivity().application)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Observe cars from database
        viewModel.allCars.observe(viewLifecycleOwner) { cars ->
            if (cars.isEmpty()) {
                binding.tvNoCar.text = "No car added yet"
                binding.btnAddCar.visibility = View.VISIBLE
            } else {
                val car = cars.first()
                binding.tvNoCar.text = "${car.year} ${car.make} ${car.model}\n${car.licensePlate}"
                binding.btnAddCar.visibility = View.GONE
            }
        }

        binding.btnAddCar.setOnClickListener {
            findNavController().navigate(R.id.addCarFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}