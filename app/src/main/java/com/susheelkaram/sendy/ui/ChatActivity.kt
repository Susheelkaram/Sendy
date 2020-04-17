package com.susheelkaram.sendy.ui

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.media.*
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.susheelkaram.sendy.R
import com.susheelkaram.sendy.databinding.ActivityChatBinding
import com.susheelkaram.sendy.socket.udpAudio.AudioUDPClientHelper
import com.susheelkaram.sendy.socket.udpAudio.AudioUDPServer
import com.susheelkaram.sendy.socket.tcp.ServerHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


class ChatActivity : AppCompatActivity(), View.OnClickListener, ServerHelper.DataInterface {
    lateinit var B: ActivityChatBinding

    var context: Context = this

    private val job = Job()
    val coroutineScope: CoroutineScope = CoroutineScope(job + Dispatchers.IO)
    private val serverHelper =
        AudioUDPServer(this)
    private val clientHelper =
        AudioUDPClientHelper()

    var mBufferSize = AudioRecord.getMinBufferSize(
        44100,
        AudioFormat.CHANNEL_IN_MONO,
        AudioFormat.ENCODING_PCM_8BIT
    )
    private val audioRecord: AudioRecord = AudioRecord(MediaRecorder.AudioSource.MIC, 44100,
        AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT,
        mBufferSize)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        B = DataBindingUtil.setContentView(this,
            R.layout.activity_chat
        )

        B.btnStartServer.setOnClickListener(this)
        B.btnConnect.setOnClickListener(this)
        B.btnSend.setOnClickListener(this)
        B.btnStopServer.setOnClickListener(this)
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_StartServer -> {
                coroutineScope.launch {
                    serverHelper.startServer()
                }
            }

            R.id.btn_Connect -> {
                if (B.inputHost.text.toString().isNotEmpty()) {
                    coroutineScope.launch {
                        clientHelper.connect(B.inputHost.text.toString())
                        audioRecord.startRecording()
                        clientHelper.sendAudio(audioRecord)
                    }
                }
            }

            R.id.btn_Send -> {
                if (B.inputMessage.text.toString().isNotEmpty()) {
                    coroutineScope.launch {
                    }
                }
            }

            R.id.btn_StopServer -> {
                coroutineScope.launch {
//                    serverHelper.stop()
                }
            }
        }
    }

    override fun onMessageReceived(message: String) {
        coroutineScope.launch(Dispatchers.Main) {
            val toast: Toast = Toast
                .makeText(context, message, Toast.LENGTH_SHORT)

            toast.view.background =
                ColorDrawable(ContextCompat.getColor(context, android.R.color.holo_blue_dark))

            val text: TextView = toast.view.findViewById(android.R.id.message)
            text.setTextColor(ContextCompat.getColor(context, android.R.color.white))

            toast.show()
        }
    }

}
