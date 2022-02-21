package at.tug.search.controllers.events

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import at.tug.search.R
import at.tug.search.models.Event

class EventsAdapter (var con: Context, var resources: Int, var items : List<Event>): ArrayAdapter<Event>(con, resources, items) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val layoutInflater : LayoutInflater = LayoutInflater.from(con)
        val view : View = layoutInflater.inflate(resources, null)

        val eventName : TextView = view.findViewById(R.id.eventName)
        val eventType : TextView = view.findViewById(R.id.eventType)

        var item : Event = items[position]
        eventName.text = item.title
        eventType.text = item.category

        return view
    }
}