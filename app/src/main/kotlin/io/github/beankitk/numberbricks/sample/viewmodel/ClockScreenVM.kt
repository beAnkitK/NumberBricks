package io.github.beankitk.numberbricks.sample.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.isActive
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import java.time.Instant
import java.time.LocalTime
import java.time.ZoneId

class ClockScreenVM : ViewModel() {

    private val _currentTime = MutableStateFlow(LocalTime.now(ZoneId.systemDefault()))
    val currentTime: StateFlow<LocalTime> = _currentTime.asStateFlow()
    
    private val _isAmbientMode = MutableStateFlow(false)
    val isAmbientMode: StateFlow<Boolean> = _isAmbientMode.asStateFlow()
    
    private val _isLargeClock = MutableStateFlow(false)
    val isLargeClock: StateFlow<Boolean> = _isLargeClock.asStateFlow()
    
    val currentTimeAsList: StateFlow<List<Int>> = _currentTime
        .map { it.toDigitList() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = _currentTime.value.toDigitList()
        )
        
    init {
        viewModelScope.launch {
            while (isActive) {
                val instant = Instant.now()
                val millis = instant.toEpochMilli()
                val delayUntilNextSecond = 1000 - (millis % 1000)
                delay(delayUntilNextSecond)
                _currentTime.value = LocalTime.now(ZoneId.systemDefault())
            }
        }
    }
    
    fun toggleAmbient() {
        _isAmbientMode.value = !_isAmbientMode.value
    }

    fun toggleLargeClock() {
        _isLargeClock.value = !_isLargeClock.value
    }  
    
    fun scheduleAmbientMode() {
        if (!_isAmbientMode.value) {
            viewModelScope.launch {
                delay(3000)
                _isAmbientMode.value = true
            }
        }
    } 
}

fun LocalTime.toDigitList(is24Hour: Boolean = false): List<Int> {
    val h = if (is24Hour) this.hour else this.hour % 12
    val m = this.minute
    val s = this.second
    return listOf(h / 10, h % 10, m / 10, m % 10, s / 10, s % 10)
}