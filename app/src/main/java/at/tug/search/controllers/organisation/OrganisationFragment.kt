package at.tug.search.controllers.organisation;

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
import at.tug.search.models.Organisation
import at.tug.search.utils.APIManager
import at.tug.search.utils.ObjectCache

class OrganisationFragment : Fragment(){

    var root: View? = null
    var lastQuery : String? = null
    var queryErrorText : TextView? = null
    var organisationSearchView : SearchView? = null
    var handler : Handler = Handler()
    var listView : ListView? = null
    var organisationProgressBar : ProgressBar? = null

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
                organisationSearchView?.requestFocus()
            }

            return root
        }

        root = inflater.inflate(R.layout.fragment_organisation, container, false)
        listView = root!!.findViewById(R.id.listViewOrganisation)
        organisationProgressBar = root!!.findViewById(R.id.organisationProgressBar)
        queryErrorText = root!!.findViewById(R.id.organisationQueryErrorText)
        organisationSearchView = root!!.findViewById(R.id.organisationSearchBar) as SearchView

        return root
    }

    override fun onStart() {
        super.onStart()

        checkInternetConnection()

        listView?.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->

                lastQuery =  (activity as MainActivity).getQueryText()
                val args = Bundle()
                args.putParcelable("organisation", listView?.getItemAtPosition(position) as Organisation)
                ObjectCache.inAnimation = true

                view.findNavController().navigate(R.id.action_nav_organisation_to_organisation_detail, args)
            }

        organisationSearchView?.setOnQueryTextListener( object : SearchView.OnQueryTextListener {

            override fun onQueryTextChange(query: String): Boolean {
                downloadOrganisation(query, true)
                return false
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                downloadOrganisation(query, false)
                return false
            }
        })
    }

    fun downloadOrganisation(query: String, keyboardOpen : Boolean) {

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
                handler.removeCallbacksAndMessages(null)
                if(organisationProgressBar?.visibility == View.INVISIBLE) {
                    organisationProgressBar?.visibility = View.VISIBLE
                }

                APIManager.instance.startDownloadOrganisation(context!!, organisationSearchView?.query.toString(),
                    {
                        listView?.adapter = OrganisationAdapter(context!!,
                            R.layout.row_organisation, it)
                        ObjectCache.keyboardOpen = keyboardOpen
                        organisationProgressBar?.visibility = View.INVISIBLE

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
            organisationSearchView?.onActionViewExpanded()
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