/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.model.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import de.sudoq.model.actionTree.Action;
import de.sudoq.model.actionTree.ActionTreeElement;
import de.sudoq.model.actionTree.NoteActionFactory;
import de.sudoq.model.actionTree.SolveAction;
import de.sudoq.model.actionTree.SolveActionFactory;
import de.sudoq.model.sudoku.Constraint;
import de.sudoq.model.sudoku.Field;
import de.sudoq.model.sudoku.Position;
import de.sudoq.model.sudoku.Sudoku;
import de.sudoq.model.sudoku.SudokuManager;
import de.sudoq.model.xml.XmlAttribute;
import de.sudoq.model.xml.XmlTree;
import de.sudoq.model.xml.Xmlable;

/**
 * Diese Klasse repräsentiert ein Sudoku-Spiel. Bezüglich der Controller stellt das Game eine Fassade dar.
 */
public class Game implements Xmlable {
    /** Attributes */

    /**
     * Eine eindeutige Zahl, zur Identifikation
     */
    private int id;

    /**
     * Das zugehörige Sudoku
     */
    private Sudoku sudoku;

    /**
     * Der dazugehörige GameStateHandler, um den Zustand des Sudokus zu verwalten
     */
    private GameStateHandler stateHandler;

    /**
     * Die seit Spielstart verstrichene Zeit in Sekunden
     */
    private int time;

    /**
     * Die Summe der Kosten der bisher genutzten Hilfestellungen
     */
    private int assistancesCost;

    /**
     * Ein BitSet, welches die zur Verfügung stehenden Hilfestellungen darstellt
     */
    private GameSettings gameSettings;

    /**
     * Zeigt an, ob das Sudoku beendet ist
     */
    private boolean finished;

    /** Constructors */

    /**
     * Ein geschützter Konstruktor um die Instanziierung von Games außerhalb dieses Packages zu vermeiden. Wird er
     * aufgerufen wird eine neues Game mit dem gegebenen Sudoku instanziiert. Die verfügbaren Hilfestellungen werden
     * anhand der Einstellungen des aktuellen Profils gesetzt. Ist das übergebene Sudoku null, so wird eine
     * IllegalArgumentException geworfen.
     * 
     * @param id
     *            id dieses Spiels
     * @param sudoku
     *            Das Sudoku, das bespielt werden soll
     * @throws IllegalArgumentException
     *             Wird geworfen, falls das übergebene Sudoku null ist
     */
    protected Game(int id, Sudoku sudoku) {
        if (sudoku == null) {
            throw new IllegalArgumentException("sudoku null");
        }
        time = 0;
        this.id = id;
        gameSettings = new GameSettings();
        this.sudoku = sudoku;
        this.time = 0;
        stateHandler = new GameStateHandler();
    }

    /**
     * Erzeugt ein vollständig leeres Game, welches noch gefüllt werden muss.
     */
    // package scope!
    Game() {
        id = -1;
    }

    /** Methods */

    /**
     * Diese Methode gibt die profilweit eindeutige id dieses Games zurück.
     * 
     * @return Die id
     */
    public int getId() {
        return id;
    }

    /**
     * Diese Methode gibt das Sudoku dieses Spiels zurück.
     * 
     * @return Das Sudoku
     */
    public Sudoku getSudoku() {
        return sudoku;
    }

    /**
     * Fügt zur Zeit dieses Spiel die spezifizierte hinzu.
     * 
     * @param time
     *            Die hinzuzufügende Zeit
     */
    public void addTime(int time) {
        this.time += time;
    }

    /**
     * Gibt die Zeit dieses Spiels zurück.
     * 
     * @return Die Zeit dieses Spiels in Sekunden
     */
    public int getTime() {
        return this.time;
    }

    /**
     * Gibt den Highscorewert des Spiels zurück
     * 
     * @return der ermittelte Wert
     */
    public int getScore() {
        int scoreFactor = 0;
        switch (sudoku.getComplexity()) {
        case infernal:
            scoreFactor = (int) Math.pow(sudoku.getSudokuType().getNumberOfSymbols(), 4.0f);
            break;
        case difficult:
            scoreFactor = (int) Math.pow(sudoku.getSudokuType().getNumberOfSymbols(), 3.5f);
            break;
        case medium:
            scoreFactor = (int) Math.pow(sudoku.getSudokuType().getNumberOfSymbols(), 3.0f);
            break;
        case easy:
            scoreFactor = (int) Math.pow(sudoku.getSudokuType().getNumberOfSymbols(), 2.5f);
            break;
        }

        return (int) (scoreFactor * 10 / ((this.time + getAssistancesTimeCost()) / 60.0f));
    }
    
    public int getAssistancesTimeCost() {
    	return this.assistancesCost * 60;
    }

    /**
     * Überprüft das zugrundeliegende Sudoku auf Korrektheit. Dies wird als Assistance gewertet und die AssistanceCost
     * wird um 1 erhöht.
     * 
     * @return true, falls das Sudoku bisher korrekt gelöst ist, false falls nicht
     */
    public boolean checkSudoku() {
        this.assistancesCost += 1;
        return checkSudokuValidity();
    }

    /**
     * Überprüft das zugrundeliegende Sudoku auf Korrektheit.
     * 
     * @return true, falls das Sudoku bisher korrekt gelöst ist, false falls nicht
     */
    private boolean checkSudokuValidity() {
        boolean correct = !sudoku.hasErrors();
        if (correct) {
            getCurrentState().markCorrect();
        } else {
            getCurrentState().markWrong();
        }

        return correct;
    }

    /**
     * Gibt die Kosten für die Hilfestellungen zurück, welches ein Maß dafür ist, wieviele (bzw. wie schwere)
     * Hilfestellung verfügbar waren bzw. tatsächlich genutzt wurden.
     * 
     * @return Die Kosten für die Hilfestellungen
     */
    public int getAssistancesCost() {
        return this.assistancesCost;
    }

    /**
     * Diese Methode gibt den GameStateHandler dieses Spiels zurück.
     * 
     * @return Der GameStateHandler
     */
    public GameStateHandler getStateHandler() {
        return stateHandler;
    }

    /**
     * Führt die gegebene Action aus und speichert sie ab.
     * 
     * @param action
     *            Die auszuführende Aktion
     */
    public void addAndExecute(Action action) {
        if (this.finished)
            return;

        this.stateHandler.addAndExecute(action);

        updateNotes(sudoku.getField(action.getFieldId()));

        if (isFinished())
            this.finished = true;
    }

    /**
     * Updatet die Notizen in den Constraints des spezifizierten Feldes so, dass der spezifizierte Wert aus diesen
     * gelöscht wird. Dies wird nur ausgeführt, falls die entsprechende Hilfestellung im Game aktiviert ist.
     * 
     * @param field
     *            Das Field, welches modifiziert wurde
     */
    private void updateNotes(Field field) {
        if (!this.isAssistanceAvailable(Assistances.autoAdjustNotes))
            return;

        Position editedPos = sudoku.getPosition(field.getId());
        int value = field.getCurrentValue();

        /*this.sudoku.getSudokuType().getConstraints().stream().filter(c -> c.includes(editedPos))
                                                             .flatMap(c -> c.getPositions().stream())
                                                             .filter(changePos -> this.sudoku.getField(changePos).isNoteSet(value))
                                                             .forEachOrdered(changePos -> this.addAndExecute(new NoteActionFactory().createAction(value, this.sudoku.getField(changePos))));
        should work, but to tired to try*/
        for (Constraint c : this.sudoku.getSudokuType()) {
            if (c.includes(editedPos)) {
                for (Position changePos : c) {
                    if (this.sudoku.getField(changePos).isNoteSet(value)) {
                        this.addAndExecute(new NoteActionFactory().createAction(value, this.sudoku.getField(changePos)));
                    }
                }
            }
        }
    }

    /**
     * Führt alle nötigen Aktionen aus, damit das Sudoku nach Ausführung dieser Methode wieder im gleichen Zustand wie
     * nach der ersten Ausführung der gegebenen Aktion ist. Ist das übergebene Element null, so wird eine
     * IllegalArgumentException geworfen.
     * 
     * @param ate
     *            das ActionTreeElement in dessen Zustand das Sudoku überführt werden soll
     * @throws IllegalArgumentException
     *             Wird geworfen, falls das übergebene ActionTreeElement null ist
     */
    public void goToState(ActionTreeElement ate) {
        if(ate == null)
            throw new IllegalArgumentException("ate is null");
        stateHandler.goToState(ate);
    }

    /**
     * Macht die letzte Aktion rückgängig. Ein Schritt rückwärts in der Versionshistorie.
     */
    public void undo() {
        stateHandler.undo();
    }

    /**
     * Geht, falls möglich und eindeutig, einen Schritt vorwärts in der Versionshistorie. Ist diese verzweigt, aber der
     * Schritt rückwärts erfolgte über undo(), wird dieses undo rückgängig gemacht.
     */
    public void redo() {
        stateHandler.redo();
    }

    /**
     * Diese Methode gibt das ActionTreeElement zurück, welches den aktuellen Zustand beinhaltet.
     * 
     * @return Der aktuelle Zustand als ActionTreeElement
     */
    public ActionTreeElement getCurrentState() {
        return stateHandler.getCurrentState();
    }

    /**
     * Markiert den aktuellen Zustand um ihn später einfacher wiederzufinden
     */
    public void markCurrentState() {
        stateHandler.markCurrentState();
    }

    /**
     * Überprüft, ob das gegebene ActionTreeElement markiert ist.
     * 
     * @param ate
     *            das zu überprüfende ActionTreeElement
     * @return true falls es markiert ist und false falls nicht
     */
    public boolean isMarked(ActionTreeElement ate) {
        return stateHandler.isMarked(ate);
    }

    /**
     * Überprüft ob das Sudoku im aktuellen Zustand vollständig und korrekt gelöst ist.
     * 
     * @return true falls gelöst, andernfalls false
     */
    public boolean isFinished() {
        return this.finished || this.sudoku.isFinished();
    }

    /**
     * Versucht das spezifizierte Feld zu lösen und gibt zurück, ob das Lösen möglich war. Ist das Sudoku ungültig oder
     * gibt es Fehler im Sudoku, so wird false zurückgegeben.
     * 
     * @param field
     *            Das zu lösende Feld
     * @return true, falls das Feld gelöst werden konnte, false andernfalls
     */
    public boolean solveField(Field field) {
        if (this.sudoku.hasErrors() || field == null)
            return false;

        this.assistancesCost += 3;
        int solution = field.getSolution();
        if (solution != Field.EMPTYVAL) {
            this.addAndExecute(new SolveActionFactory().createAction(solution, field));
            return true;
        } else {
            return false;
        }
    }

    /**
     * Versucht ein zufällig ausgewähltes Feld des Sudokus zu lösen und gibt zurück, ob dies möglich war.
     * 
     * @return true, falls ein Feld gelöst werden konnte, false falls nicht
     */
    public boolean solveField() {
        if (this.sudoku.hasErrors())
            return false;

        this.assistancesCost += 3;

        for (Field f : this.sudoku) {
            if (f.isNotSolved()) {
                this.addAndExecute(new SolveActionFactory().createAction(f.getSolution(), f));
                break;
            }
        }
        return true;

        /*
         * Solution solution = solver.getHint(); if (solution != null) {
         * stateHandler.addAndExecute(solution.getAction()); return true; } else { return false; }
         */
    }

    /**
     * Löst das gesamte Sudoku. Bei Erfolg wird true zurückgegeben, sonst false.
     * 
     * @return true, falls das Sudoku gelöst werden konnte, sonst false
     */
    public boolean solveAll() {
        if (this.sudoku.hasErrors())
            return false;

        List<Field> unsolvedFields = new ArrayList<Field>();
        for (Field f : this.sudoku) {
            if (f.isNotSolved()) {
                unsolvedFields.add(f);
            }
        }

        Random rnd = new Random();
        while (!unsolvedFields.isEmpty()) {
            int nr = rnd.nextInt(unsolvedFields.size());
            this.addAndExecute(new SolveActionFactory().createAction(unsolvedFields.get(nr).getSolution(),
                    unsolvedFields.get(nr)));
            unsolvedFields.remove(nr);
        }

        this.assistancesCost += Integer.MAX_VALUE / 80;
        return true;
        /*
         * if (solver.solveAll(false, false, false) != null) { for (Field f : unsolvedFields) { this.addAndExecute(new
         * SolveActionFactory().createAction(f.getCurrentValue(), f)); } return true; } else { return false; }
         */
    }

    /**
     * Geht zurück zum letzten korrekten gelösten Status im Aktionsbaum. Ist der aktuelle Status korrekt wird nichts
     * getan. Dies wird als Assistance gewertet und die AssistanceCost wird um 3 erhöht.
     */
    public void goToLastCorrectState() {
        this.assistancesCost += 3;
        while (!this.checkSudokuValidity()) {
            undo();
        }
        getCurrentState().markCorrect();
    }
    
    /**
     * Geht zurück zum letzten Lesezeichen im Aktionsbaum. Ist der aktuelle Zustand bereits mit einem Lesezeichen versehen, so wird
     * nichts getan. Befindet sich vor dem aktuellen Zustand kein markierter Zustand, so wird zum Wurzelzustand zurückgegangen.
     */
    public void goToLastBookmark() {
        while (!this.stateHandler.getCurrentState().equals(this.stateHandler.getActionTree().getRoot()) 
        		&& !this.stateHandler.getCurrentState().isMarked()) {
            undo();
        }
    }


    /**
     * Setzt die für dieses Spiel verfügbaren Assistances auf die spezifizierten. Diese sollten für korrekte
     * Funktionalität mithilfe der Methoden der {@link Assistances}-Klasse erstellt werden. Missbrauch kann zu
     * unerwünschten Ergebnissen führen.
     * 
     * @param assistances
     *            Die Assistances die für dieses Game gesetzt werden soll
     * @throws IllegalArgumentException
     *             Wird geworfen, falls das spezifizierte BitSet null ist
     */
    public void setAssistances(GameSettings assistances) {
        if (assistances == null)
            throw new IllegalArgumentException("AssistanceSet was null");
        
        this.gameSettings = assistances;
        
        /* calculate costs */
        if (isAssistanceAvailable(Assistances.autoAdjustNotes))
            this.assistancesCost += 4;
        if (isAssistanceAvailable(Assistances.markRowColumn))
            this.assistancesCost += 2;
        if (isAssistanceAvailable(Assistances.markWrongSymbol))
            this.assistancesCost += 6;
        if (isAssistanceAvailable(Assistances.restrictCandidates))
            this.assistancesCost += 12;
    }

    /**
     * Gibt true zurück, falls die spezifizierte Hilfestellung verfügbar ist, false falls nicht oder falls die
     * Hilfestellung ungültig ist.
     * 
     * @param assist
     *            Die Hilfestellung, welche auf Verfügbarkeit geprüft werden soll
     * @return true, falls die Hilfestellung verfügbar ist, false andernfalls
     */
    public boolean isAssistanceAvailable(Assistances assist) {
        return this.gameSettings.getAssistance(assist);
    }

    public boolean isLefthandedModeActive() {
        return this.gameSettings.isLefthandModeSet();
    }

    
    
    /**
     * {@inheritDoc}
     */
    public XmlTree toXmlTree() {
        XmlTree representation = new XmlTree("game");
        representation.addAttribute(new XmlAttribute("id", "" + id));
        representation.addAttribute(new XmlAttribute("finished", "" + finished));
        representation.addAttribute(new XmlAttribute("time", "" + time));
        representation.addAttribute(new XmlAttribute("currentTurnId", "" + getCurrentState().getId()));
        representation.addChild(this.gameSettings.toXmlTree());
        representation.addAttribute(new XmlAttribute("assistancesCost", "" + this.assistancesCost));

        representation.addChild(sudoku.toXmlTree());

        ArrayList<ActionTreeElement> actionList = new ArrayList<ActionTreeElement>();
        for (ActionTreeElement ate : stateHandler.getActionTree()) {
            actionList.add(ate);
        }
        Collections.sort(actionList);
        for (ActionTreeElement ate : actionList) {
            if (ate.toXml() != null)
                representation.addChild(ate.toXml());
            // TODO cant add child do for null just nothing?
        }

        return representation;
    }

    /**
     * {@inheritDoc}
     */
    public void fillFromXml(XmlTree xmlTreeRepresentation) {

        id = Integer.parseInt(xmlTreeRepresentation.getAttributeValue("id"));
        time = Integer.parseInt(xmlTreeRepresentation.getAttributeValue("time"));
        int currentStateId = Integer.parseInt(xmlTreeRepresentation.getAttributeValue("currentTurnId"));

        // Problems:
        // - What about corrupt files? is the game validated after it has been
        // filled?

        this.assistancesCost = Integer.parseInt(xmlTreeRepresentation.getAttributeValue("assistancesCost"));

        for (XmlTree sub : xmlTreeRepresentation) {
            if (sub.getName().equals("sudoku")) {
                sudoku = SudokuManager.getEmptySudokuToFillWithXml();
                sudoku.fillFromXml(sub);
            }else if(sub.getName().equals("gameSettings")){
                gameSettings = new GameSettings();
                gameSettings.fillFromXml(sub);
            }
        }
        stateHandler = new GameStateHandler();

        for (XmlTree sub : xmlTreeRepresentation) {
            if (sub.getName().equals("action")) {
                int diff = Integer.parseInt(sub.getAttributeValue(ActionTreeElement.DIFF));

                // put the action to the parent action
                String attributeValue =  sub.getAttributeValue(ActionTreeElement.PARENT);
                int parentID = Integer.parseInt(attributeValue);
                ActionTreeElement parent = stateHandler.getActionTree().getElement(parentID);
                goToState(parent);
                // if(!sub.getAttributeValue(ActionTreeElement.PARENT).equals(""))
                // is not necessary since the root action comes from the gsh so

                // every element has e parent

                int field_id = Integer.parseInt(sub.getAttributeValue(ActionTreeElement.FIELD_ID));
                Field f = sudoku.getField(field_id);
                if (sub.getAttributeValue(ActionTreeElement.ACTION_TYPE).equals(SolveAction.class.getSimpleName())) {
                    this.stateHandler.addAndExecute(new SolveActionFactory().createAction(f.getCurrentValue() + diff,
                                                                                          f));
                } else { // if(sub.getAttributeValue(ActionTreeElement.ACTION_TYPE).equals(NoteAction.class.getSimpleName()))
                    this.stateHandler.addAndExecute(new NoteActionFactory().createAction(diff, f));
                }

                if (Boolean.parseBoolean(sub.getAttributeValue(ActionTreeElement.MARKED))) {
                    markCurrentState();
                }
                String s = sub.getAttributeValue(ActionTreeElement.MISTAKE);
                if (s != null && Boolean.parseBoolean(s)) {
                    getCurrentState().markWrong();
                }
                s = sub.getAttributeValue(ActionTreeElement.CORRECT);
                if (s != null && Boolean.parseBoolean(s)) {
                    getCurrentState().markCorrect();
                }
            }
        }

        finished = Boolean.parseBoolean(xmlTreeRepresentation.getAttributeValue("finished"));
        goToState(stateHandler.getActionTree().getElement(currentStateId));

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Game) {
            Game g = (Game) obj;
            return this.id == g.id && this.sudoku.equals(g.sudoku)
                    && this.stateHandler.getActionTree().equals(g.stateHandler.getActionTree())
                    && this.getCurrentState().equals(g.getCurrentState());
        }
        return false;
    }

}
