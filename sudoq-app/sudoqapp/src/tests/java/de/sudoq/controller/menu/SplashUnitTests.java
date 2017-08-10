package de.sudoq.test;

/**
 * Created by timo on 11.05.17.
 */

import org.junit.Test;

import de.sudoq.controller.menus.SplashActivity;

import static org.junit.Assert.assertTrue;


public class SplashUnitTests extends SplashActivity {

    @Test
	public void testInit() {
		assertTrue(updateSituation("dummy"));

	}
}
