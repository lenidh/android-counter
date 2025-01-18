package de.lenidh.android.counter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.List
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import de.lenidh.android.counter.ui.theme.CounterTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CounterTheme {
                CounterScreen()
            }
        }
    }
}

@Composable
fun CounterScreen(viewModel: CounterViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
        ) {
            Spacer(modifier = Modifier.weight(1.0f))
            CounterDisplay(uiState.count, onClick = { viewModel.increment() })
            Spacer(modifier = Modifier.weight(1.0f))
            CounterMenu(onDecrement = { viewModel.decrement() }, onReset = { viewModel.reset() })
            Spacer(modifier = Modifier.height(64.dp))
        }
    }
}

@Preview
@Composable
fun PreviewCounterScreen() {
    CounterTheme {
        CounterScreen()
    }
}

@Composable
fun CounterDisplay(count: Int = 0, onClick: () -> Unit = {}) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            }, contentAlignment = Alignment.Center
    ) {
        Text(text = count.toString(), fontSize = 256.sp)
    }
}

@Preview
@Composable
fun PreviewCounter() {
    CounterTheme {
        CounterDisplay()
    }
}

@Composable
fun CounterMenu(onDecrement: () -> Unit = {}, onReset: () -> Unit = {}) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
        Button(onClick = { onReset() }) {
            Icon(Icons.Rounded.Refresh, contentDescription = "Reset")
        }
        Spacer(modifier = Modifier.width(16.dp))
        Button(onClick = { onDecrement() }) {
            Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Decrement")
        }
    }
}

@Preview
@Composable
fun PreviewCounterMenu() {
    CounterTheme {
        CounterMenu()
    }
}
