package au.edu.jcu.cp3406_cp5307_utilityappstartertemplate

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import java.time.LocalDate

@Composable
fun AccountScreen(accountViewModel: AccountViewModel = viewModel()) {
    val username by accountViewModel.username.collectAsState()
    val goals by accountViewModel.allGoals.collectAsState()
    val today = goals.find { it.date == LocalDate.now().toString() }

    var nameInput by remember { mutableStateOf("") }
    var goalInput by remember { mutableStateOf("") }

    LaunchedEffect(username) {
        if (username.isNotEmpty()) nameInput = username
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("My Account", style = MaterialTheme.typography.headlineMedium)

        // ── Username ──────────────────────────────────────────────
        OutlinedTextField(
            value = nameInput,
            onValueChange = { nameInput = it },
            label = { Text("Your name") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { accountViewModel.saveUsername(nameInput) }),
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = { accountViewModel.saveUsername(nameInput) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Name")
        }

        HorizontalDivider()

        // ── Today's Goal ──────────────────────────────────────────
        Text("Today's Focus Goal", style = MaterialTheme.typography.titleMedium)

        if (today != null) {
            val progress = (today.achievedMinutes.toFloat() / today.targetMinutes).coerceIn(0f, 1f)
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
            )
            Spacer(Modifier.height(4.dp))
            Text("${today.achievedMinutes} / ${today.targetMinutes} min")
            if (today.completed) {
                Text(
                    "✅ Goal achieved today!",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        } else {
            Text("No goal set for today yet.")
        }

        OutlinedTextField(
            value = goalInput,
            onValueChange = { goalInput = it.filter(Char::isDigit) },
            label = { Text("Set goal (minutes)") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = {
                goalInput.toIntOrNull()?.let { accountViewModel.setTodayGoal(it) }
            }),
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = { goalInput.toIntOrNull()?.let { accountViewModel.setTodayGoal(it) } },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Set Goal")
        }
    }
}