package com.aswindev.epicreads

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Settings
import com.google.android.material.snackbar.Snackbar

interface BroadcastMessageCallback {
    fun showMessage(str: String)
}

class CustomBroadcastReceiver(val callback: BroadcastMessageCallback) : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent != null) {
            when (intent.action) {
                Intent.ACTION_AIRPLANE_MODE_CHANGED -> {
                    val airplaneModeOn = Settings.System.getInt(
                        context?.contentResolver,
                        Settings.Global.AIRPLANE_MODE_ON, 0) != 0
                    if (airplaneModeOn) {
                        callback.showMessage("Airplane mode is On")
                    } else {
                        callback.showMessage("Airplane mode is Off")
                    }
                }
                Intent.ACTION_BATTERY_LOW -> {
                    callback.showMessage("Your Phone Battery is Low")
                }
            }
        }
    }

}