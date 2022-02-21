package at.tug.search.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import at.tug.search.R

class AboutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)


        supportActionBar?.title = getString(R.string.menu_about)





    }
}
