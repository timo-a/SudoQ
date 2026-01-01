package de.sudoq.model.sudoku;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.NotImplementedException;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import de.sudoq.model.TestWithInitCleanforSingletons;
import de.sudoq.model.Utility;
import de.sudoq.model.persistence.IRepo;
import de.sudoq.model.persistence.xml.sudoku.ISudokuRepoProvider;
import de.sudoq.model.solverGenerator.utils.SudokuTypeRepo4Tests;
import de.sudoq.model.sudoku.sudokuTypes.SudokuType;
import de.sudoq.model.sudoku.complexity.Complexity;
import de.sudoq.model.sudoku.sudokuTypes.SudokuTypes;
import de.sudoq.persistence.XmlHelper;
import de.sudoq.persistence.sudoku.SudokuBE;
import de.sudoq.persistence.sudoku.SudokuMapper;
import de.sudoq.persistence.sudoku.SudokuRepo;

public class SudokuManagerTests extends TestWithInitCleanforSingletons {

	private final IRepo<SudokuType> sudokuTypeRepo = new SudokuTypeRepo4Tests();

    private final IRepo<Sudoku> sudokuRepo = new IRepo<Sudoku>() {
        @Override
        public Sudoku create() {
            Sudoku sudoku = new Sudoku();
            sudoku.setId(5);
            return sudoku;
        }

        private File getSudokuFile(int id) {
            ClassLoader classLoader = SudokuManagerTests.class.getClassLoader();
            return new File(classLoader.getResource("persistence/SudokuTypes/standard9x9/sudoku_5.xml").getFile());
        }
        private XmlHelper helper = new XmlHelper();

        @Override
        public Sudoku read(int id) {
            SudokuBE obj = new SudokuBE();
            File file = getSudokuFile(id);

            try {
                obj.fillFromXml(helper.loadXml(file), sudokuTypeRepo);
            } catch (IOException e) {
                throw new IllegalArgumentException("Something went wrong when reading xml file %s", e);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Something went wrong when filling obj from xml ", e);
            }
            return SudokuMapper.INSTANCE.fromBE(obj);
        }

        @Override
        public Sudoku update(Sudoku cells) {
            return cells;
        }

        @Override
        public void delete(int id) {  }

        @Override
        public @NotNull List<Integer> ids() {
            return Arrays.asList(5);
        }
    };

    ISudokuRepoProvider sudokuRepoProvider = new ISudokuRepoProvider() {

        @Override
        @NotNull
        public IRepo<Sudoku> getRepo(@NotNull SudokuTypes type, @NotNull Complexity complexity) {
            assertEquals(SudokuTypes.standard9x9, type);
            assertEquals(Complexity.infernal, complexity);
            return sudokuRepo;
        }

        @Override
        @NotNull
        public IRepo<Sudoku> getRepo(@NotNull Sudoku sudoku) {
            return sudokuRepo;
        }
    };

    @Test(timeout = 12000) // threw an exception and ran forever in the past -> timeout
	public void test() {
		//assertEquals(21, FileManager.getSudokuCountOf(SudokuTypes.standard9x9, Complexity.infernal));
        Sudoku s = new SudokuManager(sudokuTypeRepo, sudokuRepoProvider)
				.getNewSudoku(SudokuTypes.standard9x9, Complexity.infernal);
		for (int i = 0; i < 10; i++) {
			s.increaseTransformCount();
		}
		SudokuManager sm = new SudokuManager(sudokuTypeRepo, sudokuRepoProvider) {
			public void generationFinished(Sudoku sudoku) {
				synchronized (SudokuManagerTests.this) {
					super.generationFinished(sudoku);
					SudokuManagerTests.this.notifyAll();
				}
			}
		};
		sm.usedSudoku(s);
		synchronized (this) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		//assertEquals(21, FileManager.getSudokuCountOf(SudokuTypes.standard9x9, Complexity.infernal));
	}

}
