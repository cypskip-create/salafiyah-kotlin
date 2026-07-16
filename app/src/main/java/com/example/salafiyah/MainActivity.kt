package com.example.salafiyah

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.salafiyah.ui.SalafiyahApp
import com.example.salafiyah.ui.theme.SalafiyahTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SalafiyahTheme {
                SalafiyahApp()
            }
        }
    }
}
