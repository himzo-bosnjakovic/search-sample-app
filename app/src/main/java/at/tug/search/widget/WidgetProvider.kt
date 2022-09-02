package at.tug.search.widget

import android.annotation.SuppressLint
import android.app.PendingIntent.*
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.RemoteViews
import at.tug.search.R
import at.tug.search.activity.MainActivity
import at.tug.search.utils.APIManager
import at.tug.search.utils.ObjectCache

class WidgetProvider : AppWidgetProvider() {
    private var widgetOptions = Bundle()
    private var widgetId = 0
    private var widgetManager: AppWidgetManager? = null

    override fun onUpdate(
        context: Context?,
        appWidgetManager: AppWidgetManager?,
        appWidgetIds: IntArray?
    ) {
        context ?: return
        appWidgetManager ?: return
        appWidgetIds?.toList()?.forEach {
            updateAppWidget(context, appWidgetManager, it)
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds)
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        val remoteViews = RemoteViews(context.packageName, R.layout.tug_widget_layout)
        widgetManager = appWidgetManager
        widgetId = appWidgetId

        handleThemeUI(context, remoteViews)

        APIManager.instance.startDownloadNews(context) {
            ObjectCache.newsList?.clear()
            ObjectCache.newsList = it

            refreshWidgetData(context)
        }

        APIManager.instance.startDownloadEvent(context) {
            ObjectCache.eventList?.clear()
            ObjectCache.eventList = it


            // event handling
            val randomLatestEvent = ObjectCache.eventList?.toList()?.get((0..3).random())
            remoteViews.setTextViewText(
                R.id.event_title,
                randomLatestEvent?.title?.substringAfter("]")?.trim() ?: ""
            )
            remoteViews.setTextViewText(R.id.event_category, randomLatestEvent?.category ?: "")

            val eventClickIntent = Intent(context, MainActivity::class.java)
            eventClickIntent.action = "EventWebView"
            eventClickIntent.data = Uri.parse(randomLatestEvent?.link ?: "")
            val pendingIntent =
                getActivity(context, 0, eventClickIntent, 0)
            remoteViews.setOnClickPendingIntent(R.id.event_view, pendingIntent)

            // search handling
            val personClickIntent = Intent(context, MainActivity::class.java)
            personClickIntent.action = "SearchPerson"
            val personPendingIntent = getActivity(context, 0, personClickIntent, FLAG_CANCEL_CURRENT)
            remoteViews.setOnClickPendingIntent(R.id.person_search, personPendingIntent)

            val roomClickIntent = Intent(context, MainActivity::class.java)
            roomClickIntent.action = "SearchRoom"
            val roomPendingIntent = getActivity(context, 0, roomClickIntent, FLAG_CANCEL_CURRENT)
            remoteViews.setOnClickPendingIntent(R.id.room_search, roomPendingIntent)

            val courseClickIntent = Intent(context, MainActivity::class.java)
            courseClickIntent.action = "SearchCourse"
            val coursePendingIntent = getActivity(context, 0, courseClickIntent, FLAG_CANCEL_CURRENT)
            remoteViews.setOnClickPendingIntent(R.id.course_search, coursePendingIntent)

            val organizationClickIntent = Intent(context, MainActivity::class.java)
            organizationClickIntent.data = Uri.parse("SearchOrganization")
            organizationClickIntent.action = "SearchOrganization"
            val organizationPendingIntent =
                getActivity(context, 0, organizationClickIntent, FLAG_CANCEL_CURRENT)
            remoteViews.setOnClickPendingIntent(R.id.organization_search, organizationPendingIntent)
            appWidgetManager.updateAppWidget(appWidgetId, remoteViews)
        }

        widgetOptions = appWidgetManager.getAppWidgetOptions(appWidgetId)
        resizeWidget(remoteViews)

        appWidgetManager.updateAppWidget(appWidgetId, remoteViews)
    }

    override fun onAppWidgetOptionsChanged(
        context: Context?,
        appWidgetManager: AppWidgetManager?,
        appWidgetId: Int,
        newOptions: Bundle?
    ) {
        context ?: return
        appWidgetManager ?: return
        val remoteViews = RemoteViews(context.packageName, R.layout.tug_widget_layout)
        widgetOptions = newOptions ?: Bundle()
        resizeWidget(remoteViews)

        appWidgetManager.updateAppWidget(appWidgetId, remoteViews)
    }

    private fun resizeWidget(views: RemoteViews) {
        val minWidth = getDimension(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH)
        val minHeight = getDimension(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT)
        val maxWidth = getDimension(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH)

        when (getDimension(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT)) {
            in (0..180) -> {
                views.setViewVisibility(R.id.event_view, View.VISIBLE)
                views.setViewVisibility(R.id.search_views, View.GONE)
            }
            in (180..260) -> {
                views.setViewVisibility(R.id.event_view, View.VISIBLE)
                views.setViewVisibility(R.id.search_views, View.VISIBLE)
            }
            in (260..1000) -> {
                views.setViewVisibility(R.id.event_view, View.VISIBLE)
                views.setViewVisibility(R.id.search_views, View.VISIBLE)
            }
        }

    }

    private fun getDimension(dimension: String) = widgetOptions.getInt(dimension)

    override fun onReceive(context: Context?, intent: Intent?) {
        context ?: return
        if (ACTION_CLICK == intent?.action) {
            ObjectCache.clickedNewsUrl = intent.extras?.get("url") as? String ?: ""
            val mainActivityIntent = Intent(context, MainActivity::class.java)
            mainActivityIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(mainActivityIntent)
        }

        refreshWidgetData(context)
        super.onReceive(context, intent)
    }

    private fun handleThemeUI(context: Context, views: RemoteViews) {
        if ((context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES) {
            views.setInt(R.id.container, "setBackgroundResource", R.drawable.dark_background_rounded)
            views.setInt(R.id.logo_image, "setImageResource", R.drawable.tug_logo_light)
        } else {
            views.setInt(R.id.container, "setBackgroundResource", R.drawable.light_background_rounded)
            views.setInt(R.id.logo_image, "setImageResource", R.drawable.tug_logo)
        }
    }

    private fun refreshWidgetData(context: Context?) {
        context ?: return
        val remoteViews = RemoteViews(context.packageName, R.layout.tug_widget_layout)

        val serviceIntent = Intent(context, WidgetService::class.java)
        serviceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
        serviceIntent.data = Uri.parse(serviceIntent.toUri(Intent.URI_INTENT_SCHEME))

        val newsClickIntent = Intent(context, WidgetProvider::class.java)
        newsClickIntent.action = ACTION_CLICK
        val newsClickPendingIntent = getBroadcast(
            context, 0, newsClickIntent, 0
        )

        remoteViews.setRemoteAdapter(R.id.stack_view, serviceIntent)
        remoteViews.setPendingIntentTemplate(R.id.stack_view, newsClickPendingIntent)
        widgetManager?.notifyAppWidgetViewDataChanged(widgetId, R.id.stack_view)
        widgetManager?.updateAppWidget(widgetId, remoteViews)
    }

    companion object {
        const val ACTION_CLICK = "actionClick"
    }
}
