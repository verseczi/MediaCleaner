package com.mediacleaner.Utils

import java.io.File
import java.text.StringCharacterIterator

object FileUtils {

    fun getFileSize(file: File): Long {
        return file.length()
    }

    fun humanReadableByteCountSI(bytes: Long): String {
        var bytes = bytes
        if (-1000 < bytes && bytes < 1000) {
            return "$bytes B"
        }
        val ci = StringCharacterIterator("kMGTPE")
        while (bytes <= -999950 || bytes >= 999950) {
            bytes /= 1000
            ci.next()
        }
        return String.format("%.1f %cB", bytes / 1000.0, ci.current())
    }
}