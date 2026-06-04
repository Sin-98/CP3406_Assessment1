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
    val soundEnabled: Boolean = true
)

class TimerViewModel : ViewModel() {

    private val _state = MutableStateFlow(TimerState())
    val state: StateFlow<TimerState> = _state

    private var timerJob: Job? = null

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

    private fun onTimerFinished() {
        val s = _state.value
        if (s.mode == TimerMode.FOCUS) {
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
}