package at.tug.search.activity

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.net.NetworkRequest
import android.os.Bundle
import android.os.Debug
import android.os.Handler
import android.util.Log
import android.view.KeyEvent
import android.view.Menu
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import at.tug.search.R
import at.tug.search.controllers.course.CourseAdapter
import at.tug.search.controllers.organisation.OrganisationAdapter
import at.tug.search.controllers.person.PersonAdapter
import at.tug.search.controllers.room.RoomAdapter
import at.tug.search.utils.APIManager
import at.tug.search.utils.ObjectCache
import at.tug.search.widget.WidgetProvider
import com.google.android.material.navigation.NavigationView


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private var searchView : SearchView? = null
    private var handler : Handler = Handler()
    private var inputMethodManager : InputMethodManager? = null

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        closeKeyboard()
        return super.onKeyDown(keyCode, event)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)

        appBarConfiguration = AppBarConfiguration(setOf(
            R.id.nav_home,
            R.id.nav_events,
            R.id.nav_lv,
            R.id.nav_news,
            R.id.nav_organisation,
            R.id.nav_person,
            R.id.nav_room
        ), drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        inputMethodManager = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        findViewById<TextView>(R.id.aboutTextView).setOnClickListener {
                startActivity(Intent(this, AboutActivity::class.java))
        }
    }

    override fun onStart() {
        super.onStart()

        try {
            val connectivityManager =
                this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val builder = NetworkRequest.Builder()
            connectivityManager.registerNetworkCallback(
                builder.build(),
                object : NetworkCallback() {
                    override fun onAvailable(network: Network?) {
                        ObjectCache.internetConnected = true

                        if(ObjectCache.eventList == null)
                        {
                            APIManager.instance.startDownloadEvent(applicationContext,
                                {
                                    ObjectCache.eventList = it
                                })
                        }

                        if(ObjectCache.newsList == null)
                        {
                            APIManager.instance.startDownloadNews(applicationContext,
                                {
                                    ObjectCache.newsList = it
                                })
                        }

                        this@MainActivity.runOnUiThread({
                            ObjectCache.FragmentErrorIndicator()
                        })
                    }

                    override fun onLost(network: Network?) {
                        ObjectCache.internetConnected  = false

                        this@MainActivity.runOnUiThread({
                            ObjectCache.FragmentErrorIndicator()
                        })
                    }
                }
            )
        } catch (e: Exception) {
            ObjectCache.internetConnected  = false
        }
    }

    override fun onResume() {
        super.onResume()
        performWidgetNavigation()
    }

    override fun onPause() {
        super.onPause()
        val intent = Intent(this, WidgetProvider::class.java)
        intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        val ids = AppWidgetManager.getInstance(application).getAppWidgetIds(ComponentName(application, WidgetProvider::class.java))
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
        sendBroadcast(intent)
        finish()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val x = 10
    }

    private fun performWidgetNavigation() {
        when (intent.action ?: "") {
            "SearchPerson" -> {
                findNavController(R.id.nav_host_fragment).navigate(R.id.action_nav_home_to_nav_person)
                return
            }
            "SearchRoom" -> {
                findNavController(R.id.nav_host_fragment).navigate(R.id.action_nav_home_to_nav_room)
                return
            }
            "SearchCourse" -> {
                findNavController(R.id.nav_host_fragment).navigate(R.id.action_nav_home_to_nav_lv)
                return
            }
            "SearchOrganization" -> {
                findNavController(R.id.nav_host_fragment).navigate(R.id.action_nav_home_to_nav_organisation)
                return
            }
            "EventWebView" -> {
                val url = intent.data.toString()
                navigateToWebView(url, "Events")
                return
            }
            else -> {}
        }

        val clickedNewsUrl = ObjectCache.clickedNewsUrl
        if (clickedNewsUrl.isNotEmpty()) {
           navigateToWebView(clickedNewsUrl, "News")
        }
    }

    private fun navigateToWebView(url: String, title: String) {
        if (url.isEmpty()) return
        ObjectCache.clickedNewsUrl = ""
        val args = Bundle()
        args.putString("url", url)
        args.putString("title", title)
        ObjectCache.inAnimation = true
        findNavController(R.id.nav_host_fragment).navigate(R.id.open_events_web_view, args)
    }

    fun setOnQueryTextListenerSearchViewInToolBarPerson(listView: ListView, progressBar: ProgressBar, queryErrorText: TextView) {
        searchView?.setOnQueryTextListener( object : SearchView.OnQueryTextListener {

            override fun onQueryTextChange(query: String): Boolean {
                downloadPerson(listView, progressBar, query, true, queryErrorText)
                return false
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                downloadPerson(listView, progressBar, query, false, queryErrorText)
                return false
            }
        })

        setOnCloseListener(listView, queryErrorText)
    }

    fun setOnQueryTextListenerSearchViewInToolBarRoom(listView: ListView, progressBar: ProgressBar, queryErrorText: TextView) {
        searchView?.setOnQueryTextListener( object : SearchView.OnQueryTextListener {

            override fun onQueryTextChange(query: String): Boolean {
                downloadRoom(listView, progressBar, query, true, queryErrorText)
                return false
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                downloadRoom(listView, progressBar, query, false, queryErrorText)
                return false
            }
        })

        setOnCloseListener(listView, queryErrorText)
    }

    fun setOnQueryTextListenerSearchViewInToolBarCourse(listView: ListView, progressBar: ProgressBar, queryErrorText: TextView) {
        searchView?.setOnQueryTextListener( object : SearchView.OnQueryTextListener {

            override fun onQueryTextChange(query: String): Boolean {
                downloadCourse(listView, progressBar, query, true, queryErrorText)
                return false
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                downloadCourse(listView, progressBar, query, false, queryErrorText)
                return false
            }
        })

        setOnCloseListener(listView, queryErrorText)
    }

    fun setOnQueryTextListenerSearchViewInToolBarOrganisation(listView: ListView, progressBar: ProgressBar, queryErrorText: TextView) {
        searchView?.setOnQueryTextListener( object : SearchView.OnQueryTextListener {

            override fun onQueryTextChange(query: String): Boolean {
                downloadOrganisation(listView, progressBar, query, true, queryErrorText)
                return false
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                downloadOrganisation(listView, progressBar, query, false, queryErrorText)
                return false
            }
        })

        setOnCloseListener(listView, queryErrorText)
    }

    fun downloadPerson(listView: ListView, progressBar: ProgressBar, query: String, keyboardOpen: Boolean, queryErrorText: TextView) {

        if(ObjectCache.internetConnected) {
            if (query.count() > 2) {
                handler.removeCallbacksAndMessages(null)
                queryErrorText.visibility = View.INVISIBLE
                if (progressBar.visibility == View.INVISIBLE) {
                    progressBar.visibility = View.VISIBLE
                }
                APIManager.instance.startDownloadPerson(this@MainActivity,
                    searchView?.query.toString(),
                    {
                        listView.adapter = PersonAdapter(this@MainActivity,
                            R.layout.row_person, it)
                        ObjectCache.keyboardOpen = keyboardOpen
                        progressBar.visibility = View.INVISIBLE

                        if(it.isEmpty()){
                            queryErrorText.text = getString(R.string.noResults)
                            queryErrorText.visibility = View.VISIBLE
                        } else {
                            queryErrorText.visibility = View.INVISIBLE
                        }

                    })
            } else if (query.count() > 0) {
                queryErrorText.visibility = View.INVISIBLE
                handler.postDelayed({
                    queryErrorText.text = getString(R.string.min3)
                    queryErrorText.visibility = View.VISIBLE
                }, 1000)
            }
        }
        else
        {
            queryErrorText.text = getString(R.string.noInternet)
            queryErrorText.visibility = View.VISIBLE
        }
    }

    fun downloadRoom(listView: ListView, progressBar: ProgressBar, query: String, keyboardOpen: Boolean, queryErrorText: TextView) {

        if(ObjectCache.internetConnected) {
            if (query.count() > 1) {
                handler.removeCallbacksAndMessages(null)
                queryErrorText.visibility = View.INVISIBLE
                if (progressBar.visibility == View.INVISIBLE) {
                    progressBar.visibility = View.VISIBLE
                }
                APIManager.instance.startDownloadRoom(this@MainActivity,
                    searchView?.query.toString(),
                    {
                        listView.adapter = RoomAdapter(this@MainActivity,
                            R.layout.row_room, it)
                        ObjectCache.keyboardOpen = keyboardOpen
                        progressBar.visibility = View.INVISIBLE

                        if(it.isEmpty()){
                            queryErrorText.text = getString(R.string.noResults)
                            queryErrorText.visibility = View.VISIBLE
                        } else {
                            queryErrorText.visibility = View.INVISIBLE
                        }
                    })
            } else if (query.count() > 0) {
                queryErrorText.visibility = View.INVISIBLE
                handler.postDelayed({
                    queryErrorText.text = getString(R.string.min2)
                    queryErrorText.visibility = View.VISIBLE
                }, 1000)
            }
        }
        else
        {
            queryErrorText.text = getString(R.string.noInternet)
            queryErrorText.visibility = View.VISIBLE
        }

    }

    fun downloadCourse(listView: ListView, progressBar: ProgressBar, query: String, keyboardOpen: Boolean, queryErrorText: TextView) {

        if(ObjectCache.internetConnected) {
            if(query.count() > 2) {
                handler.removeCallbacksAndMessages(null)
                queryErrorText.visibility = View.INVISIBLE
                if(progressBar.visibility == View.INVISIBLE)
                {
                    progressBar.visibility = View.VISIBLE
                }
                APIManager.instance.startDownloadCourse(this@MainActivity, searchView?.query.toString(),
                    {
                        listView.adapter = CourseAdapter(this@MainActivity,
                            R.layout.row_course, it)
                        ObjectCache.keyboardOpen = keyboardOpen
                        progressBar.visibility = View.INVISIBLE

                        if(it.isEmpty()){
                            queryErrorText.text = getString(R.string.noResults)
                            queryErrorText.visibility = View.VISIBLE
                        } else {
                            queryErrorText.visibility = View.INVISIBLE
                        }
                    })
            } else if (query.count() > 0) {
                queryErrorText.visibility = View.INVISIBLE
                handler.postDelayed({
                    queryErrorText.text = getString(R.string.min3)
                    queryErrorText.visibility = View.VISIBLE
                }, 1000)
            }
        }
        else
        {
            queryErrorText.text = getString(R.string.noInternet)
            queryErrorText.visibility = View.VISIBLE
        }
    }

    fun downloadOrganisation(listView: ListView, progressBar: ProgressBar, query: String, keyboardOpen: Boolean, queryErrorText: TextView) {

        if(ObjectCache.internetConnected) {
        if(query.count() > 2) {
            handler.removeCallbacksAndMessages(null)
            queryErrorText.visibility = View.INVISIBLE
            if(progressBar.visibility == View.INVISIBLE)
            {
                progressBar.visibility = View.VISIBLE
            }

            APIManager.instance.startDownloadOrganisation(this@MainActivity, searchView?.query.toString(),
                {
                    listView.adapter = OrganisationAdapter(this@MainActivity,
                        R.layout.row_organisation, it)
                    ObjectCache.keyboardOpen = keyboardOpen
                    progressBar.visibility = View.INVISIBLE

                    if(it.isEmpty()){
                        queryErrorText.text = getString(R.string.noResults)
                        queryErrorText.visibility = View.VISIBLE
                    } else {
                        queryErrorText.visibility = View.INVISIBLE
                    }
                })
            } else if (query.count() > 0) {
                queryErrorText.visibility = View.INVISIBLE
                handler.postDelayed({
                    queryErrorText.text = getString(R.string.min3)
                    queryErrorText.visibility = View.VISIBLE
                }, 1000)
            }
        }
        else
        {
            queryErrorText.text = getString(R.string.noInternet)
            queryErrorText.visibility = View.VISIBLE
        }
    }

    fun setOnCloseListener(listView: ListView, queryErrorText: TextView) {
        searchView?.setOnCloseListener {
            listView.adapter = null

            if(ObjectCache.internetConnected)
            {
                queryErrorText.visibility = View.INVISIBLE
            }

            setActionBarTitle(ObjectCache.actualFragment)
            false
        }

        resetCache()
    }

//    fun setSearchIconVisibility(status: Boolean, collapse: Boolean = false) {
//        searchItem?.isVisible = status
//        if(collapse)
//        {
//            searchView?.onActionViewCollapsed()
//        }
//    }

    fun setQuery(query: String) {
        searchView?.setQuery(query, false)
    }

    fun closeKeyboard() {
        val current = this.currentFocus
        current?.let {
            inputMethodManager?.hideSoftInputFromWindow(current.windowToken, 0)
        }
    }

    fun openKeyboard() {
        val current = this.currentFocus
        current?.let {
            inputMethodManager?.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
        }
        searchView?.requestFocus()
    }

    fun setActionBarTitle(title: String?) {
        supportActionBar!!.title = title
    }

    fun resetCache() {
        ObjectCache.keyboardOpen = false
    }

    fun getQueryText() : String {
        return searchView?.query.toString()
    }

    override fun onBackPressed() {

        Log.e("Main", "Back pressed inanimation = " + ObjectCache.inAnimation)
        if(!ObjectCache.inAnimation)
        {
            super.onBackPressed()
        }
    }
}
