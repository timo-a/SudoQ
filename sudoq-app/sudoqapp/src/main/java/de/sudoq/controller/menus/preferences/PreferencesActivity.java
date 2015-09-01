package de.sudoq.controller.menus.preferences;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import de.sudoq.controller.SudoqActivitySherlock;
import de.sudoq.controller.menus.GestureBuilder;
import de.sudoq.model.ModelChangeListener;
import de.sudoq.model.game.Assistances;
import de.sudoq.model.profile.Profile;

public abstract class PreferencesActivity extends SudoqActivitySherlock implements ModelChangeListener<Profile> {
	
	CheckBox gesture;
	CheckBox autoAdjustNotes;
	CheckBox markRowColumn;
	CheckBox markWrongSymbol;
	CheckBox restrictCandidates;
	
	CheckBox helper;
	CheckBox lefthand;
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
	
	protected void saveToProfile() {
		Profile p = Profile.getInstance();
		p.setGestureActive(gesture.isChecked());
		if(helper != null)
			p.setHelperActive(helper.isChecked());
		if(lefthand != null)
			p.setLefthandActive(lefthand.isChecked());
		saveAssistance(Assistances.autoAdjustNotes,    autoAdjustNotes);
		saveAssistance(Assistances.markRowColumn,      markRowColumn  );
		saveAssistance(Assistances.markWrongSymbol,    markWrongSymbol);
		saveAssistance(Assistances.restrictCandidates, restrictCandidates);
		Profile.getInstance().saveChanges();
	}
	
	private void saveAssistance(Assistances a, CheckBox c){
		Profile.getInstance().setAssistance(a, c.isChecked());
	}
	
	/* parameter View only needed to be foud by xml who clicks this*/
	public void switchToAdvancedPreferences(View view){
		
		Intent advIntent = new Intent(this, AdvancedPreferencesActivity.class);
		AdvancedPreferencesActivity.myCaller=this;
		startActivity(advIntent);

	}
		
}