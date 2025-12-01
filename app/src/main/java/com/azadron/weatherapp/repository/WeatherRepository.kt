package com.azadron.weatherapp.repository

import com.azadron.weatherapp.data.CurrentWeatherResponse
import com.azadron.weatherapp.data.ForecastResponse
import com.azadron.weatherapp.di.WeatherApi
import javax.inject.Inject

class WeatherRepository @Inject constructor(
    private val api: WeatherApi
) {
    // You should store this securely, e.g., BuildConfig or local.properties
    private val API_KEY = "d35a6841f1e0cd541a69c545ddaa177d"

    suspend fun getWeather(lat: Double, lon: Double): CurrentWeatherResponse {
        return api.getCurrentWeather(lat, lon, API_KEY)
    }

    suspend fun getWeatherByCity(city: String): CurrentWeatherResponse {
        return api.getCurrentWeatherByCity(city, API_KEY)
    }

    suspend fun getForecast(lat: Double, lon: Double): ForecastResponse {
        return api.getForecast(lat, lon, API_KEY)
    }

    suspend fun getForecastByCity(city: String): ForecastResponse {
        return api.getForecastByCity(city, API_KEY)
    }
}