package com.example.uthsmarttasks.ui.onboarding

import androidx.lifecycle.ViewModel
import com.example.uthsmarttasks.R

data class OnBoardingPage(
    val imageRes: Int,
    val title: String,
    val description: String
)

class OnBoardingViewModel : ViewModel() {
    val pages = listOf(
        OnBoardingPage(
            R.drawable.onboarding1,
            "Easy Time Management",
            "With management based on priority and daily tasks, it will give you convenience in managing and determining the tasks that must be done first."
        ),
        OnBoardingPage(
            R.drawable.onboarding2,
            "Increase Work Effectiveness",
            "Time management and prioritizing important tasks will give your job statistics better and always improve."
        ),
        OnBoardingPage(
            R.drawable.onboarding3,
            "Reminder Notification",
            "The advantage of this application is that it gives reminders for you, so you don't forget to keep doing your assignments well and on time."
        )
    )
}
