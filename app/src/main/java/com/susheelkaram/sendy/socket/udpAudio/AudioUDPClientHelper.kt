package com.susheelkaram.sendy.socket.udpAudio

import android.media.AudioRecord
import android.util.Log
import com.susheelkaram.sendy.utils.Constants
import java.io.*
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

/**
 * Created by Susheel Kumar Karam
 * Website - SusheelKaram.com
 */
class AudioUDPClientHelper {
    private val LOG_TAG: String? = "AudioUDPClientHelper"
    private var out: OutputStream? = null
    private var datagramSocket: DatagramSocket? = null
    private var inetAddress: InetAddress? = null

    suspend fun connect(ip: String) {
        datagramSocket = DatagramSocket(Constants.PORT)
        inetAddress = InetAddress.getByName(ip)
    }

    suspend fun sendAudio(audioRecord: AudioRecord) {
        var sending: Boolean = true

        var buffer : ByteArray = ByteArray(4096)

        while(sending){
            var bufferSize = audioRecord.read(buffer, 0, buffer.size)
            var datagramPacket = DatagramPacket(buffer, buffer.size, inetAddress, Constants.PORT)
            Log.d(LOG_TAG, "Received: packet siz ${datagramPacket.length}")
            datagramSocket?.send(datagramPacket)
        }
    }
}