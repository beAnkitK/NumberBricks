package io.github.beankitk.numberbricks.sample.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import java.time.LocalTime
import io.github.beankitk.numberbricks.sample.utils.delayUntilNextSecond
import io.github.beankitk.numberbricks.sample.utils.getTime
import io.github.beankitk.numberbricks.sample.utils.toDigitList
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted

class TimeVM : ViewModel() {

    private val _currentTime = MutableStateFlow(getTime())
    val currentTime: StateFlow<LocalTime> = _currentTime.asStateFlow()
    
    private val _isClockRunning = MutableStateFlow(true)
    val isClockRunning: StateFlow<Boolean> = _isClockRunning.asStateFlow()
    
    val currentTimeAsList: StateFlow<List<Int>> = _currentTime
        .map { it.toDigitList() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = _currentTime.value.toDigitList()
        )
        
    private var clockJob: Job? = null

    init {
        viewModelScope.launch {
            _isClockRunning.collect { running ->
                if (running) startClock() else stopClock()
            }
        }
    }

    private fun startClock() {
        if (clockJob?.isActive == true) return
        clockJob = viewModelScope.launch {
            while (isActive) {
                _currentTime.value = getTime()
                delay(delayUntilNextSecond())
            }
        }
    }

    private fun stopClock() {
        clockJob?.cancel()
        clockJob = null
    }
    
    fun toggleClockRunning() {
        _isClockRunning.value = !_isClockRunning.value
    }
}