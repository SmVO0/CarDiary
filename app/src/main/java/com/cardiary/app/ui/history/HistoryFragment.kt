package com.cardiary.app.ui.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.cardiary.app.databinding.FragmentHistoryBinding
import com.cardiary.app.data.model.HistoryItem
import com.cardiary.app.ui.CarViewModel
import com.cardiary.app.ui.CarViewModelFactory

class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CarViewModel by activityViewModels {
        CarViewModelFactory(requireActivity().application)
    }

    private lateinit var adapter: HistoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = HistoryAdapter()
        binding.rvHistory.layoutManager = LinearLayoutManager(requireContext())
        binding.rvHistory.adapter = adapter

        viewModel.allCars.observe(viewLifecycleOwner) { cars ->
            if (cars.isEmpty()) {
                showEmpty()
                return@observe
            }

            val carId = cars.first().id

            viewModel.getFuelEntries(carId).observe(viewLifecycleOwner) { fuelEntries ->
                viewModel.getRepairEntries(carId).observe(viewLifecycleOwner) { repairEntries ->

                    val combined = mutableListOf<HistoryItem>()
                    fuelEntries.forEach { combined.add(HistoryItem.Fuel(it)) }
                    repairEntries.forEach { combined.add(HistoryItem.Repair(it)) }
                    combined.sortByDescending { it.getDate() }

                    if (combined.isEmpty()) {
                        showEmpty()
                    } else {
                        binding.tvNoEntries.visibility = View.GONE
                        binding.rvHistory.visibility = View.VISIBLE
                        adapter.submitList(combined)
                    }
                }
            }
        }
    }

    private fun showEmpty() {
        binding.tvNoEntries.visibility = View.VISIBLE
        binding.rvHistory.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}