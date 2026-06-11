package au.edu.jcu.cp3406_cp5307_utilityappstartertemplate

enum class BadgeType(
    val id: String,
    val title: String,
    val description: String,
    val emoji: String
) {
    FIRST_GOAL(
        id = "FIRST_GOAL",
        title = "First Step",
        description = "Complete your first daily goal",
        emoji = "🎯"
    ),
    THREE_DAY_STREAK(
        id = "THREE_DAY_STREAK",
        title = "On a Roll",
        description = "Hit your goal 3 days in a row",
        emoji = "🔥"
    ),
    HOUR_FOCUS(
        id = "HOUR_FOCUS",
        title = "Deep Focus",
        description = "Accumulate 60 min of focus in one day",
        emoji = "⏱️"
    ),
    FIVE_GOALS(
        id = "FIVE_GOALS",
        title = "Consistent",
        description = "Complete goals on 5 different days",
        emoji = "🏅"
    )
}