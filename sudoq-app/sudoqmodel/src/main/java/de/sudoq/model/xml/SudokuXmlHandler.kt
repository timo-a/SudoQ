/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.model.xml

import de.sudoq.model.files.FileManager
import de.sudoq.model.sudoku.Sudoku
import de.sudoq.model.sudoku.complexity.Complexity
import de.sudoq.model.sudoku.sudokuTypes.SudokuTypes
import java.io.File

/**
 * This class aids in converting sudokus into and from XML
 *
 * @property type [Type][SudokuTypes] of the Sudoku
 * @property complexity [Complexity] of the Sudoku
 */
open class SudokuXmlHandler @JvmOverloads constructor(
        private val type: SudokuTypes? = null,
        private val complexity: Complexity? = null) : XmlHandler2<Sudoku>() {

    /**
     * {@inheritDoc}
     */
    protected override fun getFileFor(s: Sudoku): File {
        return if (type != null && complexity != null) {
            FileManager.getRandomSudoku(type, complexity)
        } else if (s.id <= 0) {
            FileManager.getNewSudokuFile(s)
        } else {
            FileManager.getSudokuFile(s)
        }
    }

    override fun modifySaveTree(tree: XmlTree) {
        //  7] cuts sudoku_
        // [-4 cuts the .xml todo use regex instead
        val idFromFileName : String = file!!.name.substring(7, file!!.name.length - 4)
        tree.addAttribute(XmlAttribute("id", idFromFileName))
    }

}