package at.tug.search.controllers.room;

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
import at.tug.search.models.Room
import at.tug.search.utils.APIManager
import at.tug.search.utils.ObjectCache

class RoomFragment : Fragment(){

    var root: View? = null
    var lastQuery : String? = null
    var queryErrorText : TextView? = null
    var roomSearchView : SearchView? = null
    var listView : ListView? = null
    var roomProgressBar : ProgressBar? = null
    var handler : Handler = Handler()

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
                val inputMethodManager : InputMethodManager = (activity as MainActivity).getSystemService(
                    Context.INPUT_METHOD_SERVICE) as InputMethodManager
                (activity as MainActivity).openKeyboard()
                val current = (activity as MainActivity).currentFocus
                current?.let {
                    inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
                }
                roomSearchView?.requestFocus()
            }
            return root
        }


        root = inflater.inflate(R.layout.fragment_room, container, false)
        listView = root!!.findViewById(R.id.listViewRoom)
        roomProgressBar = root!!.findViewById(R.id.roomProgressBar)
        queryErrorText = root!!.findViewById(R.id.roomQueryErrorText)
        roomSearchView = root!!.findViewById(R.id.roomSearchBar) as SearchView

        return root
    }

    override fun onStart() {
        super.onStart()
        ObjectCache.actualFragment = "Room"

        checkInternetConnection()

        listView?.onItemClickListener =
            AdapterView.OnItemClickListener {parent, view, position, id ->

                lastQuery =  (activity as MainActivity).getQueryText()
                val args = Bundle()
                args.putParcelable("room", listView?.getItemAtPosition(position) as Room)
                ObjectCache.inAnimation = true
                view.findNavController().navigate(R.id.action_nav_room_to_room_detail, args)
            }

        roomSearchView?.setOnQueryTextListener( object : SearchView.OnQueryTextListener {

            override fun onQueryTextChange(query: String): Boolean {
                downloadRoom(query, true)
                return false
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                downloadRoom(query, false)
                return false
            }
        })

        ObjectCache.FragmentErrorIndicator = {
            checkInternetConnection()
        }
    }

    fun downloadRoom(query: String, keyboardOpen : Boolean) {

        if(ObjectCache.internetConnected) {
            if(query.count() == 0)
            {
                listView?.adapter = null

                if(ObjectCache.internetConnected)
                {
                    queryErrorText?.visibility = View.INVISIBLE
                }
            }
            else if (query.count() > 1) {
                handler.removeCallbacksAndMessages(null)
                if (roomProgressBar?.visibility == View.INVISIBLE) {
                    roomProgressBar?.visibility = View.VISIBLE
                }
                APIManager.instance.startDownloadRoom(context!!,
                    roomSearchView?.query.toString(),
                    {
                        listView?.adapter = RoomAdapter(context!!,
                            R.layout.row_room, it)
                        ObjectCache.keyboardOpen = keyboardOpen
                        roomProgressBar?.visibility = View.INVISIBLE

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
                    queryErrorText?.text = getString(R.string.min2)
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
            roomSearchView?.onActionViewExpanded()
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
