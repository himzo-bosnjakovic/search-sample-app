package at.tug.search.controllers.personDetail

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import at.tug.search.R
import android.content.Intent
import android.location.Address
import android.net.Uri
import at.tug.search.activity.MainActivity
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.MarkerOptions
import android.location.Geocoder
import android.os.Handler
import android.util.Log
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import at.tug.search.models.Person
import at.tug.search.utils.ObjectCache
import java.lang.Exception

class PersonDetailFragment : Fragment(), OnMapReadyCallback {

    private var root : View? = null

    private lateinit var mMap : GoogleMap
    private lateinit var person : Person

    private lateinit var personImage: ImageView
    private lateinit var personDetailTitle: TextView
    private lateinit var personDetailName: TextView
    private lateinit var personDetailMapOverlay: TextView
    private lateinit var personDetailInstitution: TextView
    private lateinit var personDetailAdress: TextView
    private lateinit var personDetailTelefone: TextView
    private lateinit var personDetailEmail: TextView
    private lateinit var personDetailWebsite: TextView
    private lateinit var personDetailTugonline: TextView
    private lateinit var personDetailOpenMaps: TextView
    private lateinit var personLinearLayoutPhone: LinearLayout
    private lateinit var personLinearLayoutEmail: LinearLayout
    private lateinit var personLinearLayoutWebsite: LinearLayout
    private lateinit var mapFragment: SupportMapFragment


    @SuppressLint("ResourceAsColor")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        if (root != null)
        {
            if (root?.parent != null)
            {
                (root?.parent as ViewGroup).removeView(root)
            }
            mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        }
        else
        {
            root = inflater.inflate(R.layout.fragment_person_detail, container, false)
            personImage = root!!.findViewById(R.id.personDetailImage)
            personDetailTitle = root!!.findViewById(R.id.personDetailTitle)
            personDetailName = root!!.findViewById(R.id.personDetailName)
            personDetailInstitution = root!!.findViewById(R.id.personDetailInstitution)
            personDetailAdress = root!!.findViewById(R.id.personDetailAdress)
            personDetailTelefone = root!!.findViewById(R.id.personDetailTelefone)
            personDetailEmail = root!!.findViewById(R.id.personDetailEmail)
            personDetailWebsite = root!!.findViewById(R.id.personDetailWebsite)
            personDetailTugonline = root!!.findViewById(R.id.personDetailTugonline)
            personDetailOpenMaps = root!!.findViewById(R.id.personDetailOpenMaps)
            personLinearLayoutPhone = root!!.findViewById(R.id.personLinearLayoutPhone)
            personLinearLayoutEmail = root!!.findViewById(R.id.personLinearLayoutEmail)
            personLinearLayoutWebsite = root!!.findViewById(R.id.personLinearLayoutWebsite)
            personDetailMapOverlay = root!!.findViewById(R.id.personDetailMapOverlay)
            mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        }

        return root
    }

    override fun onStart() {
        super.onStart()
        person = arguments?.getParcelable("person")!!

        (activity as MainActivity).closeKeyboard()
        (activity as MainActivity).setActionBarTitle(person.personName)

        if(person.image != null)
        {
            personImage.setImageBitmap(person.image)
        }

        if(person.title.isNullOrEmpty()){
            personDetailTitle.visibility = View.GONE
        } else {
            personDetailTitle.text = person.title
        }
        personDetailName.text = person.personName
        personDetailInstitution.text = person.contactName
        personDetailAdress.text = person.p_street_PA


        if(!person.email_person.isNullOrEmpty()) {
            personDetailEmail.text = person.email_person

            personLinearLayoutEmail.setOnClickListener({
                val intent = Intent(Intent.ACTION_VIEW)
                val data = Uri.parse("mailto:" + person.email_person)
                intent.data = data
                startActivity(intent)
            })
        }
        else
        {
            personDetailEmail.text = getString(R.string.notFound)
            context?.let {
                ContextCompat.getColor(
                    it, R.color.textGrey)
            }?.let { (root!!.findViewById(R.id.personDetailEmailText) as TextView).setTextColor(it)
                personDetailEmail.setTextColor(it)}
        }


        if(!person.tel_office.isNullOrEmpty()) {

            personDetailTelefone.text = person.tel_office

            personLinearLayoutPhone.setOnClickListener({
                val intent = Intent(Intent.ACTION_DIAL)
                val data = Uri.parse("tel:" + person.tel_office)
                intent.data = data
                startActivity(intent)
            })
        }
        else
        {
            personDetailTelefone.text = getString(R.string.notFound)
            context?.let {
                ContextCompat.getColor(
                    it, R.color.textGrey)
            }?.let { (root!!.findViewById(R.id.personDetailTelephoneText) as TextView).setTextColor(it)
                personDetailTelefone.setTextColor(it)}
        }


        if(!person.webLink_person.isNullOrEmpty()) {

            personDetailWebsite.text = person.webLink_person

            personLinearLayoutWebsite.setOnClickListener({

                var url: String? = person.webLink_person

                if (url?.startsWith("www.")!!) {
                    url = "http://" + url
                }

                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                startActivity(intent)
            })
        }
        else
        {
            context?.let {
                ContextCompat.getColor(
                    it, R.color.textGrey)
            }?.let { (root!!.findViewById(R.id.personDetailWebsiteText) as TextView).setTextColor(it)
                personDetailWebsite.setTextColor(it)}

            personDetailWebsite.text = getString(R.string.notFound)
        }

        personDetailTugonline.setOnClickListener({
            val intent = Intent(Intent.ACTION_VIEW)
            val data = Uri.parse(person.p_detail_infoBlock_webLink)
            intent.data = data
            startActivity(intent)
        })


        if(!person.p_street_PA.isNullOrEmpty() &&
            !person.p_pcode_PA.isNullOrEmpty() &&
            !person.p_locality_PA.isNullOrEmpty()) {


            personDetailOpenMaps.setOnClickListener({


                val gmmIntentUri = Uri.parse(
                    "geo:0,0?q=" + person.p_street_PA
                            + "+" + person.p_pcode_PA + "+" + person.p_locality_PA
                )
                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                mapIntent.setPackage("com.google.android.apps.maps")
                startActivity(mapIntent)
            })

            Handler().postDelayed({
                mapFragment.getMapAsync(this)
                ObjectCache.inAnimation = false
            }, 350)
        }
        else
        {
            personDetailMapOverlay.visibility = View.VISIBLE
        }


    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        try {

            val geoCoderResult: List<Address> = Geocoder(context).getFromLocationName(
                person.p_street_PA
                        + "," + person.p_pcode_PA + " " + person.p_locality_PA,
                1
            )
            if (geoCoderResult[0].hasLatitude() && geoCoderResult[0].hasLongitude()) {
                val destination = LatLng(geoCoderResult[0].latitude, geoCoderResult[0].longitude)
                mMap.addMarker(MarkerOptions().position(destination).title(person.personName))
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(destination, 17F))
                mMap.uiSettings.setAllGesturesEnabled(false)
            }
        }
        catch (exception : Exception)
        {
            Log.e("PersonDetail", "Map error:" + exception.toString())
            personDetailMapOverlay.visibility = View.VISIBLE
            personDetailOpenMaps.setOnClickListener { return@setOnClickListener }
        }
    }
}
