package com.susheelkaram.sendy.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.susheelkaram.sendy.R
import com.susheelkaram.sendy.databinding.ActivityReceiveFileBinding
import com.susheelkaram.sendy.socket.DataReceive
import com.susheelkaram.sendy.socket.tcp_files.TcpClient
import com.susheelkaram.sendy.socket.tcp_files.TcpServer
import com.susheelkaram.sendy.utils.FileManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.File

class ReceiveFileActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var B: ActivityReceiveFileBinding
    private val job = Job()
    val coroutineScope: CoroutineScope = CoroutineScope(job + Dispatchers.IO)

    var tcpServer: TcpServer = TcpServer(onDataReceived = object : DataReceive{
        override fun onFileReceived(name: String, size: Long, data: ByteArray) {
        }
    })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        B = DataBindingUtil.setContentView(this, R.layout.activity_receive_file)

        B.btnRecieveStartServer.setOnClickListener(this)

        createDir()
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.btn_RecieveStartServer -> startServer()
        }
    }

    fun startServer() {
        var dirPath = FileManager.getDownloadsDirectory(this) + "/Sendy"
        coroutineScope.launch {
            tcpServer.startServer(dirPath)
        }
    }

    fun createDir() {
        var dirPath = FileManager.getDownloadsDirectory(this)

        var sendyDir = File("$dirPath/Sendy")

        if(!sendyDir.exists()) {
            sendyDir.mkdir()
        }
    }
}
