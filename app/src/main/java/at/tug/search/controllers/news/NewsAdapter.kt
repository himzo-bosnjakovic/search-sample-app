package at.tug.search.controllers.news

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import at.tug.search.R
import at.tug.search.models.News
import java.text.SimpleDateFormat
import java.util.*




class NewsAdapter (var con: Context, var resources: Int, var items : List<News>): ArrayAdapter<News>(con, resources, items) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val layoutInflater : LayoutInflater = LayoutInflater.from(con)
        val view : View = layoutInflater.inflate(resources, null)

        val newsDate : TextView = view.findViewById(R.id.newsDate)
        val newsName : TextView = view.findViewById(R.id.newsName)

        var item : News = items[position]

        val parser = SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale("en", "US", "POSIX"))
        val formatter = SimpleDateFormat("dd.MM.yyyy, HH:mm")

        newsDate.text = formatter.format(parser?.parse(item.pubDate))
        newsName.text = item.title

        return view
    }
}