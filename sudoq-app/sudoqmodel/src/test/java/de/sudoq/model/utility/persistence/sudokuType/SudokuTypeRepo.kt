package de.sudoq.model.utility.persistence.sudokuType

/*class SudokuTypeRepo() : ReadRepo<SudokuType> {

    private val typesDir: File = FileUtils.getFile("src","test", "resources", "persistence", "SudokuTypes");

    override fun create(): SudokuType {
        TODO("Not yet implemented")
    }

    override fun read(id: Int): SudokuType {
        val st: SudokuTypes = SudokuTypes.values()[id]
        return getSudokuType(st)
    }

    /**
     * Gibt die Sudoku-Typdatei für den spezifizierten Typ zurück.
     * @param type die Typ-Id
     * @return die entsprechende Sudoku-Typdatei
     */
    private fun getSudokuTypeFile(type: SudokuTypes): File {
        return File(typesDir, "$type.xml");
    }

    /**
     * Creates and returns a SudokuType subject to the specified Type Name.
     * If the type cannot be mapped to a type null is returned.
     *
     * @param type Enum Type of the SudokuType to create.
     * @return a [SudokuType] of null if type cannot be mapped
     */
    private fun getSudokuType(type: SudokuTypes): SudokuType {
        val f = getSudokuTypeFile(type)
        if (!f.exists()) {
            throw IllegalStateException("no sudoku type file found for $type")
        }
        val helper = XmlHelper()
        try {
            val t = SudokuTypeBE()
            val xt = helper.loadXml(f)!!
            t.fillFromXml(xt)
            return SudokuTypeMapper.fromBE(t)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        throw IllegalStateException("Something went wrong loading sudoku type for $type")
    }


    override fun update(t: SudokuType): SudokuType {
        TODO("Not yet implemented")
    }

    override fun delete(id: Int) {
        TODO("Not yet implemented")
    }

    override fun ids(): List<Int> {
        TODO("Not yet implemented")
    }


}*/