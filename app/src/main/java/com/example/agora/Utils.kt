package com.example.agora

import androidx.fragment.app.Fragment

fun setQuality(quality: Int): NetworkQuality {
    return when(quality){
        0-> NetworkQuality.UNKNOWN
        1-> NetworkQuality.EXCELLENT
        2-> NetworkQuality.GOOD
        3-> NetworkQuality.POOR
        4-> NetworkQuality.BAD
        5-> NetworkQuality.VBAD
        6-> NetworkQuality.DOWN
        8-> NetworkQuality.DETECTING
        else -> { NetworkQuality.UNKNOWN }
    }
}

