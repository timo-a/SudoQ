package de.sudoq.model.sudoku;

import io.mockk.every
import io.mockk.mockk

import java.io.File;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout

import de.sudoq.model.persistence.IRepo;
import de.sudoq.model.persistence.xml.sudoku.ISudokuRepoProvider;
import de.sudoq.model.solverGenerator.utils.SudokuTypeRepo4Tests;
import de.sudoq.model.sudoku.sudokuTypes.SudokuType;
import de.sudoq.model.sudoku.complexity.Complexity;
import de.sudoq.model.sudoku.sudokuTypes.SudokuTypes;
import de.sudoq.persistence.XmlHelper;
import de.sudoq.persistence.sudoku.SudokuBE;
import de.sudoq.persistence.sudoku.SudokuMapper;
import java.io.IOException
import java.util.concurrent.TimeUnit

class SudokuManagerTests {

	private val sudokuTypeRepo : IRepo<SudokuType> = SudokuTypeRepo4Tests();

    private val sudokuRepo : IRepo<Sudoku> = object : IRepo<Sudoku> {

        override fun create() : Sudoku {
            val sudoku = mockk<Sudoku>()
            every { sudoku.id }.returns(5)
            return sudoku;
        }

        private fun getSudokuFile(id : Int) : File {
            val classLoader = javaClass.classLoader
            return File(classLoader.getResource("persistence/SudokuTypes/standard9x9/sudoku_5.xml").file);
        }
        private val helper : XmlHelper = XmlHelper();

        override fun read(id : Int) : Sudoku {
            val obj : SudokuBE = SudokuBE();
            val file : File = getSudokuFile(id);

            try {
                obj.fillFromXml(helper.loadXml(file)!!, sudokuTypeRepo);
            } catch (e : IOException) {
                throw IllegalArgumentException("Something went wrong when reading xml file ${file.name}", e);
            } catch (e : IllegalArgumentException) {
                throw IllegalArgumentException("Something went wrong when filling obj from xml ", e);
            }
            return SudokuMapper.fromBE(obj);
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
