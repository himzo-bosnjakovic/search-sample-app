package at.tug.search.controllers.room

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import at.tug.search.R
import at.tug.search.models.Room

class RoomAdapter (var con: Context, var resources: Int, var items : List<Room>): ArrayAdapter<Room>(con, resources, items) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val layoutInflater : LayoutInflater = LayoutInflater.from(con)
        val view : View = layoutInflater.inflate(resources, null)

        val roomName : TextView = view.findViewById(R.id.roomName)
        val adress : TextView = view.findViewById(R.id.adress)

        var item : Room = items[position]
        roomName.text = item.additionalInformation + " (" + item.roomCode + ")"
        adress.text = item.address

        return view
    }
}