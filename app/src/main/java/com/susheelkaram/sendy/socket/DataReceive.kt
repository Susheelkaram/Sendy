package com.susheelkaram.sendy.socket

/**
 * Created by Susheel Kumar Karam
 * Website - SusheelKaram.com
 */
interface DataReceive {
    fun onFileReceived(name: String, size: Long, data: ByteArray)
}