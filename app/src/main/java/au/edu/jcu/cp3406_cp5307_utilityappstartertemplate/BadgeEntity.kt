package au.edu.jcu.cp3406_cp5307_utilityappstartertemplate

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "badges")
data class BadgeEntity(
    @PrimaryKey val badgeId: String,
    val earnedDate: String
)