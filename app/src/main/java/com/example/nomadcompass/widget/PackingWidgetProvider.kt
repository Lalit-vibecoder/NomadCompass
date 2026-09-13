package com.example.nomadcompass.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.RemoteViews
import com.example.nomadcompass.R
import com.example.nomadcompass.data.local.NomadDatabase
import com.example.nomadcompass.ui.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PackingWidgetProvider : AppWidgetProvider() {

    companion object {
        const val ACTION_TOGGLE_PACKING_ITEM = "com.example.nomadcompass.action.TOGGLE_PACKING_ITEM"
        const val ACTION_REFRESH_PACKING_WIDGET = "com.example.nomadcompass.action.REFRESH_PACKING_WIDGET"
        const val EXTRA_ITEM_ID = "com.example.nomadcompass.extra.ITEM_ID"
        const val EXTRA_ITEM_IS_PACKED = "com.example.nomadcompass.extra.ITEM_IS_PACKED"

        /**
         * Helper to trigger widget refresh from anywhere in the app (e.g., repository or viewmodel)
         */
        fun notifyDataChanged(context: Context) {
            val intent = Intent(context, PackingWidgetProvider::class.java).apply {
                action = ACTION_REFRESH_PACKING_WIDGET
            }
            context.sendBroadcast(intent)
        }

        fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            val views = RemoteViews(context.packageName, R.layout.widget_packing_layout)

            // 1. Set RemoteViewsService adapter for ListView
            val serviceIntent = Intent(context, PackingWidgetService::class.java).apply {
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                data = Uri.parse(toUri(Intent.URI_INTENT_SCHEME))
            }
            views.setRemoteAdapter(R.id.widget_packing_list, serviceIntent)
            views.setEmptyView(R.id.widget_packing_list, R.id.widget_empty_view)

            // 2. Set PendingIntent template on ListView for item click (check/uncheck)
            val toggleIntent = Intent(context, PackingWidgetProvider::class.java).apply {
                action = ACTION_TOGGLE_PACKING_ITEM
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            }
            val togglePendingIntent = PendingIntent.getBroadcast(
                context,
                0,
                toggleIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
            )
            views.setPendingIntentTemplate(R.id.widget_packing_list, togglePendingIntent)

            // 3. Set Refresh Button PendingIntent
            val refreshIntent = Intent(context, PackingWidgetProvider::class.java).apply {
                action = ACTION_REFRESH_PACKING_WIDGET
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            }
            val refreshPendingIntent = PendingIntent.getBroadcast(
                context,
                1,
                refreshIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_refresh_button, refreshPendingIntent)

            // 4. Header Click opens Main App
            val appIntent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            val appPendingIntent = PendingIntent.getActivity(
                context,
                2,
                appIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_header, appPendingIntent)

            appWidgetManager.updateAppWidget(appWidgetId, views)

            // 5. Update header info and progress counts asynchronously
            updateHeaderInfo(context, appWidgetManager, appWidgetId)
        }

        fun updateHeaderInfo(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val db = NomadDatabase.getInstance(context)
                    val latestTrip = db.tripDao().getLatestTrip()
                    val items = if (latestTrip != null) {
                        db.packingItemDao().getPackingItemsListForTrip(latestTrip.id)
                    } else {
                        db.packingItemDao().getAllPackingItemsList()
                    }

                    val totalCount = items.size
                    val packedCount = items.count { it.isPacked }
                    val tripTitle = if (latestTrip != null && latestTrip.countryName.isNotBlank()) {
                        "${latestTrip.flagEmoji} ${latestTrip.countryName}".trim()
                    } else {
                        "Trip Essentials"
                    }

                    withContext(Dispatchers.Main) {
                        val partialViews = RemoteViews(context.packageName, R.layout.widget_packing_layout)
                        partialViews.setTextViewText(R.id.widget_trip_name, tripTitle)
                        partialViews.setTextViewText(R.id.widget_counter_text, "$packedCount/$totalCount")
                        appWidgetManager.partiallyUpdateAppWidget(appWidgetId, partialViews)
                    }
                } catch (_: Exception) {
                    // Ignore transient exceptions during update
                }
            }
        }
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds)
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        val appWidgetManager = AppWidgetManager.getInstance(context)
        val componentName = ComponentName(context, PackingWidgetProvider::class.java)
        val allWidgetIds = appWidgetManager.getAppWidgetIds(componentName)

        when (intent.action) {
            ACTION_TOGGLE_PACKING_ITEM -> {
                val itemId = intent.getLongExtra(EXTRA_ITEM_ID, -1L)
                val isCurrentlyPacked = intent.getBooleanExtra(EXTRA_ITEM_IS_PACKED, false)

                if (itemId != -1L) {
                    val pendingResult = goAsync()
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            val db = NomadDatabase.getInstance(context)
                            db.packingItemDao().updatePackedStatus(itemId, !isCurrentlyPacked)

                            withContext(Dispatchers.Main) {
                                // Notify ListView to refresh items
                                appWidgetManager.notifyAppWidgetViewDataChanged(allWidgetIds, R.id.widget_packing_list)

                                // Update counters on all widget instances
                                for (widgetId in allWidgetIds) {
                                    updateHeaderInfo(context, appWidgetManager, widgetId)
                                }
                            }
                        } catch (_: Exception) {
                            // Handled
                        } finally {
                            pendingResult.finish()
                        }
                    }
                }
            }

            ACTION_REFRESH_PACKING_WIDGET -> {
                appWidgetManager.notifyAppWidgetViewDataChanged(allWidgetIds, R.id.widget_packing_list)
                for (widgetId in allWidgetIds) {
                    updateHeaderInfo(context, appWidgetManager, widgetId)
                }
            }
        }
    }
}
