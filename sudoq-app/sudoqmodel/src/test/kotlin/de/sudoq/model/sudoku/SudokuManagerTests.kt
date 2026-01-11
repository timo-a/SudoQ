package de.sudoq.model.sudoku;

import io.mockk.every
import io.mockk.mockk

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout

import de.sudoq.model.persistence.IRepo;
import de.sudoq.model.persistence.xml.sudoku.ISudokuRepoProvider;
import de.sudoq.model.ports.persistence.ReadRepo
import de.sudoq.model.solverGenerator.utils.SudokuTypeRepo4Tests;
import de.sudoq.model.sudoku.complexity.Complexity;
import de.sudoq.model.sudoku.sudokuTypes.SudokuType
import de.sudoq.model.sudoku.sudokuTypes.SudokuTypes;
import java.util.concurrent.TimeUnit

class SudokuManagerTests {

	private val sudokuTypeRepo : ReadRepo<SudokuType> = SudokuTypeRepo4Tests();

    private val sudokuRepo : IRepo<Sudoku> = object : IRepo<Sudoku> {

        override fun create() : Sudoku {
            val sudoku = mockk<Sudoku>()
            every { sudoku.id }.returns(5)
            return sudoku;
        }

        override fun read(id : Int) : Sudoku {

            val pattern = """
            .1  7  2 .4 .3 .5  8  6 .9
            .8 .9  5  7 .2 .6 .3 .4  1
            .3  4 .6  1  8  9 .2  7 .5 
             5 .1 .3 .2 .4  8 .6 .9 .7
             4 .2 .9 .5 .6 .7  1 .8 .3
             7 .6  8 .3 .9 .1  4  5  2    
            .2 .8 .4 .9  5 .3 .7  1  6    
             9  3 .1 .6 .7  4  5 .2 .8    
            .6  5  7 .8 .1  2 .9  3  4
            """.trimIndent()

            require(pattern.split(Regex("\\s+")).size == 81)

            fun parseCell(chunk: String, id : Int) : Cell =
                if (chunk.startsWith('.')) {
                    Cell(true, chunk.substring(1).toInt(), id, 9)
                } else {
                    Cell(false, chunk.toInt(), id, 9)
                }

            val cells = pattern.split(Regex("\\s+"))
                .mapIndexed { i, chunk -> Position[i % 9, i / 9] to parseCell(chunk, i) }
                .associate { it }
                .let { HashMap(it) }

            return Sudoku(5, 0, sudokuTypeRepo.read(SudokuTypes.standard9x9.ordinal),
                Complexity.infernal, cells)
        }

        override fun update(cells : Sudoku) : Sudoku {
            return cells;
        }

        override fun delete(id : Int) {  }

        override fun ids() : List<Int> {
            return listOf(5)
        }
    };

    val sudokuRepoProvider : ISudokuRepoProvider = object : ISudokuRepoProvider {

        override fun getRepo (type : SudokuTypes, complexity : Complexity) : IRepo<Sudoku> {
            require(type == SudokuTypes.standard9x9)
            require(complexity == Complexity.infernal)
            return sudokuRepo;
        }

        override fun getRepo (sudoku : Sudoku) : IRepo<Sudoku> {
            return sudokuRepo;
        }
    };

    @Test
    @Timeout(12, unit = TimeUnit.SECONDS)// threw an exception and ran forever in the past -> timeout
	fun test() {
		//assertEquals(21, FileManager.getSudokuCountOf(SudokuTypes.standard9x9, Complexity.infernal));
        val s : Sudoku = SudokuManager(sudokuTypeRepo, sudokuRepoProvider)
				.getNewSudoku(SudokuTypes.standard9x9, Complexity.infernal);
        //after 10 tmes of reuse, a new sudoku must be generated
        repeat(10) { s.increaseTransformCount(); }
		val sm = object : SudokuManager(sudokuTypeRepo, sudokuRepoProvider) {
			override fun generationFinished(sudoku : Sudoku) {
				synchronized (this@SudokuManagerTests) {
					super.generationFinished(sudoku);
				}
			}
		};
		sm.usedSudoku(s);
		//assertEquals(21, FileManager.getSudokuCountOf(SudokuTypes.standard9x9, Complexity.infernal));
	}
}
