package at.tug.search.controllers.person

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import at.tug.search.R
import at.tug.search.models.Person
import at.tug.search.utils.APIManager

class PersonAdapter (var con: Context, var resources: Int, var items : List<Person>): ArrayAdapter<Person>(con, resources, items) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val layoutInflater : LayoutInflater = LayoutInflater.from(con)
        val view : View = layoutInflater.inflate(resources, null)

        val personTitle : TextView = view.findViewById(R.id.personTitle)
        val personName : TextView = view.findViewById(R.id.personName)
        val personInstitute : TextView = view.findViewById(R.id.personInstitute)
        val personImage : ImageView = view.findViewById(R.id.personImage)

        val item : Person = items[position]

        personTitle.text = item.title
        personInstitute.text = item.contactName
        personName.text = item.personName
        item.infoBlock_pic?.let { APIManager.instance.getImageFromUrl(it, personImage) }

        return view
    }
}