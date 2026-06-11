package au.edu.jcu.cp3406_cp5307_utilityappstartertemplate

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "goals")
data class GoalEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: String,
    val targetMinutes: Int,
    val achievedMinutes: Int = 0,
    val completed: Boolean = false
)