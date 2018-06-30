package com.mediacleaner.app

import java.util.*

class MediaCleanerTimer(mediaCleaner: MediaCleaner): TimerTask() {
    private val mediaCleaner = mediaCleaner
    override fun run() {
        mediaCleaner.mediaCleaner()
        if(mediaCleaner.cancelled) {
            mediaCleaner.startTimer(mediaCleaner)
        }
    }
}