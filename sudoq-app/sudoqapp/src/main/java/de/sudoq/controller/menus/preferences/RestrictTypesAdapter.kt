/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.controller.menus.preferences

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import de.sudoq.R
import de.sudoq.controller.menus.Utility
import de.sudoq.model.sudoku.sudokuTypes.SudokuTypes
import de.sudoq.model.xml.SudokuTypesList

/**
 * Adapter für die Anzeige aller zu wählenden Sudoku Typen
 */
class RestrictTypesAdapter(context: Context, typesList: SudokuTypesList) :
    ArrayAdapter<SudokuTypes>(context, R.layout.restricttypes_item, typesList.allTypes) {

    private val types: List<SudokuTypes>

    /**
     * {@inheritDoc}
     */
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val rowView = inflater.inflate(R.layout.restricttypes_item, parent, false)
        val type = super.getItem(position)!!
        val full = Utility.type2string(context, type) //translated name of Sudoku type;
        (rowView.findViewById(R.id.regular_languages_layout) as View).visibility = View.GONE
        (rowView.findViewById(R.id.irregular_languages_layout) as View).visibility = View.VISIBLE
        val sudokuType = rowView.findViewById<View>(R.id.combined_label) as TextView
        sudokuType.text = full
        val color = if (types.contains(type)) Color.BLACK else Color.LTGRAY
        sudokuType.setTextColor(color)
        return rowView
    }

    companion object {
        private val LOG_TAG = RestrictTypesAdapter::class.java.simpleName
    }

    /**
     * Erzeugt einen neuen SudokuLoadingAdpater mit den gegebenen Parametern
     *
     * @param context
     * der Applikationskontext
     * @param typesList
     * die Liste der Typen
     */
    init {
        types = typesList
        Log.d("rtAdap", "rtAdap is initialized, size: " + types.size)
    }
}