package at.tug.search.utils

import android.widget.TextView
import at.tug.search.models.Event
import at.tug.search.models.News

object ObjectCache {

    var keyboardOpen : Boolean = false
    var actualFragment : String = ""
    var eventList : ArrayList<Event>? = null
    var newsList : ArrayList<News>? = null
    var internetConnected : Boolean = false
    var inAnimation : Boolean = false
    var FragmentErrorIndicator: ()->Unit = {}
}