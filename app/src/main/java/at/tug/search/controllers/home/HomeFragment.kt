package at.tug.search.controllers.home

import android.os.Bundle
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import at.tug.search.activity.MainActivity
import at.tug.search.R
import android.content.res.Resources
import android.view.*
import at.tug.search.utils.ObjectCache

class HomeFragment : Fragment() {

    var root: View? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        (activity as MainActivity).closeKeyboard()
        (activity as MainActivity).resetCache()
        ObjectCache.actualFragment = "Home"
        ObjectCache.FragmentErrorIndicator = {}

        if (root != null)
        {
            if (root!!.parent != null)
            {
                (root!!.parent as ViewGroup).removeView(root)
            }

            return root
        }

        root = inflater.inflate(R.layout.fragment_home, container, false)
        val linearLayoutPerson: LinearLayout = root!!.findViewById(R.id.linearLayoutPerson) as LinearLayout
        val linearLayoutRoom: LinearLayout = root!!.findViewById(R.id.linearLayoutRoom)
        val linearLayoutCourse: LinearLayout = root!!.findViewById(R.id.linearLayoutCourse)
        val linearLayoutOrganisation: LinearLayout = root!!.findViewById(R.id.linearLayoutOrganisation)
        val linearLayoutNews: LinearLayout = root!!.findViewById(R.id.linearLayoutNews)
        val linearLayoutEvents: LinearLayout = root!!.findViewById(R.id.linearLayoutEvents)
        val linearLayoutFirstRow: LinearLayout = root!!.findViewById(R.id.linearLayoutFirstRow)
        val linearLayoutSecondRow: LinearLayout = root!!.findViewById(R.id.linearLayoutSecondRow)

        linearLayoutFirstRow.layoutParams.height = Resources.getSystem().displayMetrics.widthPixels / 3
        linearLayoutSecondRow.layoutParams.height = Resources.getSystem().displayMetrics.widthPixels / 3
        linearLayoutFirstRow.requestLayout()
        linearLayoutSecondRow.requestLayout()

        linearLayoutPerson.setOnClickListener( {
            it.findNavController().navigate(R.id.action_nav_home_to_nav_person)
            ObjectCache.inAnimation = true
        })

        linearLayoutRoom.setOnClickListener( {
            it.findNavController().navigate(R.id.action_nav_home_to_nav_room)
            ObjectCache.inAnimation = true
        })

        linearLayoutCourse.setOnClickListener( {
            it.findNavController().navigate(R.id.action_nav_home_to_nav_lv)
            ObjectCache.inAnimation = true
        })

        linearLayoutOrganisation.setOnClickListener( {
            it.findNavController().navigate(R.id.action_nav_home_to_nav_organisation)
            ObjectCache.inAnimation = true
        })

        linearLayoutNews.setOnClickListener( {
            it.findNavController().navigate(R.id.action_nav_home_to_nav_news)
            ObjectCache.inAnimation = true
        })

        linearLayoutEvents.setOnClickListener( {
            it.findNavController().navigate(R.id.action_nav_home_to_nav_events)
            ObjectCache.inAnimation = true
        })

        return root
    }
}
