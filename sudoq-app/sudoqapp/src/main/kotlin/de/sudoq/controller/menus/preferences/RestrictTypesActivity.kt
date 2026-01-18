/*
 * SudoQ is a Sudoku-App for Android Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.controller.menus.preferences

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.AdapterView.OnItemLongClickListener
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import dagger.hilt.android.AndroidEntryPoint
import de.sudoq.R
import de.sudoq.controller.SudoqListActivity
import de.sudoq.model.profile.ProfileManager
import de.sudoq.model.sudoku.sudokuTypes.SudokuTypes
import javax.inject.Inject

/**
 * Diese Klasse repräsentiert den Lade-Controller des Sudokuspiels. Mithilfe von
 * SudokuLoading können Sudokus geladen werden und daraufhin zur SudokuActivity
 * gewechselt werden.
 */
@AndroidEntryPoint
class RestrictTypesActivity : SudoqListActivity(), OnItemClickListener, OnItemLongClickListener {

    @Inject
    lateinit var profileManager: ProfileManager

    /** Attributes  */
    private var adapter: RestrictTypesAdapter? = null
    private var types: ArrayList<SudokuTypes>? = null
    /** Constructors  */
    /** Methods  */
    /**
     * Wird aufgerufen, wenn SudokuLoading nach Programmstart zum ersten Mal
     * geladen aufgerufen wird. Hier wird das Layout inflated und es werden
     * nötige Initialisierungen vorgenommen.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.restricttypes)
        Log.d("gameSettings", "RestrictTypesActivity.onCreate")
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        val ab = supportActionBar
        ab!!.setHomeAsUpIndicator(R.drawable.launcher)
        ab.setDisplayHomeAsUpEnabled(true)
        ab.setDisplayShowTitleEnabled(true)
        initialiseTypes()
    }

    /**
     * Wird beim ersten Anzeigen des Options-Menü von SudokuLoading aufgerufen
     * und initialisiert das Optionsmenü indem das Layout inflated wird.
     *
     * @return true falls das Options-Menü angezeigt werden kann, sonst false
     */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.action_bar_restrict_types, menu)
        return true
    }

    /**
     * Wird beim Auswählen eines Menü-Items im Options-Menü aufgerufen. Ist das
     * spezifizierte MenuItem null oder ungültig, so wird nichts getan.
     *
     * @param item
     * Das ausgewählte Menü-Item
     * @return true, falls die Selection hier bearbeitet wird, false falls nicht
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_restore_all ->
                /* add (only!) types that are not currently selected */
                types!!.addAll(SudokuTypes.values().filter { !types!!.contains(it) })

            else -> super.onOptionsItemSelected(item)
        }
        onContentChanged()
        return false
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        super.onPrepareOptionsMenu(menu)

        //Toast.makeText(getApplicationContext(), "prepOpt called. s_1: "+types.size()+" s_2: "+types.getAllTypes().size(), Toast.LENGTH_LONG).show();
        menu.findItem(R.id.action_restore_all).isVisible =
            types!!.size < SudokuTypes.values().size //offer option to restore all only when some are disabled...
        return true
    }

    /**
     * {@inheritDoc}
     */
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    /**
     * {@inheritDoc}
     */
    override fun onContentChanged() {
        super.onContentChanged()
        initialiseTypes()
    }

    /**
     * Wird aufgerufen, falls ein Element (eine View) in der AdapterView
     * angeklickt wird.
     *
     * @param parent
     * AdapterView in welcher die View etwas angeklickt wurde
     * @param view
     * View, welche angeklickt wurde
     * @param position
     * Position der angeklickten View im Adapter
     * @param id
     * ID der angeklickten View
     */
    override fun onItemClick(parent: AdapterView<*>?, view: View, position: Int, id: Long) {

        /* toggle item */
        val st = adapter!!.getItem(position)
        if (types!!.contains(st!!) && types!!.size == 1) //trying to remove last element -> deny and warn
            Toast.makeText(
                this,
                R.string.advanced_settings_restrict_types_empty_warning,
                Toast.LENGTH_SHORT
            ) else if (types!!.contains(st)) types!!.remove(st) else types!!.add(st)
        profileManager.saveChanges()
        adapter!!.notifyDataSetChanged()
    }

    override fun onItemLongClick(
        parent: AdapterView<*>?,
        view: View,
        position: Int,
        id: Long
    ): Boolean {

        /* nothing */
        return true //prevent itemclick from fire-ing as well
    }

    private fun initialiseTypes() {
        types = profileManager.assistances.wantedTypesList
        // initialize ArrayAdapter for the type names and set it
        adapter = RestrictTypesAdapter(this, types!!)
        listAdapter = adapter
        listView!!.onItemClickListener = this
        listView!!.onItemLongClickListener = this
    }

    /**
     * Führt die onBackPressed-Methode aus.
     *
     * @param view
     * unbenutzt
     */
    fun goBack(view: View?) {
        super.onBackPressed()
    }

    companion object {
    }
}
