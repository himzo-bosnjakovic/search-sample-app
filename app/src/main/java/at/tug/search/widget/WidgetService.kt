package at.tug.search.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import at.tug.search.R
import at.tug.search.utils.ObjectCache
import at.tug.search.utils.SearchCategory
import at.tug.search.widget.WidgetProvider.Companion.ACTION_CLICK
import at.tug.search.widget.items.WidgetNewsItem

class WidgetService : RemoteViewsService() {

    override fun onGetViewFactory(intent: Intent?) =
        WidgetRemoteViewsFactory(applicationContext, intent)

    inner class WidgetRemoteViewsFactory(private val context: Context, intent: Intent?) :
        RemoteViewsFactory {

        private val newsItems: MutableList<WidgetNewsItem> = arrayListOf()
        private var appWidgetId: Int = 0

        init {
            appWidgetId = intent?.getIntExtra(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID
            ) ?: 0
        }

        override fun onCreate() {
        }

        override fun onDataSetChanged() {
            when (ObjectCache.lastSearchedCategory) {
                SearchCategory.PERSON -> handlePersonSearch()
                SearchCategory.ROOM -> handleRoomSearch()
                SearchCategory.ORGANISATION -> handleOrganisationSearch()
                SearchCategory.COURSE -> handleCourseSearch()
                SearchCategory.NONE -> handleNewsSearch()
            }
        }

        private fun handleRoomSearch() {
            val searchedRooms = ObjectCache.searchedRooms.map {
                WidgetNewsItem(it.additionalInformation ?: "", it.address ?: "", "")
            }

            newsItems.clear()
            newsItems.addAll(searchedRooms)
        }

        private fun handleOrganisationSearch() {
            val searchedOrganisations = ObjectCache.searchedOrganisations.map {
                WidgetNewsItem(it.name ?: "", it.email ?: "", "")
            }

            newsItems.clear()
            newsItems.addAll(searchedOrganisations)
        }

        private fun handleCourseSearch() {
            val searchedCourses = ObjectCache.searchedCourses.map {
                WidgetNewsItem(it.courseName ?: "", it.courseCAMPUSonlineURL ?: "", "")
            }

            newsItems.clear()
            newsItems.addAll(searchedCourses)
        }

        private fun handlePersonSearch() {
            val searchedPersons = ObjectCache.searchedPersons.map {
                WidgetNewsItem(it.personName ?: "", it.email_person ?: "", "")
            }

            newsItems.clear()
            newsItems.addAll(searchedPersons)
        }

        private fun handleNewsSearch() {
            newsItems.clear()
            val news = ObjectCache.newsList?.toList() ?: arrayListOf()
            news.take(15).forEach {

                val title = (it.title ?: "").substringAfter("]").trim()
                val publishDate = (it.pubDate ?: "").substringBefore("+").trim()
                val url = it.link ?: ""
                val widgetItem = WidgetNewsItem(title, publishDate, url)
                newsItems.add(widgetItem)
            }
        }

        override fun onDestroy() {
            newsItems.clear()
        }

        override fun getCount() = newsItems.size

        override fun getViewAt(position: Int): RemoteViews {
            val remoteViews = RemoteViews(context.packageName, R.layout.widget_news_item)
            if (position > count) return remoteViews
            val news = newsItems[position]
            remoteViews.setTextViewText(R.id.news_title, news.title)
            remoteViews.setTextViewText(R.id.news_publish_date, news.publishDate)

            when (ObjectCache.lastSearchedCategory) {
                SearchCategory.PERSON -> remoteViews.setTextViewText(R.id.category, "Persons")
                SearchCategory.ROOM -> remoteViews.setTextViewText(R.id.category, "Rooms")
                SearchCategory.ORGANISATION -> remoteViews.setTextViewText(R.id.category, "Organisations")
                SearchCategory.COURSE -> remoteViews.setTextViewText(R.id.category, "Courses")
                SearchCategory.NONE -> remoteViews.setTextViewText(R.id.category, "News")
            }


            val fillInIntent = Intent().putExtra("url", news.url)
            fillInIntent.action = ACTION_CLICK
            remoteViews.setOnClickFillInIntent(R.id.widget_news_item_layout, fillInIntent)

            return remoteViews
        }

        override fun getLoadingView() = null

        override fun getViewTypeCount() = 1

        override fun getItemId(position: Int) = position.toLong()

        override fun hasStableIds() = true
    }
}
