package com.azadron.weatherapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.azadron.weatherapp.ui.screens.SettingsScreen
import com.azadron.weatherapp.ui.screens.WeatherScreen
import com.azadron.weatherapp.ui.theme.WeatherAppTheme
import com.azadron.weatherapp.viewmodel.ThemeViewModel
import com.azadron.weatherapp.viewmodel.WeatherViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val themeViewModel: ThemeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        setContent {
            val isDarkTheme by themeViewModel.isDarkTheme.collectAsState()
            WeatherAppTheme(darkTheme = isDarkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    WeatherAppHost(themeViewModel)
                }
            }
        }
    }

    @Composable
    fun WeatherAppHost(themeViewModel: ThemeViewModel) {
        val navController = rememberNavController()
        val viewModel: WeatherViewModel = hiltViewModel()

        // Define the location permission launcher here, inside the Composable scope
        val locationPermissionRequest = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            // Handle the result after the user responds to the permission dialog
            when {
                permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) ||
                        permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                    // Permissions granted, fetch location
                    getCurrentLocation(viewModel)
                }
                else -> {
                    Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }

        NavHost(navController = navController, startDestination = "weather") {
            composable("weather") {
                WeatherScreen(
                    // The lambda now contains the logic to check permissions and launch the request
                    onRequestLocation = {
                        if (checkPermissions()) {
                            getCurrentLocation(viewModel)
                        } else {
                            locationPermissionRequest.launch(
                                arrayOf(
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION
                                )
                            )
                        }
                    },
                    onSettingsClick = { navController.navigate("settings") },
                    viewModel = viewModel
                )
            }

            composable("settings") {
                SettingsScreen(themeViewModel) {
                    navController.popBackStack()
                }
            }
        }
    }

    private fun checkPermissions(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    @SuppressLint("MissingPermission")
    private fun getCurrentLocation(viewModel: WeatherViewModel) {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    viewModel.loadWeatherByLocation(location.latitude, location.longitude)
                } else {
                    Toast.makeText(this, "Location not found, try searching city.", Toast.LENGTH_SHORT).show()
                    viewModel.loadWeatherByCity("Kokshetau")
                }
            }
    }
}