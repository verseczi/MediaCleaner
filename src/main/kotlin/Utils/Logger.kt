package com.mediacleaner.Utils

import java.time.LocalDateTime

class Logger (classPath: String){
    val classPath = classPath
    fun info(str: String) {
        println("${LocalDateTime.now()} [INFO] $classPath: $str")
    }

    fun error(str: String) {
        println("${LocalDateTime.now()} [ERROR] $classPath: $str")
    }

    fun debug(str: String) {
        println("${LocalDateTime.now()} [DEBUG] $classPath: $str")
    }

    fun trace(str: String) {
        println("${LocalDateTime.now()} [TRACE] $classPath: $str")
    }
}