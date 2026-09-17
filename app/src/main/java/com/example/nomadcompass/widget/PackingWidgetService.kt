package com.example.nomadcompass.widget

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.example.nomadcompass.R
import com.example.nomadcompass.data.local.NomadDatabase
import com.example.nomadcompass.data.local.entity.PackingItemEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

class PackingWidgetService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        return PackingWidgetRemoteViewsFactory(this.applicationContext)
    }
}

class PackingWidgetRemoteViewsFactory(
    private val context: Context,
) : RemoteViewsService.RemoteViewsFactory {

    private val items = mutableListOf<PackingItemEntity>()

    override fun onCreate() {
        // Initialization handled in onDataSetChanged
    }

    override fun onDataSetChanged() {
        try {
            runBlocking(Dispatchers.IO) {
                val db = NomadDatabase.getInstance(context)
                val latestTrip = db.tripDao().getLatestTrip()
                val freshItems = if (latestTrip != null) {
                    db.packingItemDao().getPackingItemsListForTrip(latestTrip.id)
                } else {
                    db.packingItemDao().getAllPackingItemsList()
                }
                synchronized(items) {
                    items.clear()
                    items.addAll(freshItems)
                }
            }
        } catch (_: Exception) {
            // Retain existing items if database is temporarily busy
        }
    }

    override fun onDestroy() {
        synchronized(items) {
            items.clear()
        }
    }

    override fun getCount(): Int = synchronized(items) { items.size }

    override fun getViewAt(position: Int): RemoteViews? {
        val item = synchronized(items) {
            if (position in items.indices) items[position] else null
        } ?: return null

        val views = RemoteViews(context.packageName, R.layout.widget_packing_item)
        views.setTextViewText(R.id.widget_item_name, item.name)

        if (item.isPacked) {
            views.setImageViewResource(R.id.widget_item_checkbox, R.drawable.ic_widget_check_box)
            views.setTextColor(R.id.widget_item_name, Color.parseColor("#8E918F"))
        } else {
            views.setImageViewResource(R.id.widget_item_checkbox, R.drawable.ic_widget_check_box_outline)
            views.setTextColor(R.id.widget_item_name, Color.parseColor("#191C1B"))
        }

        // FillInIntent transmits the specific item ID and status to the PendingIntent template
        val fillInIntent = Intent().apply {
            putExtra(PackingWidgetProvider.EXTRA_ITEM_ID, item.id)
            putExtra(PackingWidgetProvider.EXTRA_ITEM_IS_PACKED, item.isPacked)
        }
        views.setOnClickFillInIntent(R.id.widget_item_row, fillInIntent)
        views.setOnClickFillInIntent(R.id.widget_item_checkbox, fillInIntent)

        return views
    }

    override fun getLoadingView(): RemoteViews? = null

    override fun getViewTypeCount(): Int = 1

    override fun getItemId(position: Int): Long = synchronized(items) {
        if (position in items.indices) items[position].id else position.toLong()
    }

    override fun hasStableIds(): Boolean = true
}
