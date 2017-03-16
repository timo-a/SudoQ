package de.sudoq.controller.sudoku;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import de.sudoq.R;
import de.sudoq.model.game.Game;
import de.sudoq.model.profile.Profile;
import de.sudoq.view.SudokuLayout;

/**
 * Created by timo on 04.11.16.
 */
public class ControlPanelFragment extends Fragment {

    private SudokuActivity activity;
    private SudokuLayout sl;
    private Game game;
    private SudokuController controller;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void initialize(){
        activity =  (SudokuActivity)getActivity();
        sl   = activity.getSudokuLayout();
        game = activity.getGame();
        controller = activity.getSudokuController();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FrameLayout frameLayout = new FrameLayout(getActivity());
        populateViewForOrientation(inflater, frameLayout);
        return frameLayout;
    }


    /**
     * Container-Klasse für die Buttons dieser Activity
     */
    private static class Buttons {
        /**
         * Der "Redo" Button
         */
        protected static ImageButton redoButton;

        /**
         * Der "Undo" Button
         */
        protected static ImageButton undoButton;

        /**
         * Der "ActionTree anzeigen" Button
         */
        protected static ImageButton actionTreeButton;

        /**
         * Der "Gesten umschalten" Button
         */
        protected static ImageButton gestureButton;

        /**
         * Der "Hilfestellungen anzeigen" Button
         */
        protected static ImageButton assistancesButton;

        /**
         * Der "Lesezeichen" Button des ActionTrees
         */
        protected static Button bookmarkButton;

        /**
         * Der "Schließen" Button des ActionTrees
         */
        protected static Button closeButton;
    }

    void inflateButtons(){
        Buttons.       redoButton = (ImageButton) getView().findViewById(R.id.button_sudoku_redo);
        Buttons.       undoButton = (ImageButton) getView().findViewById(R.id.button_sudoku_undo);
        Buttons. actionTreeButton = (ImageButton) getView().findViewById(R.id.button_sudoku_actionTree);
        Buttons.    gestureButton = (ImageButton) getView().findViewById(R.id.button_sudoku_toggle_gesture);
        Buttons.assistancesButton = (ImageButton) getView().findViewById(R.id.button_sudoku_help);

        SudokuActivity activity =  (SudokuActivity)getActivity();

        Buttons.   bookmarkButton = (Button) activity.findViewById(R.id.sudoku_action_tree_button_bookmark);
        Buttons.      closeButton = (Button) activity.findViewById(R.id.sudoku_action_tree_button_close);
    }

    /**
     * Aktualisiert alle Buttons, also den Redo, Undo und ActionTree-Button,
     * sowie die Tastatur
     */
    void updateButtons() {
        SudokuActivity activity =  (SudokuActivity)getActivity();
        boolean actionTreeShown = activity.isActionTreeShown();
        boolean finished = activity.getFinished();
        Buttons.       redoButton.setEnabled(game.getStateHandler().canRedo() && !actionTreeShown);
        Buttons.       undoButton.setEnabled(game.getStateHandler().canUndo() && !actionTreeShown);
        Buttons. actionTreeButton.setEnabled(!actionTreeShown);
        Buttons.assistancesButton.setEnabled(!actionTreeShown && !finished);
        Buttons.    gestureButton.setEnabled(!actionTreeShown);
        SudokuLayout sudokuView = sl;
        activity.getMediator().setKeyboardState(!finished && sudokuView.getCurrentFieldView() != null);
    }

    ImageButton getGestureButton(){
        return Buttons.gestureButton;
    }

    void onClick(View v){
        SudokuActivity activity =  (SudokuActivity)getActivity();
        SudokuController sudokuController = controller;
        UserInteractionMediator mediator = activity.getMediator();

        if (v == Buttons.undoButton) {
            sudokuController.onUndo();
            mediator.updateKeyboard();
        } else if (v == Buttons.redoButton) {
            sudokuController.onRedo();
            mediator.updateKeyboard();
        } else if (v == Buttons.actionTreeButton) {
            activity.toogleActionTree();
        } else if (v == Buttons.gestureButton) {
            Profile profile = Profile.getInstance();
            if (activity.checkGesture()) {
				/* toggle 'gesture active'
				 * toggle button icon as well */
                profile.setGestureActive( !profile.isGestureActive() );
                v.setSelected(profile.isGestureActive());
            } else {
                profile.setGestureActive(false);
                v.setSelected(           false);
                Toast.makeText(activity, getString(R.string.error_gestures_not_complete), Toast.LENGTH_LONG).show();
            }
        } else if (v == Buttons.assistancesButton) {
            activity.showAssistancesDialog();
        } else if (v == Buttons.bookmarkButton) {
            this.game.markCurrentState();
            activity.getActionTreeController().refresh();
        } else if (v == Buttons.closeButton) {
            activity.toogleActionTree();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        populateViewForOrientation(inflater, (ViewGroup) getView());
    }

    private void populateViewForOrientation(LayoutInflater inflater, ViewGroup viewGroup) {
        SudokuActivity activity =  (SudokuActivity)getActivity();
        game = activity.getGame();

        viewGroup.removeAllViewsInLayout();


        Configuration conf = getResources().getConfiguration();
        boolean portraitLeft = conf.orientation == conf.ORIENTATION_PORTRAIT && game.isLefthandedModeActive();
        int layout = portraitLeft ? R.layout.bottom_panel_left
                                  : R.layout.bottom_panel;

        View subview = inflater.inflate(layout, viewGroup);

        // Find your buttons in subview, set up onclicks, set up callbacks to your parent fragment or activity here.
    }


    private View getControlPanel(){

        Configuration conf = getResources().getConfiguration();
        boolean portraitLeft = conf.orientation == conf.ORIENTATION_PORTRAIT && game.isLefthandedModeActive();

        return activity.findViewById(portraitLeft ?  R.id.controlPanelLeft //sl.find... doesn't seem to work
                                            :  R.id.controlPanel     );
    }

    public void hide(){getControlPanel().setVisibility(View.GONE);}

    public void show(){getControlPanel().setVisibility(View.VISIBLE);}
}
