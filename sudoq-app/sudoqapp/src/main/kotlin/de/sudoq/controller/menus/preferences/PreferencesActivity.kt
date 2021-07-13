package de.sudoq.controller.menus.preferences

import android.content.Intent
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import de.sudoq.R
import de.sudoq.controller.SudoqCompatActivity
import de.sudoq.controller.menus.GestureBuilder
import de.sudoq.model.ModelChangeListener
import de.sudoq.model.game.Assistances
import de.sudoq.model.game.GameSettings
import de.sudoq.model.profile.ProfileSingleton.Companion.getInstance
import de.sudoq.model.profile.ProfileManager
import de.sudoq.persistence.profile.ProfileRepo

abstract class PreferencesActivity : SudoqCompatActivity(), ModelChangeListener<ProfileManager> {
    var gesture: CheckBox? = null
    var autoAdjustNotes: CheckBox? = null
    var markRowColumn: CheckBox? = null
    var markWrongSymbol: CheckBox? = null
    var restrictCandidates: CheckBox? = null

    //CheckBox helper;
    //CheckBox lefthand;
    var restricttypes: Button? = null
    protected abstract fun adjustValuesAndSave()

    /**
     * Wird aufgerufen, falls eine andere Activity den Eingabfokus erhält.
     * Speichert die Einstellungen.
     */
    public override fun onPause() {
        super.onPause()
        adjustValuesAndSave()
    }

    /**
     * Öffnet den GestureBuilder.
     *
     * @param view
     * unbenutzt
     */
    fun openGestureBuilder(view: View?) {
        val gestureBuilderIntent = Intent(this, GestureBuilder::class.java)
        startActivity(gestureBuilderIntent)
    }

    /**
     * Wird aufgerufen, falls die Activity erneut den Eingabefokus erhält. Läd
     * die Preferences anhand der zur Zeit aktiven Profil-ID.
     */
    public override fun onResume() {
        super.onResume()
        refreshValues()
    }

    protected abstract fun refreshValues()
    override fun onModelChanged(obj: ProfileManager) {
        refreshValues()
    }

    protected abstract fun saveToProfile()
    protected fun saveAssistance(a: Assistances, c: CheckBox) {
        val profilesDir = getDir(getString(R.string.path_rel_profiles), MODE_PRIVATE)
        val p = getInstance(profilesDir, ProfileRepo(profilesDir))
        p.setAssistance(a, c.isChecked)
    }

    protected fun saveCheckbox(cb: CheckBox, a: Assistances, gs: GameSettings) {
        if (cb.isChecked) gs.setAssistance(a) else gs.clearAssistance(a)
    }
}