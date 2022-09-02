package at.tug.search.controllers.news

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import at.tug.search.activity.MainActivity
import at.tug.search.R
import at.tug.search.models.News
import at.tug.search.utils.APIManager
import at.tug.search.utils.ObjectCache
import at.tug.search.utils.SearchCategory

class NewsFragment : Fragment(){
    var root: View? = null
    var queryErrorText : TextView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        ObjectCache.lastSearchedCategory = SearchCategory.NONE
        Handler().postDelayed({
            ObjectCache.inAnimation = false}, 350
        )

        (activity as MainActivity).closeKeyboard()

        if (root != null)
        {
            if (root!!.parent != null)
            {
                (root!!.parent as ViewGroup).removeView(root)
            }

            if(!ObjectCache.internetConnected)
            {
                queryErrorText?.text = getString(R.string.noInternet)
                queryErrorText?.visibility = View.VISIBLE
                (activity as MainActivity).closeKeyboard()
            }
            else
            {
                queryErrorText?.visibility = View.INVISIBLE
            }

            return root
        }

        root = inflater.inflate(R.layout.fragment_news, container, false)
        queryErrorText = root!!.findViewById(R.id.newsQueryErrorText)
        val listView : ListView = root!!.findViewById(R.id.listViewNews)


        if(ObjectCache.newsList == null)
        {
            if(ObjectCache.internetConnected == false)
            {
                queryErrorText?.text = getString(R.string.noInternet)
            }
            else
            {
                APIManager.instance.startDownloadNews(root!!.context,
                    {
                        listView.adapter = NewsAdapter(root!!.context, R.layout.row_news, it)
                        ObjectCache.newsList = it
                    })
            }
        }
        else
        {
            listView.adapter =
                ObjectCache.newsList?.let { NewsAdapter(root!!.context, R.layout.row_news, it) }
        }

        listView.onItemClickListener = object : AdapterView.OnItemClickListener {

            override fun onItemClick(parent: AdapterView<*>, view: View,
                                     position: Int, id: Long) {
                val itemValue = listView.getItemAtPosition(position) as News

                val args = Bundle()
                args.putString("url", itemValue.link)
                args.putString("title", "News")
                ObjectCache.inAnimation = true

                view.findNavController().navigate(R.id.action_nav_news_to_detail_web_view, args)
            }
        }

        Handler().postDelayed({
            ObjectCache.inAnimation = false}, 350
        )

        return root
    }


    override fun onStart() {
        super.onStart()

        if (!ObjectCache.internetConnected) {
            queryErrorText?.text = getString(R.string.noInternet)
            queryErrorText?.visibility = View.VISIBLE
            (activity as MainActivity).closeKeyboard()
        } else {
            queryErrorText?.visibility = View.INVISIBLE
        }
    }

}