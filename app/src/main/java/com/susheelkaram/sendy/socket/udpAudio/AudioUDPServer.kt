package com.susheelkaram.sendy.socket.udpAudio

import android.content.Context
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.util.Log
import com.susheelkaram.sendy.utils.Constants
import java.net.DatagramPacket
import java.net.DatagramSocket

/**
 * Created by Susheel Kumar Karam
 * Website - SusheelKaram.com
 */

class AudioUDPServer(context: Context) {
    private val LOG_TAG: String? = "AudioUDPServer"
    private var datagramSocket: DatagramSocket? = null
    private var isStreaming = true

    var mBufferSize = AudioTrack.getMinBufferSize(
        44100,
        AudioFormat.CHANNEL_OUT_MONO,
        AudioFormat.ENCODING_PCM_8BIT
    )
    private var audioTrack: AudioTrack = AudioTrack(
        AudioManager.STREAM_MUSIC, 44100,
        AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT,
        mBufferSize, AudioTrack.MODE_STREAM
    )


    suspend fun startServer() {
        datagramSocket = DatagramSocket(Constants.PORT)
        var buffer: ByteArray = ByteArray(4096)

        playAudio()

        while (isStreaming) {
            var packet: DatagramPacket = DatagramPacket(buffer, buffer.size)
            datagramSocket?.receive(packet)
            Log.d(LOG_TAG, "Received: packet siz ${packet.length}")
            feedAudio(buffer)
        }
    }

    suspend fun feedAudio(data: ByteArray) {
        audioTrack.write(data, 0, data.size)
    }

    suspend fun playAudio() {
        audioTrack.play()
    }
}