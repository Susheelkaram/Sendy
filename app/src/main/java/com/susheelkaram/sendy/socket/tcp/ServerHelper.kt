package com.susheelkaram.sendy.socket.tcp

import android.app.Activity
import android.content.Context
import android.util.Log
import com.susheelkaram.sendy.utils.Constants
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.ServerSocket
import java.net.Socket
import kotlin.concurrent.thread

/**
 * Created by Susheel Kumar Karam
 * Website - SusheelKaram.com
 */

class ServerHelper(context: Context) {
    private val LOG_TAG: String? = "ServerHelper"
    private var serverSocket: ServerSocket? = null
    private var socket: Socket? = null

    private var dataInterface = context as DataInterface

    suspend fun startServer() {
        try {
            if(serverSocket == null) serverSocket = ServerSocket(Constants.PORT)
            Log.d(LOG_TAG, "Server Started")
            dataInterface.onMessageReceived("Server Started")

            thread {
                socket = (serverSocket as ServerSocket).accept()
                val input = BufferedReader(InputStreamReader(socket?.getInputStream()))

                Log.d(LOG_TAG, "Client connected")

                (dataInterface as Activity).runOnUiThread() {
                    dataInterface.onMessageReceived("Client connected")
                }
                while (true) {
                    val message = input.readLine()

                    Log.d(LOG_TAG, "Message -> $message")

                    (dataInterface as Context).run {
                        dataInterface.onMessageReceived(message)
                    }

                    if (message?.toLowerCase() == "stop") {
                        print("Client closed connection")
                        socket?.close()
                        break
                    }
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    suspend fun stopServer() {
        try {
            serverSocket?.close()
            socket?.close()
            Log.d(LOG_TAG, "Server Stopped")
            dataInterface.onMessageReceived("Server Stopped")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun playAudio() {

    }

    interface DataInterface {
        fun onMessageReceived(message: String)
    }
}