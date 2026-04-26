package com.cardiary.app.ui.dashboard

import android.annotation.SuppressLint
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

    private var totalFuelCost = 0.0
    private var totalRepairCost = 0.0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnAddCar.setOnClickListener {
            findNavController().navigate(R.id.addCarFragment)
        }

        viewModel.allCars.observe(viewLifecycleOwner) { cars ->
            if (cars.isEmpty()) {
                binding.tvCarInfo.text = "No car added yet"
                binding.tvLicensePlate.visibility = View.GONE
                binding.btnAddCar.visibility = View.VISIBLE
                return@observe
            }

            val car = cars.first()
            binding.tvCarInfo.text = "${car.year} ${car.make} ${car.model}"
            binding.tvLicensePlate.text = car.licensePlate
            binding.tvLicensePlate.visibility = View.VISIBLE
            binding.btnAddCar.visibility = View.GONE

            val carId = car.id

            // Total fuel cost
            viewModel.getTotalFuelCost(carId).observe(viewLifecycleOwner) { cost ->
                totalFuelCost = cost ?: 0.0
                binding.tvTotalFuel.text = "%.2f EUR".format(totalFuelCost)
                updateTotalCost()
            }

            // Total repair cost
            viewModel.getTotalRepairCost(carId).observe(viewLifecycleOwner) { cost ->
                totalRepairCost = cost ?: 0.0
                binding.tvTotalRepairs.text = "%.2f EUR".format(totalRepairCost)
                updateTotalCost()
            }

            // Average consumption
            viewModel.getAverageConsumption(carId).observe(viewLifecycleOwner) { avg ->
                if (avg != null && avg > 0) {
                    binding.tvAvgConsumption.text = "%.2f L/100km".format(avg)
                } else {
                    binding.tvAvgConsumption.text = "-- L/100km"
                }
            }

            // Total entries
            viewModel.getFuelEntries(carId).observe(viewLifecycleOwner) { fuelEntries ->
                viewModel.getRepairEntries(carId).observe(viewLifecycleOwner) { repairEntries ->
                    val total = (fuelEntries?.size ?: 0) + (repairEntries?.size ?: 0)
                    binding.tvTotalEntries.text = total.toString()
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateTotalCost() {
        binding.tvTotalCost.text = "%.2f EUR".format(totalFuelCost + totalRepairCost)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}