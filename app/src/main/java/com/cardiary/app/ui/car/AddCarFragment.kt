package com.cardiary.app.ui.car

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.cardiary.app.data.model.Car
import com.cardiary.app.databinding.FragmentAddCarBinding
import com.cardiary.app.ui.CarViewModel
import com.cardiary.app.ui.CarViewModelFactory

class AddCarFragment : Fragment() {

    private var _binding: FragmentAddCarBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CarViewModel by activityViewModels {
        CarViewModelFactory(requireActivity().application)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddCarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSaveCar.setOnClickListener {
            saveCar()
        }
    }

    private fun saveCar() {
        val make = binding.etMake.text.toString().trim()
        val model = binding.etModel.text.toString().trim()
        val yearStr = binding.etYear.text.toString().trim()
        val licensePlate = binding.etLicensePlate.text.toString().trim()

        // Validation
        if (make.isEmpty() || model.isEmpty() || yearStr.isEmpty() || licensePlate.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val year = yearStr.toIntOrNull()
        if (year == null || year < 1900 || year > 2100) {
            Toast.makeText(requireContext(), "Please enter a valid year", Toast.LENGTH_SHORT).show()
            return
        }

        val car = Car(
            make = make,
            model = model,
            year = year,
            licensePlate = licensePlate
        )

        viewModel.insertCar(car)
        Toast.makeText(requireContext(), "Car saved successfully!", Toast.LENGTH_SHORT).show()
        findNavController().navigateUp()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}