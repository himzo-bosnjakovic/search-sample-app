package at.tug.search.controllers.organisationDetail

import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import at.tug.search.activity.MainActivity
import at.tug.search.R
import at.tug.search.models.Organisation
import at.tug.search.utils.ObjectCache
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.lang.Exception

class OrganisationDetailFragment : Fragment(), OnMapReadyCallback {


    private var organisation: Organisation? = null
    private var root : View? = null
    private lateinit var organisationDetailName: TextView
    private lateinit var organisationDetailAdress: TextView
    private lateinit var organisationDetailEmail: TextView
    private lateinit var organisationDetailWebsite: TextView
    private lateinit var organisationDetailTugonline: TextView
    private lateinit var organisationDetailOpenMaps: TextView
    private lateinit var organisationDetailMapOverlay: TextView
    private lateinit var organisationDetailMapView: SupportMapFragment

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        if (root != null)
        {
            if (root!!.parent != null)
            {
                (root!!.parent as ViewGroup).removeView(root)
            }
            organisationDetailMapView = childFragmentManager.findFragmentById(R.id.organisationDetailMapView) as SupportMapFragment
        }
        else
        {
            root = inflater.inflate(R.layout.fragment_organisation_detail, container, false)
            organisationDetailName = root!!.findViewById(R.id.organisationDetailName)
            organisationDetailAdress = root!!.findViewById(R.id.organisationDetailAdress)
            organisationDetailEmail = root!!.findViewById(R.id.organisationDetailEmail)
            organisationDetailWebsite = root!!.findViewById(R.id.organisationDetailWebsite)
            organisationDetailTugonline = root!!.findViewById(R.id.organisationDetailTugonline)
            organisationDetailOpenMaps = root!!.findViewById(R.id.organisationDetailOpenMaps)
            organisationDetailMapOverlay = root!!.findViewById(R.id.organisationDetailMapOverlay)
            organisationDetailMapView = childFragmentManager.findFragmentById(R.id.organisationDetailMapView) as SupportMapFragment
        }

        return root
    }

    override fun onStart() {
        super.onStart()

        (activity as MainActivity).closeKeyboard()

        organisation = arguments?.getParcelable("organisation")

        organisationDetailName.text = organisation?.name


        if(!organisation?.email.isNullOrEmpty())
        {
            organisationDetailEmail.text = organisation?.email

            organisationDetailEmail.setOnClickListener({
                val intent = Intent(Intent.ACTION_VIEW)
                val data = Uri.parse("mailto:" + organisation?.email)
                intent.data = data
                startActivity(intent)
            })
        }
        else
        {
            organisationDetailEmail.text = getString(R.string.notFound)
            context?.let {
                ContextCompat.getColor(
                    it, R.color.textGrey)
            }?.let { (root!!.findViewById(R.id.organisationDetailEmailLable) as TextView).setTextColor(it)
                organisationDetailEmail.setTextColor(it)}
        }

        if(!organisation?.href.isNullOrEmpty()) {
            organisationDetailWebsite.text = organisation?.href
            organisationDetailWebsite.setOnClickListener({
                val intent = Intent(Intent.ACTION_VIEW)
                val data = Uri.parse(organisation?.href)
                intent.data = data
                startActivity(intent)
            })
        }
        else
        {
            organisationDetailWebsite.text = getString(R.string.notFound)
            context?.let {
                ContextCompat.getColor(
                    it, R.color.textGrey)
            }?.let { (root!!.findViewById(R.id.organisationDetailWebsiteLable) as TextView).setTextColor(it)
                organisationDetailWebsite.setTextColor(it)}
        }

        if(!organisation?.CAMPUSonlineURL.isNullOrEmpty()) {
            organisationDetailTugonline.setOnClickListener({
                val intent = Intent(Intent.ACTION_VIEW)
                val data = Uri.parse(organisation?.CAMPUSonlineURL)
                intent.data = data
                startActivity(intent)
            })
        }

        if(!organisation?.street.isNullOrEmpty() &&
            !organisation?.pcode.isNullOrEmpty() &&
            !organisation?.locality.isNullOrEmpty()) {

            organisationDetailAdress.text = organisation?.pcode + " " + organisation?.locality + " " +
                    organisation?.street

            organisationDetailOpenMaps.setOnClickListener({
                val gmmIntentUri = Uri.parse(
                    "geo:0,0?q=" + organisation?.street
                            + "+" + organisation?.pcode + "+" + organisation?.locality
                )
                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                mapIntent.setPackage("com.google.android.apps.maps")
                startActivity(mapIntent)
            })

            Handler().postDelayed({
                organisationDetailMapView.getMapAsync(this)
                ObjectCache.inAnimation = false}, 350
            )
        }
        else
        {
            organisationDetailMapOverlay.visibility = View.VISIBLE
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        var mMap : GoogleMap = googleMap

        try {
            val geoCoderResult: List<Address> = Geocoder(context).getFromLocationName(
                organisation?.street
                        + "," + organisation?.pcode + " " + organisation?.locality,
                1
            )
            if(geoCoderResult[0].hasLatitude() && geoCoderResult[0].hasLongitude())
            {
                val destination = LatLng(geoCoderResult[0].latitude, geoCoderResult[0].longitude)
                mMap.addMarker(MarkerOptions().position(destination).title(organisation?.name))
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(destination, 17F))
            }
        }
        catch (exception: Exception)
        {
            Log.e("OrganisationDetail", exception.toString())
            organisationDetailMapOverlay.visibility = View.VISIBLE
            organisationDetailOpenMaps.setOnClickListener { return@setOnClickListener }
        }
    }
}