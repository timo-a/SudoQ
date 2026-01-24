package de.sudoq.model.game

import de.sudoq.model.actionTree.NoteActionFactory
import de.sudoq.model.actionTree.SolveActionFactory
import de.sudoq.model.solverGenerator.Generator
import de.sudoq.model.solverGenerator.GeneratorCallback
import de.sudoq.model.solverGenerator.solution.Solution
import de.sudoq.model.solverGenerator.utils.PrettySudokuRepo2
import de.sudoq.model.solverGenerator.utils.SudokuTypeRepo4Tests
import de.sudoq.model.sudoku.Cell
import de.sudoq.model.sudoku.Position
import de.sudoq.model.sudoku.PositionMap
import de.sudoq.model.sudoku.Sudoku
import de.sudoq.model.sudoku.SudokuBuilder
import de.sudoq.model.sudoku.complexity.Complexity
import de.sudoq.model.sudoku.sudokuTypes.SudokuTypeProvider.getSudokuType
import de.sudoq.model.sudoku.sudokuTypes.SudokuTypes
import de.sudoq.model.sudoku.sudokuTypes.TypeBuilder
import org.amshove.kluent.invoking
import org.amshove.kluent.`should be`
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should not be`
import org.amshove.kluent.`should not be equal to`
import org.amshove.kluent.`should throw`
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import java.nio.file.Paths
import java.time.Duration
import java.time.temporal.ChronoUnit
import java.util.concurrent.CompletableFuture

internal class GameTests {
    private val sudokuRepo = PrettySudokuRepo2(sudokuTypeRepo)

    /**
     * This is the former @BeforeAll init method. it takes suspiciously long to generate a sudoku todo investigate!
     */
    @Test
    @Throws(Exception::class)
    fun debugGeneration() {
        TypeBuilder.get99() //just to force initialization of filemanager

        val future = CompletableFuture<Sudoku?>()

        val gc: GeneratorCallback = object : GeneratorCallback {
            override fun generationFinished(sudoku: Sudoku) {
                future.complete(sudoku)
            }

            override fun generationFinished(sudoku: Sudoku, sl: List<Solution>) {
                future.complete(sudoku)
            }
        }
        Generator(sudokuTypeRepo).generate(SudokuTypes.standard9x9, Complexity.easy, gc)
        Assertions.assertTimeoutPreemptively(Duration.of(60, ChronoUnit.SECONDS)) {
            future.get()
        }
        val sudoku = future.get()
        sudoku `should not be` null
    }

    @Test
    fun instantiation() {
        val game = Game(2, SudokuBuilder(SudokuTypes.standard9x9, sudokuTypeRepo).createSudoku())
        game.id `should be equal to` 2
        game.stateHandler `should not be` null
        game.sudoku!!.getCell(Position[8, 8])!!.currentValue `should be equal to` Cell.EMPTYVAL
        game.sudoku!!.getCell(Position[10, 2]) `should be` null
        game.assistancesCost `should be equal to` 0
    }

    @Test
    fun gameInteraction() {
        val game = Game(2, SudokuBuilder(SudokuTypes.standard9x9, sudokuTypeRepo).createSudoku())

        val pos = Position[1, 1]
        val start = game.currentState
        val f = game.sudoku!!.getCell(pos)
        game.addAndExecute(SolveActionFactory().createAction(3, f!!)) //setze 3
        game.addAndExecute(SolveActionFactory().createAction(4, f)) //setze 4
        game.isMarked(game.currentState) `should be` false
        game.addAndExecute(SolveActionFactory().createAction(5, f)) //setze 5
        game.sudoku!!.getCell(pos)!!.currentValue `should be equal to` 5
        game.markCurrentState()
        game.isMarked(game.currentState) `should be` true

        game.goToState(start)
        f.currentValue `should be equal to` Cell.EMPTYVAL
        game.isFinished() `should be` false

        game.redo()
        game.redo()
        f.currentValue `should be equal to` 5 //schlägt fehl
        game.undo()
        f.currentValue `should be equal to` Cell.EMPTYVAL
        game.redo()
        game.checkSudoku() `should be` false

        game.addTime(23)
        game.time `should be equal to` 23
    }

    @Test
    fun equals() {
        val game = Game(2, SudokuBuilder(SudokuTypes.standard9x9, sudokuTypeRepo).createSudoku())
        game `should be equal to` game
        val game2 = Game(3, SudokuBuilder(SudokuTypes.standard9x9, sudokuTypeRepo).createSudoku())
        game2 `should not be equal to` game

        val pos = Position[1, 1]
        val start = game.currentState

        game.addAndExecute(SolveActionFactory().createAction(3, game.sudoku!!.getCell(pos)!!))
        game.addAndExecute(SolveActionFactory().createAction(4, game.sudoku!!.getCell(pos)!!))
        game.addAndExecute(SolveActionFactory().createAction(5, game.sudoku!!.getCell(pos)!!))
        game.markCurrentState()

        game.goToState(start)

        game.redo()
        game.redo()
        game.undo()

        game2 `should not be equal to` game
        game `should be equal to` game

        game `should not be equal to` Any()
    }

    @Test
    fun gameXML() {
        val s = SudokuBuilder(SudokuTypes.standard9x9, sudokuTypeRepo).createSudoku()
        s.id = 5
        val game = Game(2, s)

        val pos = Position[1, 1]
        val start = game.currentState

        game.addAndExecute(SolveActionFactory().createAction(3, game.sudoku!!.getCell(pos)!!))
        game.addAndExecute(SolveActionFactory().createAction(4, game.sudoku!!.getCell(pos)!!))
        game.addAndExecute(SolveActionFactory().createAction(5, game.sudoku!!.getCell(pos)!!))
        game.addAndExecute(NoteActionFactory().createAction(2, game.sudoku!!.getCell(pos)!!))
        game.sudoku!!.getCell(pos)!!.currentValue `should be equal to` 5
        game.markCurrentState()
        game.isMarked(game.currentState) `should be` true

        game.goToState(start)

        game.redo()
        game.redo()
        game.undo()
    }

    // Regression Test for Issue-89
    @Test
    fun finishedAttributeConsistency() {
        val sb = SudokuBuilder(SudokuTypes.standard9x9, sudokuTypeRepo)
        for (i in 0..8) {
            for (j in 0..8) {
                sb.addSolution(Position[i, j], 1)
            }
        }
        val game = Game(1, sb.createSudoku())
        game.solveAll() `should be` true
        game.isFinished() `should be` true
    }

    @Test
    fun assistanceSetting() {
        val game = Game(2, SudokuBuilder(SudokuTypes.standard9x9, sudokuTypeRepo).createSudoku())

        game.setAssistances(object : GameSettings() {
            override fun getAssistance(assistance: Assistances): Boolean = true
        })

        Assistances.entries
            .map(game::isAssistanceAvailable)
            .forEach(Assertions::assertTrue)
    }

    @Test
    fun help() {
        class SudokuMock : Sudoku(
            getSudokuType(SudokuTypes.standard9x9, sudokuTypeRepo), PositionMap(Position[9, 9]),
            PositionMap(Position[9, 9])
        ) {
            override var isFinished: Boolean = false
                private set
            private var errors = false

            public override fun hasErrors(): Boolean {
                return errors
            }

            fun toogleErrors() {
                errors = !errors
            }

            fun toogleFinished() {
                this.isFinished = !this.isFinished
            }
        }

        val sudoku = SudokuMock()
        val game = Game(2, sudoku)

        game.solveCell(null) `should be` false
        game.solveCell(Cell(true, -1, 3, 9)) `should be` false

        game.addAndExecute(SolveActionFactory().createAction(2, game.sudoku!!.getCell(Position[0, 0])!!))
        sudoku.toogleErrors()
        game.checkSudoku() `should be` false
        game.currentState.isMistake `should be` true
        game.solveCell() `should be` false
        game.solveCell(game.sudoku!!.getCell(Position[0, 0])) `should be` false
        game.solveAll() `should be` false

        sudoku.toogleErrors()
        sudoku.toogleFinished()
        game.addAndExecute(
            SolveActionFactory().createAction(
                Cell.EMPTYVAL,
                game.sudoku!!.getCell(Position[0, 0])!!
            )
        )
        game.addAndExecute(SolveActionFactory().createAction(1, game.sudoku!!.getCell(Position[0, 0])!!))
        game.sudoku!!.getCell(Position[0, 0])!!.currentValue `should be equal to` Cell.EMPTYVAL
    }

    @Test
    fun noteAdjustment() {
        val game = Game(2, SudokuBuilder(SudokuTypes.standard9x9, sudokuTypeRepo).createSudoku())
        val `as` = GameSettings()
        `as`.setAssistance(Assistances.autoAdjustNotes)
        game.setAssistances(`as`)

        game.addAndExecute(NoteActionFactory().createAction(2, game.sudoku!!.getCell(Position[1, 0])!!))
        game.addAndExecute(NoteActionFactory().createAction(3, game.sudoku!!.getCell(Position[1, 0])!!))
        game.addAndExecute(NoteActionFactory().createAction(2, game.sudoku!!.getCell(Position[0, 1])!!))
        game.addAndExecute(NoteActionFactory().createAction(3, game.sudoku!!.getCell(Position[0, 1])!!))

        game.sudoku!!.getCell(Position[0, 1])!!.isNoteSet(2) `should be` true
        game.sudoku!!.getCell(Position[1, 0])!!.isNoteSet(2) `should be` true
        game.sudoku!!.getCell(Position[0, 1])!!.isNoteSet(3) `should be` true
        game.sudoku!!.getCell(Position[1, 0])!!.isNoteSet(3) `should be` true

        game.addAndExecute(SolveActionFactory().createAction(2, game.sudoku!!.getCell(Position[0, 0])!!))

        game.sudoku!!.getCell(Position[0, 1])!!.isNoteSet(3) `should be` true
        game.sudoku!!.getCell(Position[1, 0])!!.isNoteSet(3) `should be` true
        game.sudoku!!.getCell(Position[0, 1])!!.isNoteSet(2) `should be` false
        game.sudoku!!.getCell(Position[1, 0])!!.isNoteSet(2) `should be` false
    }

    @ParameterizedTest
    @CsvSource(value = ["easy,2430", "medium,7290", "difficult,21870", "infernal,65610"])
    fun score(c: Complexity, score: Int) {
        val sudoku = Sudoku(TypeBuilder.get99())
        sudoku.complexity = c
        val game = Game(0, sudoku)
        game.addTime(60)
        game.score `should be equal to` score
    }

    @Test
    fun `score for arbitrary should not be possible`() {
        val sudoku = Sudoku(TypeBuilder.get99())
        sudoku.complexity = Complexity.arbitrary
        val game = Game(0, sudoku)
        game.addTime(60)
        invoking { game.score } `should throw` IllegalStateException::class
    }

    @Test
    @Synchronized
    fun solve() {
        val sudokuPath = Paths.get("sudokus/9_lockedCandidates_1.pretty")
        val sudoku = sudokuRepo.read(sudokuPath, Complexity.easy)
        sudoku `should not be` null
        val game = Game(1, sudoku)
        val unsolvedCells = ArrayList<Cell>()
        for (f in sudoku) {
            if (f.isEditable) {
                f.clearCurrentValue()
                unsolvedCells.add(f)
            }
        }
        game.checkSudoku() `should be` true
        game.solveCell() `should be` true
        var hasNewSolved = false
        for (f in sudoku) {
            if (f.isEditable && f.isSolvedCorrect) {
                if (hasNewSolved) {
                    Assertions.fail<Any>("Solve field solved more than one field")
                } else {
                    hasNewSolved = true
                    unsolvedCells.remove(f)
                }
            }
        }
        hasNewSolved `should be` true
        game.checkSudoku() `should be` true
        game.isFinished() `should be` false
        game.solveCell(unsolvedCells[0]) `should be` true
        unsolvedCells[0].isSolvedCorrect `should be` true
        unsolvedCells.removeAt(0)
        game.isFinished() `should be` false
        game.solveAll() `should be` true
        for (f in unsolvedCells) {
            f.isSolvedCorrect `should be` true
        }
        game.isFinished() `should be` true
        game.isFinished() `should be` true
    }

    @Test
    @Synchronized
    fun goToLastCorrectState() {
        val sudokuPath = Paths.get("sudokus/9_lockedCandidates_1.pretty")
        val sudoku = sudokuRepo.read(sudokuPath, Complexity.easy)
        sudoku `should not be` null
        val game = Game(1, sudoku)

        var unsolvedCell: Cell? = null
        for (f in sudoku) {
            if (f.isEditable) {
                f.clearCurrentValue()
                if (unsolvedCell == null) unsolvedCell = f
            }
        }

        var oldAssistanceCost = game.assistancesCost
        game.goToLastCorrectState()
        game.assistancesCost `should be equal to` oldAssistanceCost + 3
        oldAssistanceCost = game.assistancesCost

        if (unsolvedCell!!.solution < 8) {
            game.addAndExecute(SolveActionFactory().createAction(8, unsolvedCell))
        } else {
            game.addAndExecute(SolveActionFactory().createAction(7, unsolvedCell))
        }

        game.goToLastCorrectState()
        game.assistancesCost `should be equal to` oldAssistanceCost + 3
        oldAssistanceCost = game.assistancesCost
        unsolvedCell.currentValue `should be equal to` Cell.EMPTYVAL
        game.currentState.isCorrect `should be` true
    }

    // Regression Test for Issue-90
    @Test
    fun autoAdjustNotesForAutomaticSolving() {
        val sb = SudokuBuilder(SudokuTypes.standard9x9, sudokuTypeRepo)
        for (i in 0..8) {
            for (j in 0..8) {
                sb.addSolution(Position[i, j], 1)
            }
        }
        val game = Game(2, sb.createSudoku())
        val `as` = GameSettings()
        `as`.setAssistance(Assistances.autoAdjustNotes)
        game.setAssistances(`as`)

        for (pos in game.sudoku!!.sudokuType.validPositions) {
            game.addAndExecute(NoteActionFactory().createAction(1, game.sudoku!!.getCell(pos)!!))
            game.sudoku!!.getCell(pos)!!.isNoteSet(1) `should be` true
        }

        game.solveCell() `should be` true
        var done = false
        var x = -1
        var y = -1
        run {
            var i = 0
            while (i < 9 && !done) {
                var j = 0
                while (j < 9 && !done) {
                    if (game.sudoku!!.getCell(Position[i, j])!!.currentValue == 1) {
                        done = true
                        x = i
                        y = j
                    }
                    j++
                }
                i++
            }
        }
        done `should be` true

        for (i in 0..8) {
            game.sudoku!!.getCell(Position[x, i])!!.isNoteSet(1) `should be` false
            game.sudoku!!.getCell(Position[i, y])!!.isNoteSet(1) `should be` false
        }
        for (i in 0..2) {
            for (j in 0..2) {
                game.sudoku!!.getCell(
                    Position[
                        (x - (x % 3) + i),
                        (y - (y % 3) + j)
                    ]
                )!!.isNoteSet(1) `should be` false
            }
        }

        x = (x + 3) % 9
        y = (y + 3) % 9
        game.solveCell(game.sudoku!!.getCell(Position[x, y]))

        for (i in 0..8) {
            game.sudoku!!.getCell(Position[x, i])!!.isNoteSet(1) `should be` false
            game.sudoku!!.getCell(Position[i, y])!!.isNoteSet(1) `should be` false
        }
        for (i in 0..2) {
            for (j in 0..2) {
                game.sudoku!!.getCell(
                    Position[
                        (x - (x % 3) + i),
                        (y - (y % 3) + j)
                    ]
                )!!.isNoteSet(1) `should be` false
            }
        }
    }

    companion object {
        private val sudokuTypeRepo = SudokuTypeRepo4Tests()
    }
}
