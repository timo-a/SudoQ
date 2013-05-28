package de.sudoq.test;

import android.test.ActivityInstrumentationTestCase2;

import com.jayway.android.robotium.solo.Solo;

import de.sudoq.R;
import de.sudoq.controller.menus.MainActivity;
import de.sudoq.controller.menus.SplashActivity;

/**
 * Diese Klasse stellt eine basis für alle unsere Robotium Tests dar. Sie stellt sicher, dass die App richtig
 * initialisiert ist und dass für jeden Test ein sauberes neues Profil genutzt wird. Jeder Test startet im Hauptmenü
 */
public class SudoqTestCase extends ActivityInstrumentationTestCase2<SplashActivity> {
	protected Solo solo;

	public SudoqTestCase() {
		super("de.sudoq", SplashActivity.class);
	}

	public void setUp() {
		solo = new Solo(getInstrumentation(), getActivity());
		int save = SplashActivity.splashTime;
		SplashActivity.splashTime = 0;
		// wait for initialization to finish
		while (solo.getCurrentActivity().getClass() == SplashActivity.class) {
			solo.sleep(100);
		}
		SplashActivity.splashTime = save;

		/* AT 150 (create profile) */
		solo.assertCurrentActivity("Should be MainAcitivty", MainActivity.class);
		solo.clickOnButton(solo.getCurrentActivity().getString(R.string.sf_mainmenu_profile));
		solo.sendKey(Solo.MENU);
		solo.clickOnText(solo.getCurrentActivity().getString(R.string.profile_preference_title_createprofile));
		solo.goBack();
	}

	@Override
	public void tearDown() {
		solo.goBackToActivity("MainActivity");
		solo.waitForActivity("MainActivity",15000);
		solo.clickOnButton(solo.getString(R.string.sf_mainmenu_profile));//faster?
		solo.sendKey(Solo.MENU);
		solo.clickOnText(solo.getCurrentActivity().getString(R.string.profile_preference_title_deleteprofile));
		solo.finishOpenedActivities();
	}
}