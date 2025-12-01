package com.azadron.weatherapp.data

import com.google.gson.annotations.SerializedName

// --- Current Weather Models ---
data class CurrentWeatherResponse(
    val coord: Coord,
    val weather: List<Weather>,
    val main: Main,
    val wind: Wind,
    val name: String,
    val dt: Long
)

data class Coord(val lon: Double, val lat: Double)
data class Weather(val id: Int, val main: String, val description: String, val icon: String)
data class Main(
    val temp: Double,
    @SerializedName("feels_like") val feelsLike: Double,
    @SerializedName("temp_min") val tempMin: Double,
    @SerializedName("temp_max") val tempMax: Double,
    val humidity: Int
)
data class Wind(val speed: Double)

// --- Forecast Models (5 Day / 3 Hour) ---
data class ForecastResponse(
    val list: List<ForecastItem>,
    val city: City
)

data class ForecastItem(
    val dt: Long,
    val main: Main,
    val weather: List<Weather>,
    @SerializedName("dt_txt") val dtTxt: String
)

data class City(val name: String, val country: String)