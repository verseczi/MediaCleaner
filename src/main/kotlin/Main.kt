package com.mediacleaner

import com.mediacleaner.app.MediaCleaner

fun main(args: Array<String>) {
    val mediaCleaner = MediaCleaner()
    mediaCleaner.startTimer(mediaCleaner)
}