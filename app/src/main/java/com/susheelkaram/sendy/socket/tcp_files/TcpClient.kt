package com.susheelkaram.sendy.socket.tcp_files

import android.util.Log
import com.susheelkaram.sendy.utils.Constants
import com.susheelkaram.sendy.utils.FileUtils
import java.io.DataOutputStream
import java.io.File
import java.io.FileInputStream
import java.lang.Exception
import java.net.Socket
import kotlin.concurrent.thread

/**
 * Created by Susheel Kumar Karam
 * Website - SusheelKaram.com
 */
class TcpClient {
    private val LOG_TAG = "TcpClient"
    var socket: Socket? = null
    var isSending: Boolean = true

    suspend fun connect(host: String) {
        try {
            if(!(socket?.isConnected ?: false)) {
                socket = Socket(host, Constants.PORT)
                Log.d(LOG_TAG, "Connected to $host")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun sendFile(path: String) {
        thread {
            try {
                var file = File(path)
                var fileInputStream = FileInputStream(file)
                var dataOutputStream = DataOutputStream(socket?.getOutputStream())

                var buffer = ByteArray(Constants.BUFFER_SIZE)

                // Sending file name
                dataOutputStream.writeUTF(file.name)

                // Sending file size
                dataOutputStream.writeLong(file.length())

                var count = 0
                // Sending file
                while (isSending) {
                    count = fileInputStream.read(buffer, 0, buffer.size)

                    if (count < 0) {
                        isSending = false
                        Log.d(LOG_TAG, "Stopped sending data")
                        break
                    }
                    dataOutputStream.write(buffer, 0, Math.min(count, buffer.size))
                    Log.d(LOG_TAG, "Sending ${file.name} - ${FileUtils.getFileSizeString(file.length())}")
                }

                fileInputStream.close()
                dataOutputStream.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun disconnect() {
        socket?.close()
    }
}