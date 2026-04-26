package com.cardiary.app.ui.fuel

import android.Manifest
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.cardiary.app.data.model.FuelEntry
import com.cardiary.app.databinding.FragmentFuelBinding
import com.cardiary.app.ui.CarViewModel
import com.cardiary.app.ui.CarViewModelFactory
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class FuelFragment : Fragment() {

    private var _binding: FragmentFuelBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CarViewModel by activityViewModels {
        CarViewModelFactory(requireActivity().application)
    }

    private var selectedDate: Long = System.currentTimeMillis()
    private var currentCarId: Long = -1L
    private var currentLatitude: Double? = null
    private var currentLongitude: Double? = null
    private var receiptImagePath: String? = null
    private var photoUri: Uri? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    // Camera launcher
    private val takePictureLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            binding.ivReceipt.setImageURI(photoUri)
            binding.ivReceipt.visibility = View.VISIBLE
        }
    }

    // Location permission launcher

    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) getLocation()
        else Toast.makeText(requireContext(), "Location permission denied", Toast.LENGTH_SHORT).show()
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
        _binding = FragmentFuelBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        viewModel.allCars.observe(viewLifecycleOwner) { cars ->
            if (cars.isNotEmpty()) currentCarId = cars.first().id
        }

        updateDateDisplay()
        binding.etDate.setOnClickListener {
            showDatePicker()
        }

        // Auto calculate total price
        val priceWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) { calculateTotal() }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }
        binding.etLiters.addTextChangedListener(priceWatcher)
        binding.etPricePerLiter.addTextChangedListener(priceWatcher)

        // GPS
        binding.btnGetLocation.setOnClickListener {
            checkLocationPermission()
        }

        // Camera
        binding.btnTakePhoto.setOnClickListener {
            checkCameraPermission()
        }

        // Save
        binding.btnSaveFuel.setOnClickListener {
            saveFuelEntry()
        }
    }

    @SuppressLint("DefaultLocale")
    private fun calculateTotal() {
        val liters = binding.etLiters.text.toString().toDoubleOrNull() ?: return
        val price = binding.etPricePerLiter.text.toString().toDoubleOrNull() ?: return
        val total = liters * price
        binding.etTotalPrice.setText(String.format("%.2f", total))
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

    private fun checkLocationPermission() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> getLocation()
            else -> locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    @SuppressLint("MissingPermission", "SetTextI18n")    private fun getLocation() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    currentLatitude = location.latitude
                    currentLongitude = location.longitude
                    binding.tvLocationStatus.text =
                        "%.4f, %.4f".format(location.latitude, location.longitude)
                } else {
                    Toast.makeText(requireContext(), "Could not get location", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun checkCameraPermission() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> launchCamera()
            else -> cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
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

    private fun saveFuelEntry() {
        if (currentCarId == -1L) {
            Toast.makeText(requireContext(), "Please add a car first!", Toast.LENGTH_SHORT).show()
            return
        }

        val liters = binding.etLiters.text.toString().toDoubleOrNull()
        val pricePerLiter = binding.etPricePerLiter.text.toString().toDoubleOrNull()
        val totalPrice = binding.etTotalPrice.text.toString().toDoubleOrNull()
        val odometer = binding.etOdometer.text.toString().toIntOrNull()
        val notes = binding.etNotes.text.toString().trim()

        if (liters == null || pricePerLiter == null || odometer == null) {
            Toast.makeText(requireContext(), "Please fill in all required fields", Toast.LENGTH_SHORT).show()
            return
        }

        val fuelEntry = FuelEntry(
            carId = currentCarId,
            date = selectedDate,
            liters = liters,
            pricePerLiter = pricePerLiter,
            totalPrice = totalPrice ?: (liters * pricePerLiter),
            odometer = odometer,
            latitude = currentLatitude,
            longitude = currentLongitude,
            receiptImagePath = receiptImagePath,
            notes = notes
        )

        viewModel.insertFuelEntry(fuelEntry)
        Toast.makeText(requireContext(), "Fuel entry saved!", Toast.LENGTH_SHORT).show()
        clearForm()
    }

    @SuppressLint("SetTextI18n")
    private fun clearForm() {
        binding.etLiters.text?.clear()
        binding.etPricePerLiter.text?.clear()
        binding.etTotalPrice.text?.clear()
        binding.etOdometer.text?.clear()
        binding.etNotes.text?.clear()
        binding.ivReceipt.visibility = View.GONE
        binding.tvLocationStatus.text = "No location"
        currentLatitude = null
        currentLongitude = null
        receiptImagePath = null
        updateDateDisplay()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}