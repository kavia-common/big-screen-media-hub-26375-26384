package org.example.app

import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import org.example.app.model.NavDestination
import org.example.app.ui.EpgFragment
import org.example.app.ui.FavoritesFragment
import org.example.app.ui.SearchFragment
import org.example.app.ui.VodFragment

class MainActivity : FragmentActivity() {

    private lateinit var titleText: TextView
    private lateinit var searchInput: EditText
    private lateinit var contentContainer: FrameLayout

    private var currentDestination: NavDestination = NavDestination.EPG

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        titleText = findViewById(R.id.topTitle)
        searchInput = findViewById(R.id.searchInput)
        contentContainer = findViewById(R.id.contentContainer)

        // Route Enter/DPAD_CENTER to Search screen.
        searchInput.setOnEditorActionListener { _, _, _ ->
            openSearch(searchInput.text?.toString().orEmpty())
            true
        }
        searchInput.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_UP && (keyCode == KeyEvent.KEYCODE_DPAD_CENTER || keyCode == KeyEvent.KEYCODE_ENTER)) {
                openSearch(searchInput.text?.toString().orEmpty())
                true
            } else {
                false
            }
        }

        // Side menu items
        val menuEpg = findViewById<TextView>(R.id.menuEpg)
        val menuVod = findViewById<TextView>(R.id.menuVod)
        val menuFav = findViewById<TextView>(R.id.menuFavorites)

        menuEpg.setOnClickListener { navigateTo(NavDestination.EPG) }
        menuVod.setOnClickListener { navigateTo(NavDestination.VOD) }
        menuFav.setOnClickListener { navigateTo(NavDestination.FAVORITES) }

        // Provide a basic focus loop: when a menu item gets focus, update selection styling.
        val menuFocusListener = View.OnFocusChangeListener { v, hasFocus ->
            if (v is TextView) {
                v.isSelected = hasFocus
            }
        }
        menuEpg.onFocusChangeListener = menuFocusListener
        menuVod.onFocusChangeListener = menuFocusListener
        menuFav.onFocusChangeListener = menuFocusListener

        // Initial destination
        if (savedInstanceState == null) {
            navigateTo(NavDestination.EPG)
            menuEpg.requestFocus()
        }
    }

    private fun openSearch(query: String) {
        titleText.text = getString(R.string.title_search)
        val f = SearchFragment.newInstance(query)
        replaceContent(f)
        currentDestination = NavDestination.SEARCH
    }

    private fun navigateTo(destination: NavDestination) {
        currentDestination = destination
        titleText.text = when (destination) {
            NavDestination.EPG -> getString(R.string.title_epg)
            NavDestination.VOD -> getString(R.string.title_vod)
            NavDestination.FAVORITES -> getString(R.string.title_favorites)
            NavDestination.SEARCH -> getString(R.string.title_search)
            NavDestination.PLAYER -> getString(R.string.title_player)
        }

        val fragment: Fragment = when (destination) {
            NavDestination.EPG -> EpgFragment.newInstance()
            NavDestination.VOD -> VodFragment.newInstance()
            NavDestination.FAVORITES -> FavoritesFragment.newInstance()
            NavDestination.SEARCH -> SearchFragment.newInstance(searchInput.text?.toString().orEmpty())
            NavDestination.PLAYER -> SearchFragment.newInstance("") // should never happen via side menu
        }
        replaceContent(fragment)
    }

    private fun replaceContent(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.contentContainer, fragment)
            .commit()
    }
}
