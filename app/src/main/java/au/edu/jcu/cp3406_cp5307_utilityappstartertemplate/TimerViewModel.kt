package au.edu.jcu.cp3406_cp5307_utilityappstartertemplate

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

enum class TimerMode { FOCUS, BREAK }

data class TimerState(
    val totalSeconds: Int = 25 * 60,
    val secondsRemaining: Int = 25 * 60,
    val isRunning: Boolean = false,
    val mode: TimerMode = TimerMode.FOCUS,
    val focusDurationMinutes: Int = 25,
    val breakDurationMinutes: Int = 5,
    val sessionsCompleted: Int = 0,
    val totalFocusMinutesToday: Int = 0,
    val soundEnabled: Boolean = true,
    val quote: String = "Loading quote...",
    val currentStreak: Int = 5,
    val bestStreak: Int = 8,
    val lastActiveDate: String = "",   // store as "yyyy-MM-dd"
    val totalSessionsAllTime: Int = 47
)

class TimerViewModel : ViewModel() {

    private val _state = MutableStateFlow(TimerState())
    val state: StateFlow<TimerState> = _state

    private var timerJob: Job? = null

    init {
        fetchQuote()
    }

    private fun fetchQuote() {
        viewModelScope.launch {
            val quote = QuoteRepository.fetchQuote()
            _state.value = _state.value.copy(quote = quote)
        }
    }

    fun start() {
        if (_state.value.isRunning) return
        _state.value = _state.value.copy(isRunning = true)
        timerJob = viewModelScope.launch {
            while (_state.value.secondsRemaining > 0) {
                delay(1000L)
                _state.value = _state.value.copy(
                    secondsRemaining = _state.value.secondsRemaining - 1
                )
            }
            onTimerFinished()
        }
    }

    fun pause() {
        timerJob?.cancel()
        _state.value = _state.value.copy(isRunning = false)
    }

    fun reset() {
        timerJob?.cancel()
        val s = _state.value
        val total = if (s.mode == TimerMode.FOCUS)
            s.focusDurationMinutes * 60 else s.breakDurationMinutes * 60
        _state.value = s.copy(isRunning = false, secondsRemaining = total, totalSeconds = total)
    }

    fun skip() {
        timerJob?.cancel()
        onTimerFinished()
    }

    private fun onTimerFinished() {
        val s = _state.value
        if (s.mode == TimerMode.FOCUS) {
            updateStreak()
            _state.value = s.copy(
                isRunning = false,
                mode = TimerMode.BREAK,
                sessionsCompleted = s.sessionsCompleted + 1,
                totalFocusMinutesToday = s.totalFocusMinutesToday + s.focusDurationMinutes,
                secondsRemaining = s.breakDurationMinutes * 60,
                totalSeconds = s.breakDurationMinutes * 60
            )
        } else {
            _state.value = s.copy(
                isRunning = false,
                mode = TimerMode.FOCUS,
                secondsRemaining = s.focusDurationMinutes * 60,
                totalSeconds = s.focusDurationMinutes * 60
            )
        }
    }

    fun setFocusDuration(minutes: Int) {
        val s = _state.value
        _state.value = s.copy(
            focusDurationMinutes = minutes,
            secondsRemaining = if (s.mode == TimerMode.FOCUS) minutes * 60 else s.secondsRemaining,
            totalSeconds = if (s.mode == TimerMode.FOCUS) minutes * 60 else s.totalSeconds
        )
    }

    fun setBreakDuration(minutes: Int) {
        val s = _state.value
        _state.value = s.copy(
            breakDurationMinutes = minutes,
            secondsRemaining = if (s.mode == TimerMode.BREAK) minutes * 60 else s.secondsRemaining,
            totalSeconds = if (s.mode == TimerMode.BREAK) minutes * 60 else s.totalSeconds
        )
    }

    fun setSoundEnabled(enabled: Boolean) {
        _state.value = _state.value.copy(soundEnabled = enabled)
    }

    private fun updateStreak() {
        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
        val today = sdf.format(java.util.Date())
        val s = _state.value

        // Calculate yesterday's date string
        val cal = java.util.Calendar.getInstance()
        cal.add(java.util.Calendar.DAY_OF_MONTH, -1)
        val yesterday = sdf.format(cal.time)

        val newStreak = when (s.lastActiveDate) {
            today -> s.currentStreak
            yesterday -> s.currentStreak + 1
            else -> 1
        }
        _state.value = _state.value.copy(
            currentStreak = newStreak,
            bestStreak = maxOf(newStreak, s.bestStreak),
            lastActiveDate = today,
            totalSessionsAllTime = s.totalSessionsAllTime + 1
        )
    }
}