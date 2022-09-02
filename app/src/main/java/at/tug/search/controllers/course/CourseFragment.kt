package at.tug.search.controllers.course

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import at.tug.search.activity.MainActivity
import at.tug.search.R
import at.tug.search.models.Course
import at.tug.search.utils.APIManager
import at.tug.search.utils.ObjectCache
import at.tug.search.utils.SearchCategory

class CourseFragment : Fragment(){

    var root: View? = null
    var lastQuery : String? = null
    var queryErrorText : TextView? = null
    var courseSearchView : SearchView? = null
    var handler : Handler = Handler()
    var listView : ListView? = null
    var courseProgressBar : ProgressBar? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        Handler().postDelayed({
            ObjectCache.inAnimation = false}, 350
        )

        if (root != null)
        {
            val inputMethodManager : InputMethodManager = (activity as MainActivity).getSystemService(
                Context.INPUT_METHOD_SERVICE) as InputMethodManager
            (activity as MainActivity).openKeyboard()
            val current = (activity as MainActivity).currentFocus
            current?.let {
                inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
            }
            courseSearchView?.requestFocus()

            return root
        }

        root = inflater.inflate(R.layout.fragment_course, container, false)
        listView = root!!.findViewById(R.id.listViewCourse)
        courseProgressBar = root!!.findViewById(R.id.courseProgressBar)
        queryErrorText = root!!.findViewById(R.id.courseQueryErrorText)
        courseSearchView = root!!.findViewById(R.id.courseSearchBar) as SearchView

        return root
    }

    override fun onStart() {
        super.onStart()

        ObjectCache.actualFragment = "LV"

        checkInternetConnection()

        listView?.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                // value of item that is clicked
                lastQuery =  (activity as MainActivity).getQueryText()

                val itemValue = listView?.getItemAtPosition(position) as Course

                val args = Bundle()
                args.putString("url", itemValue.lvDetailCAMPUSonlineURL)
                args.putString("title", itemValue.courseName)
                ObjectCache.inAnimation = true

                view.findNavController().navigate(R.id.action_nav_lv_to_detail_web_view, args)
            }


        courseSearchView?.setOnQueryTextListener( object : SearchView.OnQueryTextListener {

            override fun onQueryTextChange(query: String): Boolean {
                downloadCourse(query, true)
                return false
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                downloadCourse(query, false)
                return false
            }
        })

        ObjectCache.FragmentErrorIndicator = {
            checkInternetConnection()
        }
    }

    fun downloadCourse(query: String, keyboardOpen : Boolean) {

        if(ObjectCache.internetConnected) {
            if(query.count() == 0)
            {
                listView?.adapter = null

                if(ObjectCache.internetConnected)
                {
                    queryErrorText?.visibility = View.INVISIBLE
                }
            }
            else if(query.count() > 2) {
                ObjectCache.lastSearchedCategory = SearchCategory.COURSE
                ObjectCache.searchedCourses.clear()
                handler.removeCallbacksAndMessages(null)
                if(courseProgressBar?.visibility == View.INVISIBLE) {
                    courseProgressBar?.visibility = View.VISIBLE
                }
                APIManager.instance.startDownloadCourse(context!!, courseSearchView?.query.toString(),
                    {
                        listView?.adapter = CourseAdapter(context!!,
                            R.layout.row_course, it)
                        ObjectCache.keyboardOpen = keyboardOpen
                        courseProgressBar?.visibility = View.INVISIBLE

                        if(it.isEmpty()){
                            queryErrorText?.text = getString(R.string.noResults)
                            queryErrorText?.visibility = View.VISIBLE
                        } else {
                            queryErrorText?.visibility = View.INVISIBLE
                        }
                    })
            } else if (query.count() > 0) {
                queryErrorText?.visibility = View.INVISIBLE
                handler.postDelayed({
                    queryErrorText?.text = getString(R.string.min3)
                    queryErrorText?.visibility = View.VISIBLE
                }, 1000)
            }
        }
    }

    private fun checkInternetConnection()
    {
        if(ObjectCache.internetConnected)
        {
            queryErrorText?.visibility = View.INVISIBLE
            courseSearchView?.onActionViewExpanded()
        }
        else
        {
            queryErrorText?.text = getString(R.string.noInternet)
            queryErrorText?.visibility = View.VISIBLE
            (activity as MainActivity).closeKeyboard()
            listView?.adapter = null
        }
    }
}
