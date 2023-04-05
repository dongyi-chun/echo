package com.whitecrow.echo.util

import android.content.Context
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.Colors
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import com.whitecrow.echo.R

val Context.themeColors: Colors
    @Composable get() {
        val isDarkTheme = isSystemInDarkTheme()
        val primary = Color(ContextCompat.getColor(this, R.color.colorPrimary))
        val primaryVariant = Color(ContextCompat.getColor(this, R.color.colorPrimaryVariant))
        val secondary = Color(ContextCompat.getColor(this, R.color.colorSecondary))

        return if (isDarkTheme) {
            darkColors(
                primary = primary,
                primaryVariant = primaryVariant,
                secondary = secondary
            )
        } else {
            lightColors(
                primary = primary,
                primaryVariant = primaryVariant,
                secondary = secondary
            )
        }
    }