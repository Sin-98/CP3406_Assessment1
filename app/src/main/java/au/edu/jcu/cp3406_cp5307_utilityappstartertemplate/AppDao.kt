package au.edu.jcu.cp3406_cp5307_utilityappstartertemplate

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    @Upsert
    suspend fun upsertGoal(goal: GoalEntity)

    @Query("SELECT * FROM goals WHERE date = :date LIMIT 1")
    suspend fun getGoalForDate(date: String): GoalEntity?

    @Query("SELECT * FROM goals ORDER BY date DESC")
    fun getAllGoals(): Flow<List<GoalEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertBadge(badge: BadgeEntity)

    @Query("SELECT * FROM badges")
    fun getAllBadges(): Flow<List<BadgeEntity>>

    @Query("SELECT COUNT(*) FROM badges WHERE badgeId = :id")
    suspend fun hasBadge(id: String): Int
}