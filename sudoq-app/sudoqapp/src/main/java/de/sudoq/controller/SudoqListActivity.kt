/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.controller

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.HeaderViewListAdapter
import android.widget.ListAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import de.sudoq.R
import de.sudoq.controller.tutorial.TutorialActivity
import de.sudoq.model.files.FileManager

/**
 * Eine ListActivity, welche die für einwandfreie Funktionalität der SudoQ-App
 * notwendigen Initialisierungsarbeiten ausführt.
 */
open class SudoqListActivity : AppCompatActivity() {
    private var mListView: ListView? = null
    protected val listView: ListView?
        protected get() {
            if (mListView == null) {
                mListView = findViewById<View>(android.R.id.list) as ListView
            }
            return mListView
        }
    protected var listAdapter: ListAdapter?
        protected get() {
            val adapter = listView!!.adapter
            return if (adapter is HeaderViewListAdapter) {
                adapter.wrappedAdapter
            } else {
                adapter
            }
        }
        protected set(adapter) {
            listView!!.adapter = adapter
        }

    /**
     * Initialisiert eine neue Activity, setzt dabei die für die App notwendigen
     * System-Properties und initialisiert den FileManager.
     */
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        System.setProperty("org.xml.sax.driver", "org.xmlpull.v1.sax2.Driver")
        //getDir(getString(R.string.path_rel_profiles), Context.MODE_PRIVATE),
        FileManager.initialize(getDir(getString(R.string.path_rel_sudokus), MODE_PRIVATE))
    }

    /**
     * {@inheritDoc}
     */
    public override fun onDestroy() {
        super.onDestroy()
        System.gc()
    }

    /**
     * Erstellt das Optionsmenü mit einem Tutorial-Eintrag.
     *
     * @param menu
     * Das Menü
     * @return true
     */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.action_bar_standard, menu)
        return super.onCreateOptionsMenu(menu)
    }

    /**
     * Verarbeitet das Auswählen des Tutorial-Menüeintrags.
     *
     * @param item
     * Das ausgewählte Menü-Item
     * @return true
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_show_tutorial -> {
                startActivity(Intent(this, TutorialActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}