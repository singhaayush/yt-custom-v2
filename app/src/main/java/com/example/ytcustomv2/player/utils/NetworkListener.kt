package com.example.ytcustomv2.player.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class NetworkListener : BroadcastReceiver() {

    var onNetworkUnavailable = { }
    var onNetworkAvailable = { }

    override fun onReceive(context: Context, intent: Intent) {
        if (Utils.isOnline(context))
            onNetworkAvailable()
        else
            onNetworkUnavailable()
    }
}