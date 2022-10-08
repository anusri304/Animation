package com.udacity

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0

    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action

    private var selectedRepoURL: String? = null

    private var selectedFileName: String? = null
    lateinit var downloadManager: DownloadManager
    private lateinit var radioGroup: RadioGroup
    private val TAG = this::class.java.simpleName

    private var downloadStatus = "";


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        radioGroup = findViewById<View>(R.id.radioGroup) as RadioGroup
        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
        initLoadingButton()
        initRadioGroup()
    }

//    override fun onResume() {
//        super.onResume()
//        createChannel(
//            getString(R.string.notification_channel_id),
//            getString(R.string.notification_channel_name)
//        )
//    }

    private fun initLoadingButton() {
        custom_button.setOnClickListener {

            if (selectedRepoURL != null) {
                custom_button.buttonState = ButtonState.Loading
                download()

            } else {
                Toast.makeText(
                    applicationContext,
                    getString(R.string.validation_file_message),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun createChannel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            )
                .apply {
                    setShowBadge(false)
                }

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = getString(R.string.notification_channel_name)


            notificationManager.createNotificationChannel(notificationChannel)

        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)

            custom_button.buttonState = ButtonState.Completed
            if (id?.let { isValidDownload(it) } == true) {
                downloadStatus = getString(R.string.successful_download)
                Toast.makeText(
                    applicationContext,
                    getString(R.string.notification_description),
                    Toast.LENGTH_LONG
                ).show()
                notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

                notificationManager.sendNotification(
                    getString(R.string.notification_description),
                    applicationContext,
                    selectedFileName!!,
                    downloadStatus
                )
            } else {
                downloadStatus = getString(R.string.failed_download)
                Toast.makeText(
                    applicationContext,
                    getString(R.string.failed_download),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun isValidDownload(downloadId: Long): Boolean {
        Log.d(TAG, "Checking download status for id: $downloadId")

        //Verify if download is a success
        val c: Cursor = downloadManager.query(DownloadManager.Query().setFilterById(downloadId))
        if (c.moveToFirst()) {
            val status: Int = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS))
            return if (status == DownloadManager.STATUS_SUCCESSFUL) {
                true //Download is valid, celebrate
            } else {
                val reason: Int = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_REASON))
                Log.d(TAG, "Download not correct, status [$status] reason [$reason]")
                false
            }
        }
        return false
    }

//    fun onRadioButtonChecked(view: View) {
//            when (view.getId()) {
//                R.id.radioButtonGlide ->
//                    selectedRepoURL = GLIDE_URL
//                R.id.radioButtonUdacity ->
//                    selectedRepoURL = UDACITY_URL
//                R.id.radioButtonRetrofit ->
//                    selectedRepoURL = RETROFIT_URL
//            }
//
//            selectedFileName = view.text.toString()
//    }




    private fun download() {
        val request =
            DownloadManager.Request(Uri.parse(selectedRepoURL))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        downloadID =
            downloadManager.enqueue(request)// enqueue puts the download request in the queue.
    }

    companion object {
        private const val UDACITY_URL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val GLIDE_URL =
            "https://github.com/bumptech/glide/archive/refs/heads/master.zip"
        private const val RETROFIT_URL =
            "https://github.com/square/retrofit/archive/refs/heads/master.zip"

    }

    private fun initRadioGroup() {
        radioGroup.setOnCheckedChangeListener(
            RadioGroup.OnCheckedChangeListener { group, checkedId ->
                val radio: RadioButton = findViewById(checkedId)
                selectedFileName = radio.text.toString()

                when (checkedId) {
                    R.id.radioButtonGlide ->
                        selectedRepoURL = GLIDE_URL
                    R.id.radioButtonUdacity ->
                        selectedRepoURL = UDACITY_URL
                    R.id.radioButtonRetrofit ->
                        selectedRepoURL = RETROFIT_URL
                }
            })

    }

}
