package de.sudoq.test;

import android.app.Activity;
import android.test.ActivityInstrumentationTestCase2;

import com.jayway.android.robotium.solo.Solo;

import de.sudoq.controller.menus.MainActivity;
import de.sudoq.controller.menus.SplashActivity;

public class SplashTest extends ActivityInstrumentationTestCase2<SplashActivity> {
	private Solo solo;

	public SplashTest() {
		super("de.sudoq", SplashActivity.class);
	}

	public void setUp() throws Exception {
		solo = new Solo(getInstrumentation(), getActivity());
	}

	public void testSplashFinish() throws Exception {
		/*
		 * ok this is kinda silly but it is a way to get the inizialization done
		 * so that the mainmenu wont throw exceptions. Just run the whole
		 * project to prevent this.
		 */
		Activity a = this.getActivity();
		// wait for initialization to finish
		while (solo.getCurrentActivity() == a) {
			solo.sleep(100);
		}

		solo.assertCurrentActivity("Should be MainAcitivty", MainActivity.class);
	}

	@Override
	public void tearDown() throws Exception {
		solo.finishOpenedActivities();
	}
}