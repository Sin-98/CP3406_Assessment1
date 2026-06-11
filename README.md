# Utility App – CP3406 / CP5307

## Study Focus Timer App

### Overview
Study Focus Timer App is an Android application designed to help students improve productivity and maintain concentration during study sessions. The app uses a focus timer approach, allowing users to work in dedicated study intervals followed by short breaks. This helps users manage their time effectively, reduce distractions, and build better study habits.

---

### Features
- Start and pause study sessions
- Reset the timer at any time
- Skip directly to the next session
- Simple and user-friendly interface
- Real-time countdown display
- Sound notification on timer completion
- Adjustable focus/break durations
- Motivational quotes
- Dark/light mode
- User account with saved username
- Daily focus goals with progress tracking
- Achievement badges awarded automatically on goal completion

---

### Technologies Used
- Kotlin
- Android Studio
- Retrofit + Gson for API calls
- Jetpack Compose
- ViewModel
- Room (local database for goals and badges)
- DataStore Preferences (persistent user profile)
- KSP (Kotlin Symbol Processing for Room)

---

### How it works
1. Launch the application.
2. Press Start to begin a focus session.
3. The timer counts down the remaining study time.
4. When the session ends, a sound notification plays and the app automatically switches to a break period.
5. Users can pause, reset, or skip sessions as needed.
6. Focus and break durations can be adjusted in the app's settings to suit your schedule. 
7. Repeat the cycle to maintain productive study habits.
8. Visit the Account tab to set your name and a daily focus goal (in minutes).
9. Progress toward your goal is tracked automatically after each focus session.
10. Visit the Badges tab to view earned achievements; locked badges are revealed as goals are completed.

---

### App Structure
```
MainActivity.kt         — Navigation and screen routing
TimerViewModel.kt       — Timer logic and session state
AccountViewModel.kt     — Goal tracking and badge awarding
AccountScreen.kt        — Profile and daily goal UI
BadgesScreen.kt         — Badge gallery (locked/unlocked)
GoalEntity.kt           — Room entity for daily goals
BadgeEntity.kt          — Room entity for earned badges
AppDao.kt               — Database queries
AppDatabase.kt          — Room database setup
UserPreferences.kt      — DataStore for username
BadgeType.kt            — Badge definitions and criteria
QuoteRepository.kt      — Motivational quotes via Retrofit
```

---

### Future Improvements
- Custom session labels — Let users name sessions (e.g. "Math", "Reading") for better tracking
- Notification customization — Choose different sounds or vibration patterns per session type
- Cloud sync — Back up settings and session history across devices
- Accessibility improvements — TalkBack support, larger text options, high contrast mode
- Lock screen timer — Display the countdown on the lock screen so users don't need to unlock their phone
- More badge types — Weekly challenges, total hours milestones, and more

---

### License
This template is provided for educational use in CP3406.