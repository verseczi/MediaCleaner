package com.mediacleaner.app

import java.util.*

class MediaCleanerTimer(var mediaCleaner: MediaCleaner): TimerTask() {
    override fun run() {
        mediaCleaner.mediaCleaner()
        if(mediaCleaner.cancelled) {
            mediaCleaner.startTimer(mediaCleaner)
        }
    }
}