package com.cybersecdaily.widget

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.updateAll
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class CyberSecDailyWidgetReceiver : GlanceAppWidgetReceiver() {

    override val glanceAppWidget: CyberSecDailyWidget = CyberSecDailyWidget()

    override fun onUpdate(context: Context, appWidgetManager: android.appwidget.AppWidgetManager, appWidgetIds: IntArray) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        // Schedule periodic background refresh every 3 hours
        schedulePeriodicRefresh(context)
    }

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        schedulePeriodicRefresh(context)
        // Do an immediate refresh
        CoroutineScope(Dispatchers.IO).launch {
            glanceAppWidget.updateAll(context)
        }
    }

    override fun onDisabled(context: Context) {
        super.onDisabled(context)
        WorkManager.getInstance(context).cancelUniqueWork("cybersec_daily_refresh")
    }

    private fun schedulePeriodicRefresh(context: Context) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val refreshRequest = PeriodicWorkRequestBuilder<WidgetUpdateWorker>(
            repeatInterval = 3, TimeUnit.HOURS,
            flexTimeInterval = 30, TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "cybersec_daily_refresh",
            ExistingPeriodicWorkPolicy.KEEP,
            refreshRequest
        )
    }
}
