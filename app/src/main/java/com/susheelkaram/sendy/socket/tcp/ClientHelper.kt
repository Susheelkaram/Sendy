package com.susheelkaram.sendy.socket.tcp

import com.susheelkaram.sendy.utils.Constants
import java.io.PrintWriter
import java.lang.Exception
import java.net.Socket

/**
 * Created by Susheel Kumar Karam
 * Website - SusheelKaram.com
 */
class ClientHelper {
    private var out: PrintWriter? = null

    suspend fun connectToServer(host: String) {
        try {
            val socket: Socket = Socket(host, Constants.PORT)
            out = PrintWriter(socket.getOutputStream())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun sendMessage(message: String) {
        out?.println(message)
        out?.flush()
    }
}