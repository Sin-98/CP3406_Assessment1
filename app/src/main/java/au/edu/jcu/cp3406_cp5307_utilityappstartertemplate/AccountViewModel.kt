package au.edu.jcu.cp3406_cp5307_utilityappstartertemplate

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import au.edu.jcu.cp3406_cp5307_utilityappstartertemplate.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate

class AccountViewModel(app: Application) : AndroidViewModel(app) {

    private val dao = AppDatabase.getDatabase(app).appDao()
    private val prefs = UserPreferences(app)

    val username: StateFlow<String> = prefs.username
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), "")

    val allGoals: StateFlow<List<GoalEntity>> = dao.getAllGoals()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    val earnedBadges: StateFlow<List<BadgeEntity>> = dao.getAllBadges()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    fun saveUsername(name: String) = viewModelScope.launch {
        prefs.saveUsername(name)
    }

    /** Called from TimerViewModel when a focus session finishes */
    fun addFocusMinutes(minutes: Int) = viewModelScope.launch {
        val today = LocalDate.now().toString()
        val existing = dao.getGoalForDate(today)
        val newAchieved = (existing?.achievedMinutes ?: 0) + minutes
        val target = existing?.targetMinutes ?: 25
        val completed = newAchieved >= target

        dao.upsertGoal(
            existing?.copy(achievedMinutes = newAchieved, completed = completed)
                ?: GoalEntity(date = today, targetMinutes = target,
                    achievedMinutes = newAchieved, completed = completed)
        )
        if (completed) checkAndAwardBadges()
    }

    fun setTodayGoal(targetMinutes: Int) = viewModelScope.launch {
        val today = LocalDate.now().toString()
        val existing = dao.getGoalForDate(today)
        dao.upsertGoal(
            existing?.copy(targetMinutes = targetMinutes)
                ?: GoalEntity(date = today, targetMinutes = targetMinutes)
        )
    }

    private suspend fun checkAndAwardBadges() {
        val goals = dao.getAllGoals().first()
        val completed = goals.filter { it.completed }

        if (completed.size >= 1) award(BadgeType.FIRST_GOAL)
        if (completed.size >= 5) award(BadgeType.FIVE_GOALS)

        val today = goals.find { it.date == LocalDate.now().toString() }
        if ((today?.achievedMinutes ?: 0) >= 60) award(BadgeType.HOUR_FOCUS)

        // 3-day streak check
        val sortedDates = completed
            .map { LocalDate.parse(it.date) }
            .sortedDescending()
        if (sortedDates.size >= 3) {
            val hasStreak = sortedDates.zipWithNext().take(2)
                .all { (a, b) -> a.minusDays(1) == b }
            if (hasStreak) award(BadgeType.THREE_DAY_STREAK)
        }
    }

    private suspend fun award(badge: BadgeType) {
        if (dao.hasBadge(badge.id) == 0) {
            dao.insertBadge(BadgeEntity(badge.id, LocalDate.now().toString()))
        }
    }
}