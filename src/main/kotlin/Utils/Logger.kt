package com.mediacleaner.Utils

import com.mediacleaner.DataModels.Settings
import java.io.File
import java.time.LocalDateTime

class Logger (val classPath: String, val settings: Settings){
    fun info(str: String) {
        val logMessage = "${LocalDateTime.now()} [INFO] $classPath: $str"
        println(logMessage)
        writeToFile(logMessage)
    }

    fun error(str: String) {
        val logMessage = "${LocalDateTime.now()} [ERROR] $classPath: $str"
        println(logMessage)
        writeToFile(logMessage)
    }

    fun debug(str: String) {
        if(settings.debug) {
            val logMessage = "${LocalDateTime.now()} [DEBUG] $classPath: $str"
            println(logMessage)
            writeToFile(logMessage)
        }
    }

    fun trace(str: String) {
        if(settings.trace) {
            val logMessage = "${LocalDateTime.now()} [TRACE] $classPath: $str"
            println(logMessage)
            writeToFile(logMessage)
        }
    }

    private fun writeToFile(log: String) {
        if(settings.logFile != "")
            File(settings.logFile).appendText(log + "\n")
    }

}