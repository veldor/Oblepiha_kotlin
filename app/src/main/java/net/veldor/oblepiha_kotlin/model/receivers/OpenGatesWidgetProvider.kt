package net.veldor.oblepiha_kotlin.model.receivers

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import net.veldor.oblepiha_kotlin.R
import net.veldor.oblepiha_kotlin.view.ContentActivity

class OpenGatesWidgetProvider: AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // Perform this loop procedure for each App Widget that belongs to this provider
        appWidgetIds.forEach { appWidgetId ->
            // Create an Intent to launch ExampleActivity
            val intent = Intent(context, MiscReceiver::class.java)
            intent.putExtra(MiscReceiver.EXTRA_ACTION, MiscReceiver.ACTION_OPEN_GATES)
            val pendingIntent: PendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0)

            // Get the layout for the App Widget and attach an on-click listener
            // to the button
            val views: RemoteViews = RemoteViews(
                context.packageName,
                R.layout.open_gates_appwidget
            ).apply {
                setOnClickPendingIntent(R.id.openGatesBtn, pendingIntent)
            }

            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}