package io.github.beankitk.numberbricks.sample.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.beankitk.numberbricks.sample.utils.toDigitList
import java.time.Instant
import java.time.LocalTime
import java.time.ZoneId
import kotlinx.coroutines.isActive
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted

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
    
    fun toggleAmbientMode() {
        _isAmbientMode.value = !_isAmbientMode.value
    }

    fun toggleLargeClock() {
        _isLargeClock.value = !_isLargeClock.value
    }  
    
    fun scheduleAmbientMode() {
        viewModelScope.launch {
            delay(3000)
            _isAmbientMode.value = true
        }
    }
}