package com.mediacleaner.Utils

import com.mediacleaner.DataModels.Settings
import java.io.File
import java.time.LocalDateTime
import java.text.SimpleDateFormat
import java.util.*
import java.nio.file.Files
import java.nio.file.Paths


class Logger (val classPath: String, val settings: Settings){
    val dir = File(System.getProperty("java.class.path")).absoluteFile.parentFile.toString()

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
        if(settings.logFile != "") {
            val logFile = File(dir + System.getProperty("file.separator") + settings.logFile)
            if(logFile.exists())
                archive()
            logFile.appendText(log + "\n")
        }
    }

    private fun archive() {
        val dateFormat = SimpleDateFormat("yyyy.MM.dd.")
        val formattedDate = dateFormat.format(Date(File(settings.logFile).lastModified()))

        if(formattedDate != dateFormat.format(DateUtils.asDate(LocalDateTime.now())))
        {
            val directory = File(dir + System.getProperty("file.separator") + "archive" + System.getProperty("file.separator"))
            if (!directory.exists())
                directory.mkdir()
            Files.move(Paths.get(dir + System.getProperty("file.separator") + settings.logFile), Paths.get(dir + System.getProperty("file.separator") + "archive" + System.getProperty("file.separator") + formattedDate + "-" + settings.logFile))
        }
    }

}