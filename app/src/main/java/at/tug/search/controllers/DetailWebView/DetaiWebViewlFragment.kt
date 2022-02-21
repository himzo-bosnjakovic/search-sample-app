package at.tug.search.controllers.DetailWebView

import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import at.tug.search.activity.MainActivity
import at.tug.search.R
import at.tug.search.utils.ObjectCache


class DetaiWebViewlFragment : Fragment() {

    private var progressBar: ProgressBar? = null
    private var TuUrl: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_web_view, container, false)
        val browser : WebView = root.findViewById(R.id.detailWebView)
        progressBar = root.findViewById(R.id.detailWebViewProgressBar)

        (activity as MainActivity).closeKeyboard()
        (activity as MainActivity).setActionBarTitle(arguments?.getString("title"))


        progressBar?.max = 100
        TuUrl = arguments?.getString("url")

        browser.webViewClient = WebViewClientDemo()
        browser.webChromeClient = WebChromeClientDemo()
        browser.settings.allowContentAccess = false
        browser.settings.javaScriptEnabled = true
        browser.loadUrl(TuUrl)

        Handler().postDelayed({
            ObjectCache.inAnimation = false}, 350
        )

        return root
    }

    private inner class WebViewClientDemo : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            if(url == TuUrl)
            {
                view.loadUrl(url)
            }
            return true
        }

        override fun onPageFinished(view: WebView, url: String) {
            super.onPageFinished(view, url)
        }

        override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            progressBar?.isVisible = true
            progressBar?.progress = 0
        }
    }

    private inner class WebChromeClientDemo : WebChromeClient() {
        override fun onProgressChanged(view: WebView, progress: Int) {
            progressBar?.progress = progress
            if(progress > 95)
            {
                progressBar?.isVisible = false
            }
        }
    }
}