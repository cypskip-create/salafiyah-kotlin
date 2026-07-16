package com.example.salafiyah.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

object AppColors {
    val GreenDeep = Color(0xFF0B2417)
    val GreenDark = Color(0xFF123820)
    val GreenMid = Color(0xFF2D6A4F)
    val Gold = Color(0xFFD6AA3F)
    val GoldLight = Color(0xFFF2D27C)
    val Cream = Color(0xFFFCF7EE)
    val TextDark = Color(0xFF1B1D1A)
    val TextMid = Color(0xFF3E473F)
    val TextLight = Color(0xFF7A8178)
}

@Composable
fun SalafiyahTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = AppColors.GreenDark,
            secondary = AppColors.Gold,
            background = AppColors.Cream,
            surface = Color.White,
            onPrimary = Color.White,
            onSecondary = AppColors.TextDark,
            onBackground = AppColors.TextDark,
            onSurface = AppColors.TextDark,
        ),
        content = content,
    )
}