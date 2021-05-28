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
import androidx.appcompat.app.AppCompatActivity
import de.sudoq.R
import de.sudoq.controller.tutorial.TutorialActivity
import de.sudoq.model.files.FileManager

/**
 * Eine Activity, welche die für einwandfreie Funktionalität der SudoQ-App
 * notwendigen Initialisierungsarbeiten ausführt.
 */
open class SudoqCompatActivity : AppCompatActivity() {
    /**
     * Initialisiert eine neue Activity, setzt dabei die für die App notwendigen
     * System-Properties und initialisiert den FileManager.
     */
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        System.setProperty("org.xml.sax.driver", "org.xmlpull.v1.sax2.Driver")
        val profilesDir = getDir(getString(R.string.path_rel_profiles), MODE_PRIVATE)
        val sudokusDir = getDir(getString(R.string.path_rel_sudokus), MODE_PRIVATE)
        FileManager.initialize(sudokusDir)
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
     * Fügt dem übergebenen Menü die globalen Einträge (Tutorial) hinzu.
     *
     * @param menu
     * Das Menü
     *
     * protected void prepareOptionsMenu(Menu menu) {
     * menu.add(0, MENU_TUTORIAL, 0, getString(R.string.action_show_tutorial));
     * }
     */
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