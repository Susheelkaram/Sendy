package com.susheelkaram.sendy.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import com.susheelkaram.sendy.R
import com.susheelkaram.sendy.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var B: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        B = DataBindingUtil.setContentView(this, R.layout.activity_main)
        var pages: MutableList<ListItem> = mutableListOf(
            ListItem("Chat Activity", ChatActivity::class.java),
            ListItem("File Transfer Activity", FileTransferActivity::class.java),
            ListItem("File Receive Activity", ReceiveFileActivity::class.java)
        )

        var adapter : ArrayAdapter<ListItem> =  ArrayAdapter<ListItem>(this,android.R.layout.simple_list_item_1, pages)

        B.listViewPages.adapter = adapter
        B.listViewPages.setOnItemClickListener(
            object : AdapterView.OnItemClickListener {
                override fun onItemClick(parent: AdapterView<*>?, view: View?, index: Int, id: Long) {
                    var intent: Intent = Intent(this@MainActivity, pages[index].page)
                    startActivity(intent)
                }
            }
        )

        var intent: Intent = Intent(this, ChatActivity::class.java)
    }

    data class ListItem(val title: String, val page: Class<*>){
        override fun toString(): String {
            return title
        }
    }
}
