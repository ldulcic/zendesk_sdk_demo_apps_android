package com.example.zendesk_sdk_click_handler_demo

import android.annotation.SuppressLint
import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.WindowCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import zendesk.android.Zendesk
import zendesk.android.messaging.Messaging
import zendesk.android.messaging.MessagingDelegate
import zendesk.android.messaging.UrlSource
import zendesk.logger.Logger
import zendesk.messaging.android.DefaultMessagingFactory

class MainActivity : AppCompatActivity() {

    val LOG_TAG = "[${this.javaClass.name}]"

    private var coordinatorLayout: CoordinatorLayout? = null

    @SuppressLint("CutPasteId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.topAppBar))
        coordinatorLayout = findViewById<CoordinatorLayout>(R.id.coordinatorLayout)

        // https://developer.zendesk.com/documentation/zendesk-web-widget-sdks/sdks/android/getting_started/#troubleshooting
        Logger.setLoggable(true)

        // https://developer.zendesk.com/documentation/zendesk-web-widget-sdks/sdks/android/getting_started/#initialize-the-sdk
        findViewById<Button>(R.id.InitButton).setOnClickListener {
            initializeZendesk()
        }

        // https://developer.zendesk.com/documentation/zendesk-web-widget-sdks/sdks/android/getting_started/#show-the-conversation
        findViewById<Button>(R.id.StartButton).setOnClickListener {
            Zendesk.instance.messaging.showMessaging(this)
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.top_app_bar, menu)
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.about -> {
            MaterialAlertDialogBuilder(this)
                .setTitle(R.string.about_title)
                .setMessage(R.string.about_message)
                .setPositiveButton("OK", null)
                .show()
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    // SDK related methods

    private fun initializeZendesk() {
        // https://developer.zendesk.com/documentation/zendesk-web-widget-sdks/sdks/android/getting_started/#initialize-the-sdk
        Zendesk.initialize(this, this.getString(R.string.channel_key), successCallback = { zendesk ->
            Log.i(LOG_TAG, getString(R.string.msg_init_success))
            addClickHandler()
            coordinatorLayout?.let {
                Snackbar.make(it, getString(R.string.msg_init_success), Snackbar.LENGTH_LONG).show()
            }
        }, failureCallback = { error ->
            // Tracking the cause of exceptions in your crash reporting dashboard will help to triage any unexpected failures in production
            Log.e(LOG_TAG, "${getString(R.string.msg_init_error)}: $error")
            coordinatorLayout?.let {
                Snackbar.make(it, getString(R.string.msg_init_error), Snackbar.LENGTH_LONG).show()
            }
        }, messagingFactory = DefaultMessagingFactory())
    }

    // https://developer.zendesk.com/documentation/zendesk-web-widget-sdks/sdks/android/advanced_integration/#clickable-links-delegate
    private fun addClickHandler(){
        Messaging.setDelegate(object : MessagingDelegate() {
            override fun shouldHandleUrl(url: String, urlSource: UrlSource): Boolean {
                // Your custom action...
                Toast.makeText(applicationContext, getString(R.string.msg_click_toast), Toast.LENGTH_LONG).show()
                Log.d(LOG_TAG, getString(R.string.msg_click_toast) + " - url: $url - urlSource: $urlSource")
                // Return false to prevent the SDK from handling the URL automatically
                // Return true to allow the SDK to handle the URL automatically, even
                // if you have done something custom
                return false
            }
        })
    }
}