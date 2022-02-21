package at.tug.search.controllers.roomDetail

import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import at.tug.search.activity.MainActivity

import at.tug.search.R
import at.tug.search.models.Room
import at.tug.search.utils.ObjectCache
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.lang.Exception

class RoomDetailFragment : Fragment(), OnMapReadyCallback {

    private var root : View? = null

    private lateinit var mMap: GoogleMap
    private lateinit var room : Room

    private var roomDetailOpenMaps : TextView? = null
    private var roomDetailTugonline : TextView? = null
    private var roomDetailName : TextView? = null
    private var roomDetailUse : TextView? = null
    private var roomDetailAdress : TextView? = null
    private var roomDetailMapOverlay : TextView? = null
    private var mapFragment: SupportMapFragment? = null



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        if (root != null)
        {
            if (root!!.parent != null)
            {
                (root!!.parent as ViewGroup).removeView(root)
            }
            mapFragment = childFragmentManager.findFragmentById(R.id.roomDetailMapView) as SupportMapFragment
        }
        else
        {
            root = inflater.inflate(R.layout.fragment_room_detail, container, false)
            roomDetailOpenMaps = root?.findViewById(R.id.roomDetailOpenMap)
            roomDetailTugonline = root?.findViewById(R.id.roomDetailTugonline)
            roomDetailName = root?.findViewById(R.id.roomDetailName)
            roomDetailUse = root?.findViewById(R.id.roomDetailUse)
            roomDetailAdress = root?.findViewById(R.id.roomDetailPlace)
            roomDetailMapOverlay = root?.findViewById(R.id.roomDetailMapOverlay)
            mapFragment = childFragmentManager.findFragmentById(R.id.roomDetailMapView) as SupportMapFragment
        }

        return root
    }

    override fun onStart() {
        super.onStart()

        room = arguments?.getParcelable("room")!!

        (activity as MainActivity).closeKeyboard()
        (activity as MainActivity).setActionBarTitle(room.additionalInformation)

        roomDetailName?.text = room.additionalInformation + " (" + room.roomCode + ")"
        roomDetailUse?.text = room.purpose
        roomDetailAdress?.text = room.address

        roomDetailTugonline?.setOnClickListener({
            val intent = Intent(Intent.ACTION_VIEW)
            val data = Uri.parse(room.address_attrAltUrl)
            intent.data = data
            startActivity(intent)
        })

        if(!room.address.isNullOrEmpty()) {

            roomDetailOpenMaps?.setOnClickListener({
                val gmmIntentUri = Uri.parse(
                    "geo:0,0?q=" + room.address + "+" + room.additionalInformation
                )
                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                mapIntent.setPackage("com.google.android.apps.maps")
                startActivity(mapIntent)
            })

            Handler().postDelayed({
                mapFragment?.getMapAsync(this)

                ObjectCache.inAnimation = false
            }, 350)
        }
        else
        {
            roomDetailMapOverlay?.visibility = View.VISIBLE
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        try {

            val geoCoderResult: List<Address> =
                Geocoder(context).getFromLocationName(room.address + ", 8010 Graz", 1)

            val destination: LatLng

            if (geoCoderResult[0].hasLatitude() && geoCoderResult[0].hasLongitude()) {
                destination = LatLng(geoCoderResult[0].latitude, geoCoderResult[0].longitude)
                mMap.addMarker(MarkerOptions().position(destination).title(room.additionalInformation))
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(destination, 17F))
            }
        }
        catch (exception : Exception)
        {
            Log.e("RoomDetail", exception.toString())
            roomDetailMapOverlay?.visibility = View.VISIBLE
            roomDetailOpenMaps?.setOnClickListener { return@setOnClickListener }
        }

    }
}
