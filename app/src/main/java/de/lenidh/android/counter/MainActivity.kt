package de.lenidh.android.counter

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.os.Build
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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.viewmodel.compose.viewModel
import com.inidamleader.ovtracker.util.compose.AutoSizeText
import de.lenidh.android.counter.ui.theme.CounterTheme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class MainActivity : ComponentActivity() {
    private val countPrefKey = intPreferencesKey("count")
    private val stepPrefKey = intPreferencesKey("step")
    private val viewModel: CounterViewModel = CounterViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val count: Int = runBlocking {
            dataStore.data
                .map { preferences ->
                    // No type safety.
                    preferences[countPrefKey] ?: 0
                }.first()
        }
        val step: Int = runBlocking {
            dataStore.data
                .map { preferences ->
                    // No type safety.
                    preferences[stepPrefKey] ?: 1
                }.first()
        }

        viewModel.count = count
        viewModel.step = step

        enableEdgeToEdge()

        setContent {
            CounterTheme {
                CounterScreen(viewModel)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        runBlocking {
            dataStore.edit { settings ->
                settings[countPrefKey] = viewModel.count
            }
            dataStore.edit { settings ->
                settings[stepPrefKey] = viewModel.step
            }
        }
    }
}

@Composable
fun CounterScreen(viewModel: CounterViewModel = viewModel()) {
    val bgColor = MaterialTheme.colorScheme.primaryContainer

    if (!LocalView.current.isInEditMode) {
        val window = (LocalView.current.context as Activity).window
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S) {
            window.statusBarColor = bgColor.toArgb()
            window.navigationBarColor = bgColor.toArgb()
        }
    }

    val uiState by viewModel.uiState.collectAsState()
    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        containerColor = bgColor,
    ) { innerPadding ->
        when (LocalConfiguration.current.orientation) {
            Configuration.ORIENTATION_LANDSCAPE -> {
                Row(
                    modifier = Modifier
                        .padding(innerPadding)
                        .consumeWindowInsets(innerPadding)
                ) {
                    Spacer(modifier = Modifier.width(48.dp))
                    CounterMenu(
                        step = uiState.step,
                        onDecrement = { viewModel.step *= -1 },
                        onReset = { viewModel.reset() })
                    Spacer(modifier = Modifier.width(48.dp))
                    CounterDisplay(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(1.0f)
                            .padding(vertical = 24.dp),
                        count = uiState.count,
                        onClick = { viewModel.addStep() })
                    Spacer(modifier = Modifier.width(48.dp))
                }
            }

            else -> {
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .consumeWindowInsets(innerPadding)
                ) {
                    Spacer(modifier = Modifier.height(64.dp))
                    CounterDisplay(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1.0f)
                            .padding(horizontal = 24.dp),
                        count = uiState.count,
                        onClick = { viewModel.addStep() })
                    Spacer(modifier = Modifier.height(64.dp))
                    CounterMenu(
                        step = uiState.step,
                        onDecrement = { viewModel.step *= -1 },
                        onReset = { viewModel.reset() })
                    Spacer(modifier = Modifier.height(64.dp))
                }
            }
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
fun CounterDisplay(
    modifier: Modifier = Modifier,
    count: Int = 0,
    color: Color = Color.Unspecified,
    onClick: () -> Unit = {}
) {
    Box(
        modifier = modifier
            .clickable {
                onClick()
            }, contentAlignment = Alignment.Center
    ) {
        AutoSizeText(text = count.toString(), maxLines = 1, maxTextSize = 256.sp, color = color)
    }
}

@Preview
@Composable
fun PreviewCounter() {
    CounterDisplay()
}

@Composable
fun CounterMenu(step: Int, onDecrement: () -> Unit = {}, onReset: () -> Unit = {}) {
    when (LocalConfiguration.current.orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> {
            Column(modifier = Modifier.fillMaxHeight(), verticalArrangement = Arrangement.Center) {
                Button(onClick = { onDecrement() }) {
                    if (step > 0) {
                        Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Increment")
                    } else {
                        Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Decrement")
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { onReset() }) {
                    Icon(Icons.Rounded.Refresh, contentDescription = "Reset")
                }
            }
        }

        else -> {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Button(onClick = { onDecrement() }) {
                    if (step > 0) {
                        Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Increment")
                    } else {
                        Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Decrement")
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                Button(onClick = { onReset() }) {
                    Icon(Icons.Rounded.Refresh, contentDescription = "Reset")
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewCounterMenu() {
    CounterTheme {
        CounterMenu(step = 1)
    }
}
