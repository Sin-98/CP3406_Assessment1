package au.edu.jcu.cp3406_cp5307_utilityappstartertemplate

import android.media.MediaPlayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import au.edu.jcu.cp3406_cp5307_utilityappstartertemplate.ui.theme.CP3406_CP5603UtilityAppStarterTemplateTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CP3406_CP5603UtilityAppStarterTemplateTheme {
                UtilityApp()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun UtilityAppPreview() {
    CP3406_CP5603UtilityAppStarterTemplateTheme {
        UtilityApp()
    }
}

@Composable
fun UtilityApp(
    timerViewModel: TimerViewModel = viewModel(),
    accountViewModel: AccountViewModel = viewModel()
) {
    val state by timerViewModel.state.collectAsState()
    var selectedTab by remember { mutableStateOf("Utility") }
    var isDarkMode by remember { mutableStateOf(false) }
    // Wire timer → account: when a focus session ends, log minutes for goal/badge tracking
    LaunchedEffect(timerViewModel, accountViewModel) {
        timerViewModel.onFocusSessionComplete = { minutes ->
            accountViewModel.addFocusMinutes(minutes)
        }
    }

    CP3406_CP5603UtilityAppStarterTemplateTheme(darkTheme = isDarkMode) {
        Scaffold(
            bottomBar = {
                NavigationBar {
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Home, contentDescription = "Utility") },
                        label = { Text("Utility") },
                        selected = selectedTab == "Utility",
                        onClick = { selectedTab = "Utility" }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Person, contentDescription = "Account") },
                        label = { Text("Account") },
                        selected = selectedTab == "Account",
                        onClick = { selectedTab = "Account" }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Star, contentDescription = "Badges") },
                        label = { Text("Badges") },
                        selected = selectedTab == "Badges",
                        onClick = { selectedTab = "Badges" }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                        label = { Text("Settings") },
                        selected = selectedTab == "Settings",
                        onClick = { selectedTab = "Settings" }
                    )
                }
            }
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                when (selectedTab) {
                    "Utility"  -> UtilityScreen(timerViewModel)
                    "Account"  -> AccountScreen(accountViewModel)
                    "Badges"   -> BadgesScreen(accountViewModel)
                    "Settings" -> SettingsScreen(
                        viewModel = timerViewModel,
                        isDarkMode = isDarkMode,
                        onDarkModeToggle = { isDarkMode = it }
                    )
                }
            }
        }
    }
}

@Composable
fun UtilityScreen(viewModel: TimerViewModel) {
    val state by viewModel.state.collectAsState()

    val context = LocalContext.current
    LaunchedEffect(state.secondsRemaining) {
        if (state.secondsRemaining == 0 && state.soundEnabled) {
            val mediaPlayer = MediaPlayer.create(context, android.provider.Settings.System.DEFAULT_NOTIFICATION_URI)
            mediaPlayer?.start()
        }
    }

    val minutes = state.secondsRemaining / 60
    val seconds = state.secondsRemaining % 60
    val timeText = "%02d:%02d".format(minutes, seconds)
    val progress = if (state.totalSeconds > 0)
        state.secondsRemaining.toFloat() / state.totalSeconds.toFloat() else 1f

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Mode label
        Text(
            text = if (state.mode == TimerMode.FOCUS) "Focus Time" else "Break Time",
            style = MaterialTheme.typography.titleLarge,
            color = if (state.mode == TimerMode.FOCUS)
                MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.secondary
        )

        // Circular progress + timer
        Box(contentAlignment = Alignment.Center) {
            CircularProgressIndicator(
                progress = { progress },
                modifier = Modifier.size(200.dp),
                strokeWidth = 8.dp,
                color = if (state.mode == TimerMode.FOCUS)
                    MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.secondary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
            Text(text = timeText, style = MaterialTheme.typography.displayMedium)
        }

        // Start / Pause / Reset buttons
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(onClick = {
                if (state.isRunning) viewModel.pause() else viewModel.start()
            }) {
                Text(if (state.isRunning) "Pause" else "Start")
            }
            OutlinedButton(onClick = { viewModel.reset() }) {
                Text("Reset")
            }
            OutlinedButton(onClick = { viewModel.skip() }) {
                Text("Skip")
            }
        }

        HorizontalDivider()
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
        ) {
            Text(
                text = state.quote,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(16.dp),
                textAlign = TextAlign.Center
            )
        }

        // Streak banner
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFAEEDA)),
            shape = RoundedCornerShape(10.dp)
        ) {
            Row(
                modifier = Modifier.padding(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    Icons.Default.LocalFireDepartment,
                    contentDescription = null,
                    tint = Color(0xFFBA7517),
                    modifier = Modifier.size(24.dp)
                )
                Column {
                    Text(
                        "${state.currentStreak}-day streak",
                        fontWeight = FontWeight.Medium,
                        fontSize = 13.sp,
                        color = Color(0xFF633806)
                    )
                    Text(
                        "Keep it going — don't break the chain!",
                        fontSize = 10.sp,
                        color = Color(0xFF854F0B)
                    )
                }
            }
        }

// Expanded 4-card stats grid
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StatCard("Sessions", "${state.sessionsCompleted}", Modifier.weight(1f))
            StatCard("Focus time", "${state.totalFocusMinutesToday} min", Modifier.weight(1f), teal = true)
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StatCard("Best streak", "${state.bestStreak}", Modifier.weight(1f), amber = true)
            StatCard("Total sessions", "${state.totalSessionsAllTime}", Modifier.weight(1f))
        }
    }
}

@Composable
fun StatCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    teal: Boolean = false,
    amber: Boolean = false
) {
    val valueColor = when {
        teal -> Color(0xFF0F6E56)
        amber -> Color(0xFF854F0B)
        else -> MaterialTheme.colorScheme.primary
    }
    Card(modifier = modifier.padding(4.dp)) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, style = MaterialTheme.typography.titleLarge, color = valueColor)
            Text(label, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
fun SettingsScreen(
    viewModel: TimerViewModel,
    isDarkMode: Boolean,
    onDarkModeToggle: (Boolean) -> Unit
) {
    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text("Settings", style = MaterialTheme.typography.headlineMedium)

        // Focus duration slider
        Text(
            "Focus Duration: ${state.focusDurationMinutes} min",
            style = MaterialTheme.typography.titleMedium
        )
        Slider(
            value = state.focusDurationMinutes.toFloat(),
            onValueChange = { viewModel.setFocusDuration(it.toInt()) },
            valueRange = 5f..180f,
            steps = 34
        )

        HorizontalDivider()

        // Break duration slider
        Text(
            "Break Duration: ${state.breakDurationMinutes} min",
            style = MaterialTheme.typography.titleMedium
        )
        Slider(
            value = state.breakDurationMinutes.toFloat(),
            onValueChange = { viewModel.setBreakDuration(it.toInt()) },
            valueRange = 1f..30f,
            steps = 5
        )

        HorizontalDivider()

        // Sound toggle
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Sound Notifications", style = MaterialTheme.typography.titleMedium)
            Switch(
                checked = state.soundEnabled,
                onCheckedChange = { viewModel.setSoundEnabled(it) }
            )
        }

        HorizontalDivider()
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Dark Mode", style = MaterialTheme.typography.titleMedium)
            Switch(checked = isDarkMode, onCheckedChange = onDarkModeToggle)
        }
    }
}