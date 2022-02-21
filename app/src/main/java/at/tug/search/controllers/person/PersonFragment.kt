package at.tug.search.controllers.person

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.core.view.drawToBitmap
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import at.tug.search.activity.MainActivity
import at.tug.search.models.Person
import at.tug.search.utils.ObjectCache
import at.tug.search.R
import at.tug.search.utils.APIManager
import java.lang.Exception


class PersonFragment : Fragment(){

    var root: View? = null
    var listView : ListView? = null
    var personProgressBar : ProgressBar? = null
    var queryErrorText : TextView? = null
    var lastQuery : String? = ""
    var personSearchView : SearchView? = null
    var handler : Handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (activity as MainActivity).setActionBarTitle("Person")
    }

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
            (activity as MainActivity).setQuery(lastQuery!!)

            if (root!!.parent != null)
            {
                (root!!.parent as ViewGroup).removeView(root)
            }

            if(ObjectCache.keyboardOpen)
            {
                val inputMethodManager : InputMethodManager = (activity as MainActivity).getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                (activity as MainActivity).openKeyboard()
                val current = (activity as MainActivity).currentFocus
                current?.let {
                    inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
                }
                personSearchView?.requestFocus()
            }

            return root
        }

        root = inflater.inflate(R.layout.fragment_person, container, false)
        listView = root!!.findViewById(R.id.listViewPerson)
        personProgressBar = root!!.findViewById(R.id.personProgressBar)
        queryErrorText = root!!.findViewById(R.id.personQueryErrorText)
        personSearchView = root!!.findViewById(R.id.personSearchBar) as SearchView

        return root
    }

    override fun onStart() {
        super.onStart()
        ObjectCache.actualFragment = "Person"

        checkInternetConnection()

        listView?.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                // value of item that is clicked
                lastQuery =  (activity as MainActivity).getQueryText()

                val itemValue = listView?.getItemAtPosition(position) as Person
                itemValue.image = (view.findViewById(R.id.personImage) as ImageView).drawToBitmap()

                val args = Bundle()
                args.putParcelable("person", itemValue)

                ObjectCache.inAnimation = true
                view.findNavController().navigate(R.id.action_nav_person_to_person_detail, args)

            }

        personSearchView?.setOnQueryTextListener( object : SearchView.OnQueryTextListener {

            override fun onQueryTextChange(query: String): Boolean {
                downloadPerson(query, true)
                return false
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                downloadPerson(query, false)
                return false
            }
        })

        ObjectCache.FragmentErrorIndicator = {
            checkInternetConnection()
        }
    }

    fun downloadPerson(query: String, keyboardOpen : Boolean) {

        if(ObjectCache.internetConnected) {
            if(query.count() == 0)
            {
                listView?.adapter = null

                if(ObjectCache.internetConnected)
                {
                    queryErrorText?.visibility = View.INVISIBLE
                }
            }
            else if (query.count() > 2) {
                handler.removeCallbacksAndMessages(null)
                if (personProgressBar?.visibility == View.INVISIBLE) {
                    personProgressBar?.visibility = View.VISIBLE
                }
                APIManager.instance.startDownloadPerson(context!!,
                    personSearchView?.query.toString(),
                    {
                        listView?.adapter = PersonAdapter(context!!,
                            R.layout.row_person, it)
                        ObjectCache.keyboardOpen = keyboardOpen
                        personProgressBar?.visibility = View.INVISIBLE

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
            personSearchView?.onActionViewExpanded()
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
