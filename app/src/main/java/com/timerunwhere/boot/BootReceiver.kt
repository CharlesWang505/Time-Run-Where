package com.timerunwhere.boot

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.timerunwhere.focus.FocusGuardService
import com.timerunwhere.focus.FocusStateStore
import com.timerunwhere.worker.WorkerScheduler

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        WorkerScheduler.schedulePeriodicSync(context)
        if (FocusStateStore(context).isActive()) {
            FocusGuardService.startExisting(context)
        }
    }
}
