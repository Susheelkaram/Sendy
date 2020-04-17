package com.susheelkaram.sendy.socket.tcp_files

import android.util.Log
import com.susheelkaram.sendy.utils.Constants
import com.susheelkaram.sendy.socket.DataReceive
import com.susheelkaram.sendy.utils.FileUtils
import java.io.DataInputStream
import java.io.File
import java.io.FileOutputStream
import java.net.ServerSocket
import java.net.Socket
import kotlin.concurrent.thread

/**
 * Created by Susheel Kumar Karam
 * Website - SusheelKaram.com
 */
class TcpServer(onDataReceived: DataReceive) {
    private val LOG_TAG = "TcpServer"

    var serverSocket: ServerSocket? = null
    var socket: Socket? = null
    var socketThread: Thread? = null

    var isListening: Boolean = true
    var onDataReceived = onDataReceived

    suspend fun startServer(dirToSave: String) {
        serverSocket = ServerSocket(Constants.PORT)

        socketThread = thread {
            // waiting for connection
            socket = serverSocket?.accept()

            Log.d(LOG_TAG, "Client connected - ${socket?.inetAddress.toString()}")

            var dataInputStream: DataInputStream = DataInputStream(socket?.getInputStream())

            while (isListening && (dataInputStream.available() > 0)) {
                receiveFile(dataInputStream, dirToSave)
            }

            dataInputStream.close()
        }
    }

    fun receiveFile(stream: DataInputStream, destination: String) {
        var fileName = stream.readUTF()
        var fileSize = stream.readLong()
        var remainingFileSize = fileSize

        var newFile = File(destination, fileName)
        var newFileOutputStream: FileOutputStream = FileOutputStream(newFile)

        var buffer = ByteArray(Constants.BUFFER_SIZE)
        var readCount = 0
        var isReadingFile = true

        while (remainingFileSize > 0 && isReadingFile) {
            readCount = stream.read(buffer, 0, buffer.size)
            if (readCount < 0) {
                isReadingFile = false
                break
            }

            newFileOutputStream.write(buffer, 0, Math.min(readCount, buffer.size))
            Log.d(LOG_TAG, "Receiving  $fileName -> ${FileUtils.getFileSizeString(newFile.length())}")
        }

        Log.d(LOG_TAG, "DONE - $fileName(${FileUtils.getFileSizeString(newFile.length())}) Received")

        newFileOutputStream.close()
    }


    suspend fun stopListening() {
        isListening = true
    }

    suspend fun stop() {
        socket?.close()
        serverSocket?.close()
    }
}