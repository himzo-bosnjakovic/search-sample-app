package at.tug.search.utils

import android.widget.TextView
import at.tug.search.models.*

object ObjectCache {

    var keyboardOpen : Boolean = false
    var actualFragment : String = ""
    var eventList : ArrayList<Event>? = null
    var newsList : ArrayList<News>? = null
    var clickedNewsUrl : String = ""
    var internetConnected : Boolean = false
    var inAnimation : Boolean = false
    var FragmentErrorIndicator: ()->Unit = {}

    var lastSearchedCategory = SearchCategory.NONE
    var searchedPersons: ArrayList<Person> = arrayListOf()
    var searchedRooms: ArrayList<Room> = arrayListOf()
    var searchedOrganisations: ArrayList<Organisation> = arrayListOf()
    var searchedCourses: ArrayList<Course> = arrayListOf()
}
