package com.azadron.weatherapp.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.azadron.weatherapp.R
import com.azadron.weatherapp.data.ForecastItem
import com.azadron.weatherapp.viewmodel.WeatherUiState
import com.azadron.weatherapp.viewmodel.WeatherViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun WeatherScreen(
    onRequestLocation: () -> Unit,
    viewModel: WeatherViewModel = hiltViewModel(),
    onSettingsClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var cityQuery by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {
        // Top Bar
        WeatherTopBar(
            cityQuery = cityQuery,
            onCityQueryChange = { cityQuery = it },
            onSearchClick = { viewModel.loadWeatherByCity(cityQuery) },
            onLocationClick = onRequestLocation,
            onSettingsClick = onSettingsClick
        )

        // Content below Top Bar
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            when (val state = uiState) {
                is WeatherUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is WeatherUiState.Error -> {
                    Text(
                        text = state.message,
                        color = Color.Red,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is WeatherUiState.Success -> {
                    WeatherContent(state)
                }
            }
        }
    }
}

@Composable
fun WeatherContent(state: WeatherUiState.Success) {
    LazyColumn(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        // Current Day Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
                    .clip(RoundedCornerShape(24.dp)),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color(0xFF2196F3), Color(0xFF64B5F6))
                            )
                        )
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = state.currentWeather.name,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "${state.currentWeather.main.temp.toInt()}°C",
                            style = MaterialTheme.typography.displayLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = state.currentWeather.weather.firstOrNull()?.description
                                ?.replaceFirstChar { it.uppercase() } ?: "",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            WeatherDetailItem("Humidity", "${state.currentWeather.main.humidity}%", Color.White)
                            WeatherDetailItem("Wind", "${state.currentWeather.wind.speed} m/s", Color.White)
                        }
                    }
                }
            }
        }

        item {
            Text(
                text = "Weekly Forecast",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )
        }

        items(state.forecast.list) { forecastItem ->
            ForecastItemCard(forecastItem)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun ForecastItemCard(item: ForecastItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .padding(vertical = 4.dp)
            .clip(RoundedCornerShape(16.dp)),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(horizontalAlignment = Alignment.Start) {
                Text(
                    text = formatDate(item.dtTxt),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = item.weather.firstOrNull()?.main ?: "",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${item.main.temp.toInt()}°C",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Icon(
                    imageVector = when (item.weather.firstOrNull()?.main) {
                        "Rain" -> Icons.Default.Cloud
                        "Clear" -> Icons.Default.WbSunny
                        "Clouds" -> Icons.Default.CloudQueue
                        else -> Icons.AutoMirrored.Filled.Help
                    },
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun WeatherDetailItem(label: String, value: String, textColor: Color = Color.Black) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = textColor)
        Text(text = value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = textColor)
    }
}
@Composable
fun WeatherTopBar(
    cityQuery: String,
    onCityQueryChange: (String) -> Unit,
    onSearchClick: () -> Unit,
    onLocationClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    Surface(
        shadowElevation = 8.dp,
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 12.dp)
            .clip(RoundedCornerShape(24.dp))
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // Location Button
            IconButton(
                onClick = onLocationClick,
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(12.dp)
                    )
            ) {
                Icon(
                    Icons.Default.LocationOn,
                    contentDescription = "Current Location",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            // Search Field
            OutlinedTextField(
                value = cityQuery,
                onValueChange = onCityQueryChange,
                placeholder = { Text("Search city") },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .padding(horizontal = 12.dp),
                singleLine = true,
                shape = RoundedCornerShape(24.dp),
                trailingIcon = {
                    IconButton(onClick = onSearchClick) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.1f),
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.1f),
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = Color.Transparent,
                    cursorColor = MaterialTheme.colorScheme.primary,
                    focusedLeadingIconColor = MaterialTheme.colorScheme.primary,
                    focusedTrailingIconColor = MaterialTheme.colorScheme.primary
                )
            )

            // Settings Button
            IconButton(
                onClick = onSettingsClick,
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(12.dp)
                    )
            ) {
                Icon(
                    Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

fun formatDate(dateString: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val date = inputFormat.parse(dateString)
        val outputFormat = SimpleDateFormat("EEE HH:mm", Locale.getDefault())
        outputFormat.format(date ?: Date())
    } catch (e: Exception) {
        dateString
    }
}