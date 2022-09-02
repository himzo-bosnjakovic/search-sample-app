package at.tug.search.controllers.organisation

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import at.tug.search.R
import at.tug.search.models.Organisation
import at.tug.search.utils.ObjectCache

class OrganisationAdapter (var con: Context, var resources: Int, var items : List<Organisation>): ArrayAdapter<Organisation>(con, resources, items) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val layoutInflater : LayoutInflater = LayoutInflater.from(con)
        val view : View = layoutInflater.inflate(resources, null)

        val organisationTitel : TextView = view.findViewById(R.id.organisationTitel)
        val organisationAdress : TextView = view.findViewById(R.id.organisationAdress)

        var item : Organisation = items[position]
        ObjectCache.searchedOrganisations.add(item)

        organisationTitel.text = item.name

        if(!item.street.isNullOrEmpty())
        {
            organisationAdress.text = item.street + ", " + item.pcode + " " + item.locality
        }
        else
        {
            organisationAdress.text = ""
        }

        return view
    }
}