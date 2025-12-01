package com.azadron.weatherapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.azadron.weatherapp.data.CurrentWeatherResponse
import com.azadron.weatherapp.data.ForecastItem
import com.azadron.weatherapp.data.ForecastResponse
import com.azadron.weatherapp.repository.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class WeatherUiState {
    object Loading : WeatherUiState()
    data class Success(
        val currentWeather: CurrentWeatherResponse,
        val forecast: ForecastResponse
    ) : WeatherUiState()
    data class Error(val message: String) : WeatherUiState()
}

fun getDailyForecast(forecast: ForecastResponse): List<ForecastItem> {
    return forecast.list.filter { item ->
        item.dtTxt.contains("12:00:00") // pick forecast at 12:00 each day
    }
}

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val repository: WeatherRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<WeatherUiState>(WeatherUiState.Loading)
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()



    fun loadWeatherByLocation(lat: Double, lon: Double) {
        viewModelScope.launch {
            _uiState.value = WeatherUiState.Loading
            try {
                val current = repository.getWeather(lat, lon)
                val forecast = repository.getForecast(lat, lon)
                val dailyForecast = getDailyForecast(forecast)
                _uiState.value = WeatherUiState.Success(current, forecast.copy(list = dailyForecast))
            } catch (e: Exception) {
                _uiState.value = WeatherUiState.Error("Failed to load weather: ${e.localizedMessage}")
            }
        }
    }

    fun loadWeatherByCity(city: String) {
        viewModelScope.launch {
            _uiState.value = WeatherUiState.Loading
            try {
                val current = repository.getWeatherByCity(city)
                val forecast = repository.getForecastByCity(city)
                val dailyForecast = getDailyForecast(forecast)
                _uiState.value = WeatherUiState.Success(current, forecast.copy(list = dailyForecast))
            } catch (e: Exception) {
                _uiState.value = WeatherUiState.Error("City not found or network error.")
            }
        }
    }
}