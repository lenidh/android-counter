package de.lenidh.android.counter

import androidx.lifecycle.ViewModel
import de.lenidh.android.counter.ui.theme.CounterUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CounterViewModel : ViewModel() {
    private var _count = 0
    private var _step = 1
    private val _uiState = MutableStateFlow(CounterUiState(count, step))

    val uiState: StateFlow<CounterUiState> = _uiState.asStateFlow()

    var count
        set(value) {
            _count = value
            updateUiState()
        }
        get() = _count

    var step
        set(value) {
            _step = value
            updateUiState()
        }
        get() = _step

    fun addStep() {
        count += step
        updateUiState()
    }

    fun reset() {
        count = 0
        updateUiState()
    }

    private fun updateUiState() {
        _uiState.value = CounterUiState(count, step)
    }
}