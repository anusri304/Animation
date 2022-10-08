package com.udacity

import android.app.NotificationManager
import android.os.Bundle
import android.view.View
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.udacity.ApplicationConstants.INTENT_DOWNLOAD_STATUS
import com.udacity.ApplicationConstants.INTENT_REPOSITORY_NAME
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*

class DetailActivity : AppCompatActivity() {
    private lateinit var radioGroup: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)

        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.cancelAll()

        val repoName = intent.getStringExtra(INTENT_REPOSITORY_NAME)

        fileNameDesc.text = repoName

        val status = intent.getStringExtra(INTENT_DOWNLOAD_STATUS)
        statusDesc.text = status

        buttonOK.setOnClickListener {
            finish()
        }
    }

}
