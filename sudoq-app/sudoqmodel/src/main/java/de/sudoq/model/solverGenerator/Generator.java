/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.model.solverGenerator;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

import de.sudoq.model.solverGenerator.solution.Solution;
import de.sudoq.model.solverGenerator.solution.SolveDerivation;
import de.sudoq.model.solverGenerator.solver.ComplexityRelation;
import de.sudoq.model.solverGenerator.solver.Solver;
import de.sudoq.model.solverGenerator.solver.SolverSudoku;
import de.sudoq.model.solverGenerator.transformations.Transformer;
import de.sudoq.model.sudoku.Constraint;
import de.sudoq.model.sudoku.Field;
import de.sudoq.model.sudoku.Position;
import de.sudoq.model.sudoku.PositionMap;
import de.sudoq.model.sudoku.Sudoku;
import de.sudoq.model.sudoku.SudokuBuilder;
import de.sudoq.model.sudoku.complexity.Complexity;
import de.sudoq.model.sudoku.complexity.ComplexityConstraint;
import de.sudoq.model.sudoku.sudokuTypes.SudokuType;
import de.sudoq.model.sudoku.sudokuTypes.SudokuTypes;

/**
 * Diese Klasse stellt verschiedene Methoden zum Erstellen eines validen, neuen
 * Sudokus zur Verfügung. Dazu gibt es sowohl die Möglichkeit ein gänzlich neues
 * Sudoku mit einer spezifizierten Schwierigkeit erzeugen zu lassen, als auch
 * ein vorhandenes Sudoku durch Transformationen in ein Äquivalentes überführen
 * zu lassen.
 * 
 * @see Sudoku
 * @see Solver
 */
public class Generator {
	/** Attributes */

	private Random random;

	//@deprecated
	private Thread lastThread;
	/** Constructors */

	/**
	 * Initiiert ein neues Generator-Objekt.
	 */
	public Generator() {
		random = new Random(0);
	}//Todo remove 0 again

	/** Methods */

	/**
	 * creates a sudoku of type @param{type} and difficulty @param{complexity} and appends it
	 * (together with the callback object) to the queue of sudokus being generated.
	 * If the queue is empty and there is no sudoku being generated at the moment,
	 * generation of the new sudoku beginns immediately.
	 * Otherwise it begins after all sudokus in the queue are generated.
	 * 
	 * Ist das spezifizierte SudokuType-Objekt oder das GeneratorCallback-Objekt
	 * null, oder hat das Complexity-Argument einen ungültigen Wert, so wird
	 * false zurückgegeben. Ansonsten ist der Rückgabewert true.
	 * 
	 * @param type
	 *            Der SudokuTypes-Enum Wert, aus welchem ein Sudoku erstellt und
	 *            generiert werden soll
	 * @param complexity
	 *            Die Komplexität des zu erstellenden Sudokus
	 * @param callbackObject
	 *            Das Objekt, dessen Callback-Methode aufgerufen werden soll,
	 *            sobald der Generator fertig ist
	 * @return true, falls ein leeres Sudoku erzeugt und der Warteschlange
	 *         hinzugefügt werden konnte, false andernfalls
	 */
	public boolean generate(SudokuTypes type, Complexity complexity, GeneratorCallback callbackObject) {
		if (type == null || complexity == null || callbackObject == null)
			return false;

		// Create sudoku
		Sudoku sudoku = new SudokuBuilder(type).createSudoku();
		sudoku.setComplexity(complexity);

		Thread t = new Thread(new GenerationAlgo(sudoku, callbackObject, random));
		t.start();
		lastThread = t;

		// Initiate new random object
		random = new Random();

		return true;
	}

	/*
	* This method as well as the variable are NOT to called by the app.
	* This is a hack, so projects on PC can call `generate` and wait for the thread to end, for which they need the reference.
	* better would be for `generate` to return the thread or null, but right now i need a quick solution.
	* @deprecated
	* */
	public Thread getLastThread(){
		return lastThread;
	}

	/**
	 * NUR ZU DEBUG-ZWECKEN: Setzt das Random-Objekt dieses Sudokus, um einen
	 * reproduzierbaren, deterministischen Ablauf des Generator zu provozieren.
	 * Das Random-Objekt muss vor jedem Aufruf der generate-Methode neu gesetzt
	 * werden.
	 * 
	 * @param rnd
	 *            Das zu setzende random Objekt.
	 */
	void setRandom(Random rnd) {
		this.random = rnd;
	}

	/**
	 * Abstrakte Klasse kapselt gemeinsamkeiten von {@link SudokuGenerationStandardType} und {@link SudokuGeneration}
	 * Grund: wir hatten ursprünglich eine extra methode um 9x9 und 16x16 sudokus zu generieren.
	 * Um diese Methode zu debuggen, habe ich alle Gemeinsamkeiten ausgelagert.
	 * @author timo
	 *
	 */
	private abstract class SudokuGenerationTopClass implements Runnable{
		
		/**
		 * Das Sudoku auf welchem die Generierung ausgeführt wird
		 */
		protected Sudoku sudoku;

		/**
		 * Das Zufallsobjekt für den Generator
		 */
		protected Random random;
	
		/**
		 * Der Solver, der für Validierungsvorgänge genutzt wird
		 */
		protected Solver solver;
		
		/**
		 * Das Objekt, auf dem nach Abschluss der Generierung die
		 * Callback-Methode aufgerufen wird
		 */
		protected GeneratorCallback callbackObject;

		/**
		 * List of currently defined(occupied) Fields.
		 * If we gave the current sudoku to the user tey wouldn't have to solve these fields
		 * as they'd already be filled in.
		 */
		protected List<Position> definedFields;

		/**
		 * Die noch freien, also nicht belegten Felder des Sudokus
		 */
		protected List<Position> freeFields;

		/**
		 * Das gelöste Sudoku
		 */
		protected Sudoku solvedSudoku;

		public SudokuGenerationTopClass(Sudoku sudoku, GeneratorCallback callbackObject, Random random) {
			this.sudoku = sudoku;
			this.callbackObject = callbackObject;
			this.solver = new Solver(sudoku);
			this.freeFields = new ArrayList<Position>();
			this.definedFields = new ArrayList<Position>();
			this.random = random;

		}
	}
	
	/**
	 * Bietet die Möglichkeit Sudokus abgeleitet vom Typ TypeStandard(deprecated) zu
	 * genrieren. Für diese ist der Algorithmus wesentlich schneller als der von
	 * {@link SudokuGeneration}. Die Klasse implementiert das {@link Runnable}
	 * interface und kann daher in einem eigenen Thread ausgeführt werden.
	 * @deprecated keept until debugging complete, but not to be used
	 */
	private class SudokuGenerationStandardType extends SudokuGenerationTopClass {

		/**
		 * Definierte Felder
		 */
		private int definedOnes;

		/**
		 * Instanziiert ein neues Generierungsobjekt für das spezifizierte
		 * Sudoku. Da die Klasse privat ist wird keine Überprüfung der
		 * Eingabeparameter durchgeführt.
		 * 
		 * @param sudoku
		 *            Das Sudoku, auf dem die Generierung ausgeführt werden soll
		 * @param callbackObject
		 *            Das Objekt, auf dem die Callback-Methode nach Abschluss
		 *            der Generierung aufgerufen werden soll
		 * @param random
		 *            Das Zufallsobjekt zur Erzeugung des Sudokus
		 */
		public SudokuGenerationStandardType(Sudoku sudoku, GeneratorCallback callbackObject, Random random) {
			super(sudoku, callbackObject, random);
		}

		/**
		 * Die Methode, die die tatsächliche Generierung eines Sudokus mit der
		 * gewünschten Komplexität generiert.
		 */
		public void run() {
			int sudokuSizeX = sudoku.getSudokuType().getSize().getX();
			int sudokuSizeY = sudoku.getSudokuType().getSize().getY();

			// Reset, if it takes too long
			//Create template sudoku of defined type and transform it
			//TODO make a better template
			SudokuBuilder sub = new SudokuBuilder(sudoku.getSudokuType());
			int sqrtSymbolNumber = (int) Math.sqrt(sudoku.getSudokuType().getNumberOfSymbols());
			for (int x = 0; x < sudokuSizeX; x++) {
				for (int y = 0; y < sudokuSizeY; y++) {
					int calculatedsolution = ((y % sqrtSymbolNumber) * sqrtSymbolNumber + x + (y / sqrtSymbolNumber))
					                         % (sqrtSymbolNumber * sqrtSymbolNumber);
					sub.addSolution(Position.get(x, y),calculatedsolution);
				}
			}
			solvedSudoku = sub.createSudoku();
			Transformer.transform(solvedSudoku);

			/* extract the solutions from solvedSudoku and
			 * map the positions to their new coordinates */
			PositionMap<Integer> solutionMap = new PositionMap<Integer>(sudoku.getSudokuType().getSize());
			
			for (int x = 0; x < sudokuSizeX; x++) {
				for (int y = 0; y < sudokuSizeY; y++) {
					solutionMap.put(Position.get(x, y), solvedSudoku.getField(Position.get(x, y)).getSolution());
				}
			}

						
			// prefill every field in the sudoku being generated with template solutions
			// filled as the user would fill in
			for (int x = 0; x < sudokuSizeX; x++) {
				for (int y = 0; y < sudokuSizeY; y++) {
					int solutionFromMap = solutionMap.get(Position.get(x, y));
					sudoku.getField(Position.get(x, y)).setCurrentValue(solutionFromMap, false);
					this.definedFields.add(Position.get(x, y));
				}
			}

			/* For every Constraint
			 *   if there is no position empty
			 *       set one random position empty    */
			Iterable<Constraint> constraints = sudoku.getSudokuType();
			ArrayList<Position> positions;
			boolean emptyOne;
			for (Constraint c:constraints){
				positions = c.getPositions();
				emptyOne = false;
				for (Position p:positions) {
					if (sudoku.getField(p).isNotSolved()) {
						emptyOne = true;
						break;
					}
				}
				if (!emptyOne) {
					int nr = random.nextInt(positions.size());
					Position randomPosition = positions.get(nr);
					
					sudoku.getField(randomPosition).setCurrentValue(Field.EMPTYVAL, false);
					definedFields.remove(randomPosition);
					freeFields.add(randomPosition);
					definedOnes++;
				}
			}

			/* 
			 * call magic
			 * TODO understand & document
			 * */
			ComplexityConstraint constr = sudoku.getSudokuType().buildComplexityConstraint(sudoku.getComplexity());

			int nr = random.nextInt(definedFields.size());
			int counter = definedFields.size();

			SolverSudoku solverSudoku = (SolverSudoku) solver.getSolverSudoku();
			while (counter >= 0 && definedFields.size() > constr.getAverageFields()) {
				counter--;
				Position currentFieldPos = definedFields.get(nr);
				sudoku.getField(currentFieldPos).setCurrentValue(Field.EMPTYVAL, false);
				solverSudoku.resetCandidates();
				if (solverSudoku.getCurrentCandidates(currentFieldPos).cardinality() != 1) {                    //if currentField has not 1 possibilitie
					sudoku.getField(currentFieldPos).setCurrentValue( solutionMap.get(currentFieldPos), false); //     fill with solution
					nr = (nr + 1) % definedFields.size();                                                       //     advance to next pos
				} else {																						//else
					freeFields.add(definedFields.remove(nr));                                                   //     set one field free
					definedOnes++;                                                                              //     increase defined ones TODO why?
					if (nr >= definedFields.size()) //if nr is out of bounds because of the removal, set it to 0, the next logical position. 
					                                //otherwise nr is already at the next pos(because the original element is gone)
						nr = 0;
				}
			}

			int allocationFactor = sudokuSizeX * sudokuSizeY / 20;

			ComplexityRelation rel = ComplexityRelation.INVALID;
			while (rel != ComplexityRelation.CONSTRAINT_SATURATION) {
			
				rel = solver.validate(solutionMap);

				switch (rel) {
				case MUCH_TOO_EASY:
					//remove more
					for (int i = 0; i < allocationFactor / 2; i++)
						removeDefinedField();
					break;
				case TOO_EASY:
					//remove one
					removeDefinedField();
					break;
				case INVALID:
					//do nothen TODO better ideas?
				case MUCH_TOO_DIFFICULT:
					//add more fields
					for (int i = 0; i < allocationFactor; i++)
						addDefinedField();
					break;
				case TOO_DIFFICULT:
					//add one
					addDefinedField();
					break;
				}
			}

			/* fill the real sudoku:
			 * 
			 * for every pos
			 *     pass solution to the builder
			 *     if already filled:
			 *          set fixed, so user can't alter*/
			SudokuBuilder sudokuBuilder = new SudokuBuilder(sudoku.getSudokuType());
			Position currentPos;
			for (int x = 0; x < sudokuSizeX; x++) {
				for (int y = 0; y < sudokuSizeY; y++) {
					currentPos = Position.get(x, y);
					int value = solvedSudoku.getField(currentPos).getSolution();
					sudokuBuilder.addSolution(currentPos, value);
					if (!sudoku.getField(currentPos).isNotSolved())
						sudokuBuilder.setFixed(currentPos);
				}
			}
			Sudoku res = sudokuBuilder.createSudoku();           //the final sudoku
			res.setComplexity(sudoku.getComplexity());

			// Call the callback
			callbackObject.generationFinished(res);
		}

		
		private boolean addDefinedField() {
			if (freeFields.isEmpty()) return false;
			else if (freeFields.size() == definedOnes) {
				definedOnes--;
			}
			Position p = freeFields.remove(random.nextInt(freeFields.size() - definedOnes) + definedOnes);
			sudoku.getField(p).setCurrentValue(solvedSudoku.getField(p).getSolution(), false);
			definedFields.add(p);
			return true;

		}



		/**
		 * Entfernt eines der definierten Felder.
		 * 
		 * @return True wenn es geklappt hat, False wenn es schon leer war
		 */
		private boolean removeDefinedField() {
			if (definedFields.isEmpty())
				return false;
			Position p;
			p = definedFields.remove(random.nextInt(definedFields.size()));
			sudoku.getField(p).setCurrentValue(Field.EMPTYVAL, false);
			freeFields.add(p);
			return true;

		}

	}

	/**
	 * Bietet die Möglichkeit Sudokus zu generieren.
	 * Die Klasse implementiert das {@link Runnable} interface
	 * und kann daher in einem eigenen Thread ausgeführt werden.
	 */
	public class SudokuGeneration extends SudokuGenerationTopClass {


		/**
		 * Die Anzahl der Felder, die fest zu definieren ist
		 */
		private int fieldsToDefine;


		/**
		 * Anzahl aktuell definierter Felder
		 */
		//private int currentFieldsDefined;

		/**
		 * ComplexityConstraint für ein Sudoku des definierten
		 * Schwierigkeitsgrades
		 */
		private ComplexityConstraint desiredComplexityConstraint;

		/**
		 * Instanziiert ein neues Generierungsobjekt für das spezifizierte
		 * Sudoku. Da die Klasse privat ist wird keine Überprüfung der
		 * Eingabeparameter durchgeführt.
		 * 
		 * @param sudoku
		 *            Das Sudoku, auf dem die Generierung ausgeführt werden soll
		 * @param callbackObject
		 *            Das Objekt, auf dem die Callback-Methode nach Abschluss
		 *            der Generierung aufgerufen werden soll
		 * @param random
		 *            Das Zufallsobjekt zur Erzeugung des Sudokus
		 */
		public SudokuGeneration(Sudoku sudoku, GeneratorCallback callbackObject, Random random) {
			super(sudoku, callbackObject, random);

			this.desiredComplexityConstraint = sudoku.getSudokuType().buildComplexityConstraint(sudoku.getComplexity());

			freeFields.addAll(getPositions(sudoku));//fills the currenlty empty list freefields as no field is defined=occupied

		}

		/**
		 * Die Methode, die die tatsächliche Generierung eines Sudokus mit der
		 * gewünschten Komplexität generiert.
		 */
		public void run() {
			/* 1. Finde Totalbelegung */
			//determine ideal number of prefilled fields
			fieldsToDefine = getNumberOfFieldsToDefine(sudoku.getSudokuType(), desiredComplexityConstraint);

			//A mapping from position to solution
			PositionMap<Integer> solution = new PositionMap<>(this.sudoku.getSudokuType().getSize());
			int iteration=0;
			System.out.println("Fields to define: "+fieldsToDefine);

			//define fields
			for (int i = 0; i < fieldsToDefine; i++) {
				Position p = addDefinedField();
			}

			/* until a solution is found, remove 5 random fields and add new ones */
			while(!solver.solveAll(false, false)) {
				System.out.println("Iteration: "+(iteration++)+", defined Fields: "+definedFields.size());
				// Remove some fields, because sudoku could not be validated
				removeDefinedFields(5);

				// Define average number of fields
				while (definedFields.size() < fieldsToDefine)
					if (addDefinedField() == null) //try to add field, if null returned i.e. nospace / invalid
						removeDefinedFields(5); //remove 5 fields
			}

			/* we found a solution i.e. a combination of nxn numbers that fulfill all constraints */

			/* not sure what's happening why tmp-save complexity? is it ever read? maybe in solveall?
			   maybe this is from previous debugging, wanting to see if it's invalid here already

			   solver.validate is definitely needed

			   but the complexity is from the superclass `Sudoku`, SolverSudoku has its own `complexityValue`...
			*/
			Complexity saveCompl = solver.getSolverSudoku().getComplexity();
			solver.getSolverSudoku().setComplexity(Complexity.arbitrary);
			solver.validate(solution); //solution is filled w/ correct solution
			solver.getSolverSudoku().setComplexity(saveCompl);

			/* We have (validated) filled `solution` with the right values */

			// Create the sudoku template generated before
			SudokuBuilder sub = new SudokuBuilder(sudoku.getSudokuType());
			for(Position p : getPositions(sudoku))
				sub.addSolution(p, solution.get(p));//fill in all solutions

			solvedSudoku = sub.createSudoku();

			while (!this.freeFields.isEmpty()) {
				this.definedFields.add(this.freeFields.remove(0));
			}//now all defined

			// Fill the sudoku being generated with template solutions
			//TODO simplify: iterate over fields/positions

			for (Position pos : getPositions(sudoku)) {
				Field fSudoku =       sudoku.getField(pos);
				Field fSolved = solvedSudoku.getField(pos);
				fSudoku.setCurrentValue(fSolved.getSolution(), false);
			}

			int reallocationAmount = 2; //getReallocationAmount(sudoku.getSudokuType(), 0.05);

			ComplexityRelation rel = ComplexityRelation.INVALID;
			while (rel != ComplexityRelation.CONSTRAINT_SATURATION) {
				if(definedFields.size() == 43)
					System.out.println("letzte too easy");
				if(definedFields.size() == 41)
					System.out.println("gleich wirds invalid");
				rel = solver.validate(null);
                System.out.print("Generator.run +/- loop. validate says " + rel);
				switch(rel){
					case MUCH_TOO_EASY: removeDefinedFields(reallocationAmount);
								   break;
					case TOO_EASY: removeDefinedFields(1);
								   break;
					case INVALID:  //freeFields ARE empty ?! hence infinite loop
					case TOO_DIFFICULT:
					case MUCH_TOO_DIFFICULT:
						for (int i = 0; i < Math.min(reallocationAmount, freeFields.size()); i++) {

							Position p = freeFields.remove(random.nextInt(freeFields.size())); //used to be 0, random just in case
							Field fSudoku = sudoku.getField(p);
							Field fSolved = solvedSudoku.getField(p);
							fSudoku.setCurrentValue(fSolved.getSolution(), false);
							definedFields.add(p);//todo encapsulate into adddefinedfield2(), `adddefinedfield()` seems to be for initial...
						}
				}
				System.out.println(" #definedFields: " + definedFields.size());
			}

			// Call the callback
			SudokuBuilder suBi = new SudokuBuilder(sudoku.getSudokuType());

			for(Position p : getPositions(solvedSudoku)){
					int value = solvedSudoku.getField(p).getSolution();
					suBi.addSolution(p, value);
					if (!sudoku.getField(p).isNotSolved())
						suBi.setFixed(p);

			}
			Sudoku res = suBi.createSudoku();
			System.out.println("debug output: res created, is it null? - "+ (res==null));

			//we want to know the solutions used, so quickly an additional solver
			Solver quickSolver = new Solver(res);
			quickSolver.solveAll(true, false, true);

			res.setComplexity(sudoku.getComplexity());
			if(callbackObject.toString().equals("experiment")){
				System.out.println("solutions are null?:" + (quickSolver.getSolutions() == null));
				callbackObject.generationFinished(res, quickSolver.getSolutions());
				System.out.println("debug output: mark6.2a");

			}
			else{
				System.out.println("doesn't equal exp??!!");
				callbackObject.generationFinished(res);
				System.out.println("debug output: mark6.2b");

			}
			System.out.println("debug output: mark7");

		}


		// Calculate the number of fields to be filled
		// the number is determined as the smaller of
		//        - the standard allocation factor defined in the type
		//        - the average #fields per difficulty level defined in the type
		private int getNumberOfFieldsToDefine(SudokuType type, ComplexityConstraint desiredComplexityConstraint){
			//TODO What do we have the allocation factor for??? can't it always be expressed through avg-fields?
			float standardAllocationFactor = type.getStandardAllocationFactor();
			int fieldsOnSudokuBoard = type.getSize().getX() * type.getSize().getY();
			int fieldsByType = (int) (fieldsOnSudokuBoard * standardAllocationFactor); //TODO wäre freeFields.size nicht passender?
			int fieldsByComp = desiredComplexityConstraint.getAverageFields();
			return Math.min(fieldsByType, fieldsByComp);
		}

		/** returns `percentage` percent of the #positions in the type
		 * e.g. for standard 9x9 and 0.5 -> 40 */
		private int getReallocationAmount(SudokuType st, double percentage){
			int numberOfPositions = 0;
			for (Position p : sudoku.getSudokuType().getValidPositions())
				numberOfPositions++;

			int reallocationAmount = (int) (numberOfPositions * percentage); //remove/delete up to 10% of board
			return Math.max(1, reallocationAmount); // at least 1
		}

		/**
		 * Definiert ein weiteres Feld, sodass weiterhin Constraint Saturation
		 * vorhanden ist. Die Position des definierten Feldes wird
		 * zurückgegeben. Kann keines gefunden werden, so wird null
		 * zurückgegeben.
		 * 
		 * @return Die Position des definierten Feldes oder null, falls keines
		 *         gefunden wurde
		 */

		private Position addDefinedField() {
			//TODO not sure what they do


			int xSize = sudoku.getSudokuType().getSize().getX();
			int ySize = sudoku.getSudokuType().getSize().getY();

			// Ein Array von Markierungen zum Testen, welches Felder belegt werden können
			/*true means marked, i.e. already defined or not part of the game e.g. 0,10 for samurai
			 *false means can be added
			 */
			boolean[][] markings = new boolean[xSize][ySize]; //all false by default.



			//definierte Felder markieren
			for (Position p : this.definedFields) {
				markings[p.getX()][p.getY()] = true;
			}

			/* avoids infitite while loop*/
			int count = definedFields.size();

			//find random {@code Position} p
			Position p = null;

			while (p == null && count < xSize * ySize) {
				int x = random.nextInt(xSize);
				int y = random.nextInt(ySize);
				if (sudoku.getField(Position.get(x, y)) == null) {//position existiert nicht
					markings[x][y] = true;
					count++;
				} else if (markings[x][y] == false) { //pos existiert und ist unmarkiert
					p = Position.get(x, y);
				}
			}

			//construct a list of symbols starting at arbitrary point. there is no short way to do this without '%' 
			int numSym = sudoku.getSudokuType().getNumberOfSymbols();
			int offset = random.nextInt(numSym);
			Queue<Integer> symbols = new LinkedList<Integer>();
			for (int i = 0; i < numSym; i++)
				symbols.add(i);

			for(int i=0; i < offset; i++)//rotate offset times
				symbols.add(symbols.poll());			
			
			//constraint-saturierende belegung suchen 
			boolean valid = false;
			for (int s: symbols) {				
				sudoku.getField(p).setCurrentValue(s, false);
				//alle constraints saturiert?
				valid = true;
				for (Constraint c: this.sudoku.getSudokuType()) {
					if (!c.isSaturated(sudoku)) {
						valid = false;
						sudoku.getField(p).setCurrentValue(Field.EMPTYVAL, false);
						break;
					}
				}
				if (valid) {
					definedFields.add(p);
					freeFields.remove(p); //if it's defined it is no longer free
					break;
				}
			}
			if (!valid)
				p = null;

			return p;
		}

		/**
		 * Removes one of the defined fields (random selection)
		 * 
		 * @return position of removed field or null is nothing there to remove
		 */
		private Position removeDefinedField() {
			if (definedFields.isEmpty())
				return null;

			int nr = random.nextInt(definedFields.size());
			Position p = definedFields.remove(nr);
			sudoku.getField(p).setCurrentValue(Field.EMPTYVAL, false);
			freeFields.add(p);
			return p;
		}

		/**
		 * Tries {@code numberOfFieldsToRemove} times to remove a defined field
		 * @param numberOfFieldsToRemove number of fields to remove
		 * @return list of removed positions
		 * */
		private List<Position> removeDefinedFields(int numberOfFieldsToRemove){
			ArrayList<Position> removed = new ArrayList<>();
			for (int i = 0; i < numberOfFieldsToRemove; i++){
				Position p = removeDefinedField();
				if(p != null)
					removed.add(p);
			}

			return removed;
		}

	}

	/**
	 * returns all positions of non-null Fields of sudoku
	 * @param sudoku a sudoku object
	 *
	 * @return list of positions whose corresponding {@code Field} objects are not null
	 */
	public static List<Position> getPositions(Sudoku sudoku){
		List<Position> p = new ArrayList<>();

		for (int x = 0; x < sudoku.getSudokuType().getSize().getX(); x++)
			for (int y = 0; y < sudoku.getSudokuType().getSize().getY(); y++)
				if (sudoku.getField(Position.get(x, y)) != null)
					p.add(Position.get(x, y));

		return p;
	}
	public void printDebugMsg(){
		System.out.println("This is the debug message from `Generator`");
	}

}
