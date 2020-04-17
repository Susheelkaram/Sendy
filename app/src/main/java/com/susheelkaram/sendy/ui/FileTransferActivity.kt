package com.susheelkaram.sendy.ui

import android.content.ClipData
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import com.susheelkaram.sendy.R
import com.susheelkaram.sendy.databinding.ActivityFileTransferBinding
import com.susheelkaram.sendy.socket.tcp_files.TcpClient
import com.susheelkaram.sendy.utils.FileManager
import com.susheelkaram.sendy.utils.FileUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.File
import java.math.RoundingMode
import java.util.jar.Manifest


class FileTransferActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var B: ActivityFileTransferBinding

    val pickFileCode = 25
    var selectedFilePaths: MutableList<String> = mutableListOf()

    private val job = Job()
    val coroutineScope: CoroutineScope = CoroutineScope(job + Dispatchers.IO)

    var tcpClient: TcpClient = TcpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        B = DataBindingUtil.setContentView(this, R.layout.activity_file_transfer)

        B.btnPickFile.setOnClickListener(this)
        B.btnSendFile.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_PickFile -> pickFile()
            R.id.btn_SendFile -> sendFile()
        }
    }


    private fun pickFile() {

        // TODO: Add support to select multiple files
        ActivityCompat.requestPermissions(
            this@FileTransferActivity,
            arrayOf(
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            ),
            5
        )
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.type = "*/*"
        val i = Intent.createChooser(intent, "File")
        startActivityForResult(i, pickFileCode)
    }

    private fun sendFile() {
        for (path in selectedFilePaths) {
            coroutineScope.launch {
                tcpClient.connect(B.inputHost.text.toString())
                tcpClient.sendFile(path)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            pickFileCode -> onFilePathReceived(data)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun onFilePathReceived(data: Intent?) {
        selectedFilePaths.clear()

        if (data?.clipData != null) {

            var allSelectedString = ""
            var clipData = (data.clipData as ClipData)

            for (i in 0 until clipData.itemCount) {
                setFileDataFromUri(clipData.getItemAt(i).uri)
            }
            B.textPickedFileName.text = allSelectedString
        } else {
            data?.data?.let {
                setFileDataFromUri(it)
            }
        }
    }

    fun setFileDataFromUri(uri: Uri) {
        var filePath: String? = FileManager.getPathFromUri(this, uri)

        if (filePath.isNullOrEmpty()) return

        selectedFilePaths.add(filePath)
        var file: File = File(filePath)
        var size: Pair<Double, FileUtils.DataUnit> = FileUtils.getFileSize(file.length())
        var sizeString: String = "${size.first.toBigDecimal().setScale(1, RoundingMode.UP)
            .toDouble()} ${size.second}"
        B.textPickedFileName.text = "${filePath ?: "Cannot get path"} $sizeString"
    }

}
