package com.agrtech.maps

import androidx.annotation.ColorRes
import com.agrtech.R

enum class WateringLevel(val intValue: Int) {
    LOW(30),
    MID(50),
    HIGH(90);

    companion object {

        @ColorRes
        fun generateColor(level: WateringLevel): Int = when (level) {
            LOW -> R.color.colorBlue
            MID -> R.color.colorViolet
            HIGH -> R.color.colorOrange
        }

        fun parseProgress(progress: Int): WateringLevel = when {
            progress <= LOW.intValue -> LOW
            progress > LOW.intValue && progress < MID.intValue -> MID
            progress >= HIGH.intValue -> HIGH
            else -> MID
        }
    }
}