package de.sudoq.controller

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import de.sudoq.controller.menus.preferences.LanguageCode
import de.sudoq.controller.menus.preferences.LanguageUtility

/**
 * This abstract activity can be used as parent activity.
 * It will check, whenever the activity gets created/started/resumed, if the currently locale is still correctly set.
 * If not, it restarts itself.
 * It checks on all 3 stages to capture the changes as early as possible. This adds checking overhead, but removes other possible overhead.
 */
abstract class LanguageAdaptingCompatActivity : AppCompatActivity() {

    /**
     * The language code used when this activity got created.
     * If the code changes, the activity gets recreated and this field updated.
     */
    protected var currentLanguageCode: LanguageCode? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Save the language code of which the resources are currently set to.
        currentLanguageCode = LanguageUtility.getResourceLanguageCode(this)
        restartIfWrongLanguage()
    }

    override fun onStart() {
        super.onStart()

        restartIfWrongLanguage()
    }

    override fun onResume() {
        super.onResume()

        restartIfWrongLanguage()
    }

    /**
     * Can be called from any child activity to check if the language is still correct.
     * If the language is no longer correct it will restart the activity.
     */
    protected fun restartIfWrongLanguage() {

        // Get the language code which the user would like to see:
        val desiredLanguageCode = LanguageUtility.getDesiredLanguage(this)
        // Check if the activity already has that language:
        if (desiredLanguageCode != currentLanguageCode) {
            // If not get the current resource locale:
            val latestLanguageCode = LanguageUtility.getResourceLanguageCode(this)
            // If that still does not match the desired language, update the resource locale.
            if (latestLanguageCode != desiredLanguageCode) {
                // This should not happen, since the resource locale is already updated once changed by the user.
                LanguageUtility.setResourceLocale(this, desiredLanguageCode)
            }
            // The language has changed, restart this activity to apply:
            Log.d("SudoQLanguage", "Restarting activity '" + javaClass.simpleName + "', cause the language changed: " + currentLanguageCode + " -> " + desiredLanguageCode)
            restartThisActivity()
        }
    }

    /**
     * Simply restarts this activity, while trying to not show an animation.
     * This is an extra method, so that child classes can override it. Might be required to pass data along with the [Intent].
     */
    protected fun restartThisActivity() {
        val intent = Intent(this, javaClass)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        finish()
        startActivity(intent)
        overridePendingTransition(0, 0)
    }
}