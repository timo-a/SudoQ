/*
 * SudoQ is a Sudoku-App for Android Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.model.solverGenerator

import de.sudoq.model.solverGenerator.FastSolver.BranchAndBound.FastBranchAndBound
import de.sudoq.model.solverGenerator.FastSolver.FastSolverFactory
import de.sudoq.model.solverGenerator.solution.SolveDerivation
import de.sudoq.model.solverGenerator.solver.ComplexityRelation
import de.sudoq.model.solverGenerator.solver.Solver
import de.sudoq.model.sudoku.*
import de.sudoq.model.sudoku.complexity.ComplexityConstraint
import de.sudoq.model.sudoku.sudokuTypes.SudokuType
import java.util.*

/**
 * Bietet die Möglichkeit Sudokus zu generieren.
 * Die Klasse implementiert das [Runnable] interface
 * und kann daher in einem eigenen Thread ausgeführt werden.
 *
 * @property sudoku Das Sudoku auf welchem die Generierung ausgeführt wird
 * @property Das Objekt, auf dem nach Abschluss der Generierung die Callback-Methode aufgerufen wird
 */
class GenerationAlgo(
    private var sudoku: Sudoku,
    private var callbackObject: GeneratorCallback,
    random: Random
) : Runnable {

    /**
     * Das Zufallsobjekt für den Generator
     */
    private var random: Random = random

    /**
     * Der Solver, der für Validierungsvorgänge genutzt wird
     */
    private var solver: Solver = Solver(sudoku)

    /**
     * List of currently defined(occupied) Fields.
     * If we gave the current sudoku to the user tey wouldn't have to solve these fields
     * as they'd already be filled in.
     */
    private var definedCells: MutableList<Position> = ArrayList()

    /**
     * Die noch freien, also nicht belegten Felder des Sudokus
     */
    private var freeCells: MutableList<Position> = ArrayList(getPositions(sudoku))

    /**
     * Das gelöste Sudoku
     */
    private var solvedSudoku: Sudoku? = null

    /**
     * Die Anzahl der Felder, die fest zu definieren ist
     */
    private var cellsToDefine = 0
    /**
     * Anzahl aktuell definierter Felder
     */
    //private int currentFieldsDefined;
    /**
     * ComplexityConstraint für ein Sudoku des definierten
     * Schwierigkeitsgrades
     */
    private val desiredComplexityConstraint: ComplexityConstraint? =
        sudoku.sudokuType!!.buildComplexityConstraint(sudoku.complexity)


    /**
     * Die Methode, die die tatsächliche Generierung eines Sudokus mit der
     * gewünschten Komplexität generiert.
     */
    override fun run() {
        /* 1. Finde Totalbelegung */
        solvedSudoku = createSudokuPattern()
        createAllocation(solvedSudoku!!)

        // Call the callback
        val suBi = SudokuBuilder(sudoku.sudokuType)
        for (p in getPositions(solvedSudoku!!)) {
            val value = solvedSudoku!!.getCell(p)!!.solution
            suBi.addSolution(p, value)
            if (!sudoku.getCell(p)!!.isNotSolved) suBi.setFixed(p)
        }
        val res = suBi.createSudoku()

        //we want to know the solutions used, so quickly an additional solver
        val quickSolver = Solver(res)
        quickSolver.solveAll(true, false, false)
        res.complexity = sudoku.complexity
        if (callbackObject.toString() == "experiment") {
            callbackObject.generationFinished(res, quickSolver.solutions!!)
        } else {
            callbackObject.generationFinished(res)
        }
    }

    private fun createSudokuPattern(): Sudoku {
        //determine ideal number of prefilled fields
        cellsToDefine = getNumberOfCellsToDefine(sudoku.sudokuType, desiredComplexityConstraint)

        //A mapping from position to solution
        var solution = PositionMap<Int?>(sudoku.sudokuType!!.size!!)
        val iteration = 0
        //System.out.println("Fields to define: "+fieldsToDefine);

        //define fields
        repeat(cellsToDefine + 1) { addDefinedCell() }
        var fieldsToDefineDynamic = cellsToDefine

        /* until a solution is found, remove 5 random fields and add new ones */
        var fs = FastSolverFactory.getSolver(sudoku)
        while (!fs.hasSolution()) {
            //System.out.println("Iteration: "+(iteration++)+", defined Fields: "+definedFields.size());
            // Remove some fields, because sudoku could not be validated
            removeDefinedCells(5)

            // Define average number of fields
            while (definedCells.size < fieldsToDefineDynamic) if (addDefinedCell() == null) //try to add field, if null returned i.e. nospace / invalid
                removeDefinedCells(5) //remove 5 fields
            if (fieldsToDefineDynamic > 0 && random.nextFloat() < 0.2)
                fieldsToDefineDynamic-- //to avoid infinite loop slowly relax
            fs = FastSolverFactory.getSolver(sudoku)
        }

        /* we found a solution i.e. a combination of nxn numbers that fulfill all constraints */

        /* not sure what's happening why tmp-save complexity? is it ever read? maybe in solveall?
			   maybe this is from previous debugging, wanting to see if it's invalid here already

			   solver.validate is definitely needed

			   but the complexity is from the superclass `Sudoku`, SolverSudoku has its own `complexityValue`...
			*/

        //PositionMap<Integer> solution2 = solver.getSudoku().getField();

        //solution is filled with correct solution
        solution = fs.solutions

        /* We have (validated) filled `solution` with the right values */

        // Create the sudoku template generated before
        val sub = SudokuBuilder(sudoku.sudokuType)
        for (p in getPositions(sudoku))
            sub.addSolution(p, solution[p]!!) //fill in all solutions
        return sub.createSudoku()
    }

    private fun createAllocation(pattern: Sudoku) {

        //ensure all fields are defined
        while (freeCells.isNotEmpty()) {
            definedCells.add(freeCells.removeAt(0))
        }


        // Fill the sudoku being generated with template solutions
        //TODO simplify: iterate over fields/positions
        for (pos in getPositions(sudoku)) {
            val fSudoku = sudoku.getCell(pos)
            val fSolved = pattern.getCell(pos)
            fSudoku!!.setCurrentValue(fSolved!!.solution, false)
        }
        val reallocationAmount = 2 //getReallocationAmount(sudoku.getSudokuType(), 0.05);
        var plusminuscounter = 0
        var rel = ComplexityRelation.INVALID
        while (rel !== ComplexityRelation.CONSTRAINT_SATURATION) {


            //every 1000 steps choose another random subset
            if (plusminuscounter % 1000 == 0 && plusminuscounter > 0) {
                //while (!this.freeFields.isEmpty()) {
                //	this.definedFields.add(this.freeFields.remove(0));
                //}
                removeDefinedCells(definedCells.size)
                for (i in 0 until cellsToDefine) {
                    addDefinedCell2()
                }
            }
            sudoku = removeAmbiguity(sudoku)

            //System.out.println(sudoku);

            /*solver = new Solver(sudoku);
				System.out.println("validate:");

				rel = solver.validate(null);
				System.out.println("validate says: " + rel);*/

            //fast validation where after 10 branchpoints we return too diificult
            val solver = FastBranchAndBound(sudoku)
            rel = solver.validate()
            when (rel) {
                ComplexityRelation.MUCH_TOO_EASY -> removeDefinedCells(reallocationAmount)
                ComplexityRelation.TOO_EASY -> removeDefinedCells(1)
                ComplexityRelation.INVALID, ComplexityRelation.TOO_DIFFICULT, ComplexityRelation.MUCH_TOO_DIFFICULT -> {
                    var i = 0
                    while (i < reallocationAmount.coerceAtMost(freeCells.size)) {
                        addDefinedCell2()
                        i++
                    }

                }
            }
            plusminuscounter++
        }
    }

    /*
	 * While there are 2 solutions, add solution that is different in second sudoku
	 * Careful! If looking for the 2nd solution takes longer than x min, sudoku is declared unambiguous
	 */
    private fun removeAmbiguity(sudoku: Sudoku): Sudoku {
        var fs = FastSolverFactory.getSolver(sudoku)
        //samurai take a long time -> try without uniqueness constraint
        //if (sudoku.getSudokuType().getEnumType() != SudokuTypes.samurai)
        while (fs.isAmbiguous) {
            val p = fs.ambiguousPos
            addDefinedCell2(p)
            fs = FastSolverFactory.getSolver(sudoku)
        }
        return sudoku
    }

    private fun reduceStringList(sl: List<String>): String {
        if (sl.isEmpty()) return "[]" else if (sl.size == 1) return "[" + sl[0] + "]"
        var s = ""
        val i = sl.iterator()
        var counter = 1
        var last = i.next()
        while (i.hasNext()) {
            val current = i.next()
            if (last == current) counter++ else {
                s += ", $counter*$last"
                last = current
                counter = 0
            }
        }
        s += ", $counter*$last"
        return '['.toString() + s.substring(2) + ']'
    }

    private fun gettypes(dl: List<SolveDerivation>): List<String> {
        val sl: MutableList<String> = Stack()
        for (sd in dl) {
            sl.add(sd.type.toString())
            //sl.add(sd.toString());
        }
        return sl
    }

    // Calculate the number of fields to be filled
    // the number is determined as the smaller of
    //        - the standard allocation factor defined in the type
    //        - the average #fields per difficulty level defined in the type
    private fun getNumberOfCellsToDefine(
        type: SudokuType?,
        desiredComplexityConstraint: ComplexityConstraint?
    ): Int {
        //TODO What do we have the allocation factor for??? can't it always be expressed through avg-fields?
        val standardAllocationFactor = type!!.getStandardAllocationFactor()
        val cellsOnSudokuBoard = type.size!!.x * type.size!!.y
        val cellsByType =
            (cellsOnSudokuBoard * standardAllocationFactor).toInt() //TODO wäre freeFields.size nicht passender?
        val cellsByComp = desiredComplexityConstraint!!.averageCells
        return Math.min(cellsByType, cellsByComp)
    }

    /** returns `percentage` percent of the #positions in the type
     * e.g. for standard 9x9 and 0.5 -> 40  */
    private fun getReallocationAmount(st: SudokuType, percentage: Double): Int {
        var numberOfPositions = 0
        for (p in sudoku.sudokuType!!.validPositions) numberOfPositions++
        val reallocationAmount =
            (numberOfPositions * percentage).toInt() //remove/delete up to 10% of board
        return Math.max(1, reallocationAmount) // at least 1
    }

    /**
     * Definiert ein weiteres Feld, sodass weiterhin Constraint Saturation
     * vorhanden ist. Die Position des definierten Feldes wird
     * zurückgegeben. Kann keines gefunden werden, so wird null
     * zurückgegeben.
     *
     * This method is to be used for initialization only, once a solution is found
     * please use addDefinedField2, with just chooses a free field to define
     * and assumes constraint saturation.
     *
     * @return Die Position des definierten Feldes oder null, falls keines
     * gefunden wurde
     */
    private fun addDefinedCell(): Position? {
        //TODO not sure what they do
        val xSize = sudoku.sudokuType!!.size!!.x
        val ySize = sudoku.sudokuType!!.size!!.y

        // Ein Array von Markierungen zum Testen, welches Felder belegt werden können
        /*true means marked, i.e. already defined or not part of the game e.g. 0,10 for samurai
			 *false means can be added
			 */
        val markings = Array(xSize) { BooleanArray(ySize) } //all false by default.


        //definierte Felder markieren
        for (p in definedCells) {
            markings[p.x][p.y] = true
        }

        /* avoids infitite while loop*/
        var count = definedCells.size

        //find random {@code Position} p
        var p: Position? = null
        while (p == null && count < xSize * ySize) {
            val x = random.nextInt(xSize)
            val y = random.nextInt(ySize)
            if (sudoku.getCell(Position[x, y]) == null) { //position existiert nicht
                markings[x][y] = true
                count++
            } else if (!markings[x][y]) { //pos existiert und ist unmarkiert
                p = Position[x, y]
            }
        }

        //construct a list of symbols starting at arbitrary point. there is no short way to do this without '%' 
        val numSym = sudoku.sudokuType!!.numberOfSymbols
        val offset = random.nextInt(numSym)
        val symbols: Queue<Int> = LinkedList()
        for (i in 0 until numSym) symbols.add(i)
        for (i in 0 until offset)  //rotate offset times
            symbols.add(symbols.poll())

        //constraint-saturierende belegung suchen 
        var valid = false
        for (s in symbols) {
            sudoku.getCell(p!!)!!.setCurrentValue(s, false)
            //alle constraints saturiert?
            valid = sudoku.sudokuType!!.all { it.isSaturated(sudoku) }

            if (!valid)
                sudoku.getCell(p)!!.setCurrentValue(Cell.EMPTYVAL, false)


            if (valid) {
                definedCells.add(p)
                freeCells.remove(p) //if it's defined it is no longer free
                break
            }
        }
        if (!valid) p = null
        return p
    }

    /**
     * choses a random free field and sets it as defined
     */
    private fun addDefinedCell2(i: Int = random.nextInt(freeCells.size)) {
        val p = freeCells.removeAt(i) //used to be 0, random just in case
        val fSudoku = sudoku.getCell(p)
        val fSolved = solvedSudoku!!.getCell(p)
        fSudoku!!.setCurrentValue(fSolved!!.solution, false)
        definedCells.add(p)
    }

    private fun addDefinedCell2(p: Position) {
        val i = freeCells.indexOf(p)
        require(i >= 0) { "position is not free, so it cannot be defined." }
        addDefinedCell2(i)
    }

    /**
     * Removes one of the defined fields (random selection)
     *
     * @return position of removed field or null is nothing there to remove
     */
    private fun removeDefinedCell(): Position? {
        if (definedCells.isEmpty()) return null
        val nr = random.nextInt(definedCells.size)
        val p = definedCells.removeAt(nr)
        sudoku.getCell(p)!!.setCurrentValue(Cell.EMPTYVAL, false)
        freeCells.add(p)
        return p
    }

    /**
     * Tries `numberOfFieldsToRemove` times to remove a defined field
     * @param numberOfCellsToRemove number of fields to remove
     * @return list of removed positions
     */
    private fun removeDefinedCells(numberOfCellsToRemove: Int): List<Position> {
        return (0 until numberOfCellsToRemove).mapNotNull { removeDefinedCell() }
    }

    /* debugging, remove when done */
    fun printDebugMsg() {
        println("This is the debug message from `Generator`")
    }

    /*fun saveSudokuAllInOne(path: String, filename: String, sudoku: Sudoku) {
        FileManager.initialize(File(path))
        object : SudokuXmlHandler() {
            override fun getFileFor(s: Sudoku): File {
                return File(path + File.separator + filename)
            }

            override fun modifySaveTree(tree: XmlTree) {
                tree.addAttribute(XmlAttribute("id", "42"))
            }
        }.saveAsXml(sudoku)
    }*/

    companion object {
        /**
         * returns all positions of non-null Fields of sudoku
         * @param sudoku a sudoku object
         *
         * @return list of positions whose corresponding `Field` objects are not null
         */
        @JvmStatic ///todo Generator has same function...
        fun getPositions(sudoku: Sudoku): List<Position> {
            val p: MutableList<Position> = ArrayList()
            for (x in 0 until sudoku.sudokuType!!.size!!.x)
                for (y in 0 until sudoku.sudokuType!!.size!!.y)
                    if (sudoku.getCell(Position[x, y]) != null)
                        p.add(Position[x, y])
            return p
        }

        //nono usage found
        /*fun getSudoku(path: String, st: SudokuTypes): Sudoku? {
            Profile.getInstance(File("/home/t/Code/SudoQ/DebugOnPC/profilefiles"))//todo is this used by the app??? try to delete

            FileManager.initialize(
                    File("/home/t/Code/SudoQ/sudoq-app/sudoqapp/src/main/assets/sudokus/"))
            val f = File(path)

            val s = Sudoku(getSudokuType(st)!!)
            try {
                s.fillFromXml(XmlHelper().loadXml(f)!!)
                s.complexity = Complexity.arbitrary //justincase
                return s
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return null
        }*/
    }

}