package com.azadron.weatherapp.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ThemeViewModel : ViewModel() {
    // true = dark, false = light
    private val _isDarkTheme = MutableStateFlow(false)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme

    fun setDarkTheme(dark: Boolean) {
        _isDarkTheme.value = dark
    }

    fun toggleTheme() {
        _isDarkTheme.value = !_isDarkTheme.value
    }
}

