package com.cardiary.app.ui.repair

import android.app.DatePickerDialog
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.cardiary.app.databinding.FragmentRepairBinding
import com.cardiary.app.data.model.RepairCategory
import com.cardiary.app.data.model.RepairEntry
import com.cardiary.app.ui.CarViewModel
import com.cardiary.app.ui.CarViewModelFactory
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class RepairFragment : Fragment() {

    private var _binding: FragmentRepairBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CarViewModel by activityViewModels {
        CarViewModelFactory(requireActivity().application)
    }

    private var selectedDate: Long = System.currentTimeMillis()
    private var currentCarId: Long = -1L
    private var receiptImagePath: String? = null
    private var photoUri: Uri? = null

    // Camera launcher
    private val takePictureLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            binding.ivReceipt.setImageURI(photoUri)
            binding.ivReceipt.visibility = View.VISIBLE
        }
    }

    // Camera permission launcher
    private val cameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) launchCamera()
        else Toast.makeText(requireContext(), "Camera permission denied", Toast.LENGTH_SHORT).show()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRepairBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get current car
        viewModel.allCars.observe(viewLifecycleOwner) { cars ->
            if (cars.isNotEmpty()) currentCarId = cars.first().id
        }

        // Set up category dropdown
        val categories = RepairCategory.entries.map { it.name.replace("_", " ") }
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, categories)
        binding.actvCategory.setAdapter(adapter)

        // Set default date
        updateDateDisplay()

        // Date picker
        binding.etDate.setOnClickListener {
            showDatePicker()
        }

        // Camera
        binding.btnTakePhoto.setOnClickListener {
            checkCameraPermission()
        }

        // Save
        binding.btnSaveRepair.setOnClickListener {
            saveRepairEntry()
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            requireContext(),
            { _, year, month, day ->
                calendar.set(year, month, day)
                selectedDate = calendar.timeInMillis
                updateDateDisplay()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun updateDateDisplay() {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        binding.etDate.setText(sdf.format(Date(selectedDate)))
    }

    private fun checkCameraPermission() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.CAMERA
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED -> launchCamera()
            else -> cameraPermissionLauncher.launch(android.Manifest.permission.CAMERA)
        }
    }

    private fun launchCamera() {
        val photoFile = File.createTempFile(
            "receipt_${System.currentTimeMillis()}",
            ".jpg",
            requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        )
        receiptImagePath = photoFile.absolutePath
        photoUri = FileProvider.getUriForFile(
            requireContext(),
            "${requireContext().packageName}.fileprovider",
            photoFile
        )
        takePictureLauncher.launch(photoUri)
    }

    private fun saveRepairEntry() {
        if (currentCarId == -1L) {
            Toast.makeText(requireContext(), "Please add a car first!", Toast.LENGTH_SHORT).show()
            return
        }

        val categoryText = binding.actvCategory.text.toString().trim()
        val description = binding.etDescription.text.toString().trim()
        val cost = binding.etCost.text.toString().toDoubleOrNull()
        val odometer = binding.etOdometer.text.toString().toIntOrNull()
        val notes = binding.etNotes.text.toString().trim()

        if (categoryText.isEmpty() || description.isEmpty() || cost == null || odometer == null) {
            Toast.makeText(requireContext(), "Please fill in all required fields", Toast.LENGTH_SHORT).show()
            return
        }

        val category = try {
            RepairCategory.valueOf(categoryText.replace(" ", "_"))
        } catch (e: IllegalArgumentException) {
            Toast.makeText(requireContext(), "Please select a valid category", Toast.LENGTH_SHORT).show()
            return
        }

        val repairEntry = RepairEntry(
            carId = currentCarId,
            date = selectedDate,
            category = category,
            description = description,
            cost = cost,
            odometer = odometer,
            receiptImagePath = receiptImagePath,
            notes = notes
        )

        viewModel.insertRepairEntry(repairEntry)
        Toast.makeText(requireContext(), "Repair entry saved!", Toast.LENGTH_SHORT).show()
        clearForm()
    }

    private fun clearForm() {
        binding.actvCategory.text?.clear()
        binding.etDescription.text?.clear()
        binding.etCost.text?.clear()
        binding.etOdometer.text?.clear()
        binding.etNotes.text?.clear()
        binding.ivReceipt.visibility = View.GONE
        receiptImagePath = null
        updateDateDisplay()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}