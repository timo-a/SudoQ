package de.sudoq.controller.menus.preferences;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import de.sudoq.controller.SudoqCompatActivity;
import de.sudoq.controller.menus.GestureBuilder;
import de.sudoq.model.ModelChangeListener;
import de.sudoq.model.game.Assistances;
import de.sudoq.model.game.GameSettings;
import de.sudoq.model.profile.Profile;

public abstract class PreferencesActivity extends SudoqCompatActivity implements ModelChangeListener<Profile> {
	
	CheckBox gesture;
	CheckBox autoAdjustNotes;
	CheckBox markRowColumn;
	CheckBox markWrongSymbol;
	CheckBox restrictCandidates;
	
	//CheckBox helper;
	//CheckBox lefthand;
	Button   restricttypes;
	
	
	protected abstract void adjustValuesAndSave();
	
	/**
	 * Wird aufgerufen, falls eine andere Activity den Eingabfokus erhält.
	 * Speichert die Einstellungen.
	 */
	@Override
	public void onPause() {
		super.onPause();
		adjustValuesAndSave();
	}
	
	
	/**
	 * Öffnet den GestureBuilder.
	 * 
	 * @param view
	 *            unbenutzt
	 */
	public void openGestureBuilder(View view) {
		Intent gestureBuilderIntent = new Intent(this, GestureBuilder.class);
		startActivity(gestureBuilderIntent);
	}
	
	/**
	 * Wird aufgerufen, falls die Activity erneut den Eingabefokus erhält. Läd
	 * die Preferences anhand der zur Zeit aktiven Profil-ID.
	 */
	@Override
	public void onResume() {
		super.onResume();
		refreshValues();
	}
	
	protected abstract void refreshValues();

	public void onModelChanged(Profile obj) {
		this.refreshValues();
	}
	
	abstract protected void saveToProfile();
	
	protected void saveAssistance(Assistances a, CheckBox c){
		Profile.getInstance().setAssistance(a, c.isChecked());
	}
	

	protected void saveCheckbox(CheckBox cb, Assistances a, GameSettings gs){
		if(cb.isChecked())
			gs.setAssistance(a);
		else
			gs.clearAssistance(a);
	}
}