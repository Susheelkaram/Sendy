package com.susheelkaram.sendy.utils

import java.math.RoundingMode

/**
 * Created by Susheel Kumar Karam
 * Website - SusheelKaram.com
 */
class FileUtils {
    enum class DataUnit {
        B, KB, MB, GB
    }

    companion object {
        fun getFileSize(bytes: Long) : Pair<Double, DataUnit>{
            val doubleBytes = bytes.toDouble()
            return when(bytes) {
                in 1..1024 -> Pair(doubleBytes, DataUnit.B)
                in 1024..1024*1024 -> Pair((doubleBytes / 1024), DataUnit.KB)
                in 1024*1024..1024*1024*1024 -> Pair((doubleBytes / (1024*1024)), DataUnit.MB)
                in 1024*1024*1024..Long.MAX_VALUE -> Pair((doubleBytes / (1024*1024*1024)), DataUnit.GB)
                else -> Pair(doubleBytes, DataUnit.B)
            }
        }

        fun getFileSizeString(bytes: Long): String {
            var size: Pair<Double, FileUtils.DataUnit> = FileUtils.getFileSize(bytes)
            return "${size.first.toBigDecimal().setScale(1, RoundingMode.UP).toDouble()} ${size.second}"
        }
    }
}