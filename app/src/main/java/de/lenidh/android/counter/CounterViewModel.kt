package de.lenidh.android.counter

import androidx.lifecycle.ViewModel
import de.lenidh.android.counter.ui.theme.CounterUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.count

class CounterViewModel : ViewModel() {
    private var count = 0
    private val _uiState = MutableStateFlow(CounterUiState(count))

    val uiState: StateFlow<CounterUiState> = _uiState.asStateFlow()

    fun increment() {
        count++
        updateUiState()
    }

    fun decrement() {
        count--
        updateUiState()
    }

    fun reset() {
        count = 0
        updateUiState()
    }

    private fun updateUiState() {
        _uiState.value = CounterUiState(count = count)
    }
}