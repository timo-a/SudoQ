/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.model.xml

import de.sudoq.model.files.FileManager
import de.sudoq.model.game.Game
import de.sudoq.model.profile.Profile
import java.io.File

/**
 * This class aids in converting concrete games into and from XML
 */
class GameXmlHandler @JvmOverloads constructor(private val id: Int = -1, val p: Profile) : XmlHandler<Game>() {

    /**
     * {@inheritDoc}
     */
    protected override fun getFileFor(g: Game): File {
        return FileManager.getGameFile(if (id > 0) id else g.id, p)
    }
}