package at.tug.search.utils

import android.app.DownloadManager
import android.content.Context
import android.util.Log
import at.tug.search.models.*
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import android.graphics.BitmapFactory
import android.graphics.Bitmap
import android.view.View
import android.widget.ImageView
import at.tug.search.R
import com.squareup.picasso.Picasso
import java.io.InputStream
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLConnection
import java.net.URLEncoder
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import javax.net.ssl.HttpsURLConnection


class APIManager {

    companion object{
        val instance = APIManager()
    }

    private var TAG = "APIManager"
    private var headerKeyAuth = "Authorization"
    private var headerValueAuth = "Basic YXBwYW5kcm9pZHR1Z3JhenNlYXJjaDplZT1UYWlELG9oQG0wb294YWZhdWhlZWI4"
    private var baseURL = "https://search-api.tugraz.at/api/v0"
    private var searchAPI = "/search-proxy/search/onebox/Search.php?query="
    private var eventURL = "https://rss.tugraz.at/events.xml"
    private var newsURL = "https://rss.tugraz.at/news.xml"


    fun startDownloadCourse(context: Context, searchString: String, lamda : (ArrayList<Course>)->Unit){

        val queue = Volley.newRequestQueue(context)

        val stringRequest = object : StringRequest(
            Method.GET, baseURL + searchAPI + searchString + "&ws=cbo",
            Response.Listener<String> { response ->
                XMLParser.instance.courseModelsFromData(response, lamda)
            },
            Response.ErrorListener {
            })
        {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers[headerKeyAuth] = headerValueAuth
                return headers
            }
        }
        queue.add(stringRequest)
    }

    fun startDownloadEvent(context: Context, lamda : (ArrayList<Event>)->Unit) {

        val queue = Volley.newRequestQueue(context)

        val stringRequest = StringRequest(
            Request.Method.GET, eventURL,
            { response ->
                XMLParser.instance.eventModelsFromData(String(response.toByteArray(charset("ISO-8859-1")), charset("UTF-8")), lamda)
            },
            {
            })

        queue.add(stringRequest)
    }


    fun startDownloadNews(context: Context, lamda : (ArrayList<News>)->Unit) {

        val queue = Volley.newRequestQueue(context)

        val stringRequest = StringRequest(
            Request.Method.GET, newsURL,
            { response ->
                XMLParser.instance.newsModelsFromData(String(response.toByteArray(charset("ISO-8859-1")), charset("UTF-8")), lamda)
            },
            {
            })

        queue.add(stringRequest)
    }


    fun startDownloadOrganisation(context: Context, searchString: String, lamda : (ArrayList<Organisation>)->Unit) {

        val queue = Volley.newRequestQueue(context)
        val url = baseURL + searchAPI + searchString + "&ws=org"
        val stringRequest = object : StringRequest(
            Method.GET, url,
            Response.Listener<String> { response ->
                XMLParser.instance.organisationModelsFromData(response, lamda)
            },
            Response.ErrorListener {
            })
        {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers[headerKeyAuth] = headerValueAuth
                return headers
            }
        }
        queue.add(stringRequest)
    }

    fun startDownloadPerson(context: Context, searchString: String, lamda : (ArrayList<Person>)->Unit) {

        val queue = Volley.newRequestQueue(context)
        val url = baseURL + searchAPI + searchString + "&ws=prs"
        val stringRequest = object : StringRequest(
            Method.GET, url,
            Response.Listener<String> { response ->
                XMLParser.instance.personModelsFromData(response, lamda)
            },
            Response.ErrorListener {
            })
        {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers[headerKeyAuth] = headerValueAuth
                return headers
            }
        }
        queue.add(stringRequest)
    }

    fun startDownloadRoom(context: Context, searchString: String, lamda : (ArrayList<Room>)->Unit) {

        val queue = Volley.newRequestQueue(context)
        val url = baseURL + searchAPI + searchString + "&ws=rl"
        val stringRequest = object : StringRequest(
            Method.GET, url,
            Response.Listener<String> { response ->
                XMLParser.instance.roomModelsFromData(response, lamda)
            },
            Response.ErrorListener {
            })
        {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers[headerKeyAuth] = headerValueAuth
                return headers
            }
        }
        queue.add(stringRequest)
    }

    fun getImageFromUrl(urlImage : String, imgView : ImageView) {

        if(urlImage.isNotEmpty())
        {
            Picasso.get().load(urlImage).into(imgView)
        }
    }
}