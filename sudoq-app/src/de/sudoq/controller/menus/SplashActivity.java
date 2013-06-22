/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Haiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.controller.menus;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import de.sudoq.R;
import de.sudoq.controller.SudoqActivity;
import de.sudoq.model.files.FileManager;
import de.sudoq.model.profile.Profile;
import de.sudoq.model.sudoku.sudokuTypes.SudokuTypes;

/**
 * Eine Splash Activity für die SudoQ-App, welche einen Splash-Screen zeigt,
 * sowie den FileManager initialisiert und die Daten für den ersten Start
 * vorbereitet.
 */
public class SplashActivity extends SudoqActivity {
	/**
	 * Das Log-Tag für das LogCat.
	 */
	private static final String LOG_TAG = SplashActivity.class.getSimpleName();

	/**
	 * Konstante für das Speichern der bereits gewarteten Zeit.
	 */
	private static final int SAVE_WAITED = 0;

	/**
	 * Konstante für das Speichern des bereits begonnenen Kopiervorgangs.
	 */
	private static final int SAVE_STARTED_COPYING = 1;

	/**
	 * Die minimale Anzeigedauer des SplashScreens
	 */
	public static int splashTime = 2500;

	/**
	 * Die Zeit, die schon vergangen ist, seit der SplashScreen gestartet wurde
	 */
	private int waited;

	/**
	 * Besagt, ob der Kopiervorgang der Templates bereits gestartet wurde
	 */
	private boolean startedCopying;

	/**
	 * Der SplashThread
	 */
	private Thread splashThread;

	/**
	 * Indicates the version of the app when the templates have been updated for
	 * the last time. In some cases the sharedpreferences file seems to remain
	 * after removal of the app. So the new app sees no need to copy the
	 * templates, because 'Initialized is still true. If you want an update to
	 * update/manipulate the templates-files, advance the value to the current
	 * version
	 */
	private final static String VERSION_VALUE = "1.0.4";// extra variable.

	private final static String INIIALIZED_TAG = "Initialized";

	private final static String VERSION_TAG = "version";

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.splash);

		// If there is no profile initialize one
		if (FileManager.getNumberOfProfiles() == 0) {
			Profile.getInstance().setName(getString(R.string.default_user_name));
		}

		// Restore waited time after interruption or set it to 0
		if (savedInstanceState == null) {
			this.waited = 0;
		} else {
			this.waited = savedInstanceState.getInt(SAVE_WAITED + "");
			this.startedCopying = savedInstanceState.getBoolean(SAVE_STARTED_COPYING + "");
		}

		// Get the preferences and look if assets where completely copied before
		SharedPreferences settings = getSharedPreferences("Prefs", 0);

		// Debugging: see whats in shared preferences
		// Map<String, ?> m = settings.getAll();
		// settings.edit().clear().commit();

		/* if app is opened for 2nd time, no need to copy templates */
		Boolean init = settings.getBoolean(INIIALIZED_TAG, false);
		/*
		 * in case of an update which is to introduce new template-files, if the
		 * sharedpref-file survives, initialized will still be set, so check for
		 * a new VERSION_VALUE
		 */
		Boolean updateSituation = !settings.getString(VERSION_TAG, "wrongVersion").equals(VERSION_VALUE); //

		if (!init && !this.startedCopying || updateSituation) {
			new Initialization().execute(null, null, null);
			startedCopying = true;
		}

		this.splashThread = new Thread() {
			@Override
			public void run() {
				try {
					while ((waited < splashTime)) {
						sleep(100);
						if (waited < splashTime) {
							waited += 100;
						}
					}
					goToMainMenu();
				} catch (InterruptedException e) {
					// do nothing
				}
			}
		};
		splashThread.start();
	}

	/**
	 * Speichert die bereits im Splash gewartete Zeit.
	 */
	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putInt(SAVE_WAITED + "", this.waited);
		outState.putBoolean(SAVE_STARTED_COPYING + "", this.startedCopying);
	}

	/**
	 * Im Splash wird kein Optionsmenü angezeigt.
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		return true;
	}

	/**
	 * {@inheritdoc}
	 */
	@Override
	public void onPause() {
		super.onPause();
	}

	/**
	 * {@inheritdoc}
	 */
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		splashThread.interrupt();
		finish();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	}

	/**
	 * Gibt den Benutzernamen des GoogleAccounts zurück.
	 * 
	 * @return Der Benutzername des Google-Accounts
	 */
	@SuppressWarnings("unused")
	private String getUserName() {
		AccountManager manager = AccountManager.get(this);
		Account[] accounts = manager.getAccountsByType("com.google");
		List<String> possibleEmails = new LinkedList<String>();

		Log.d(LOG_TAG, "# of accounts: " + accounts.length);

		for (Account account : accounts) {
			possibleEmails.add(account.name);
			Log.d(LOG_TAG, "got account-name: " + account.name);
		}

		if (!possibleEmails.isEmpty() && possibleEmails.get(0) != null) {
			return possibleEmails.get(0).substring(0, possibleEmails.get(0).indexOf("@"));
		}

		return null;
	}

	/**
	 * Wechselt in die MainMenu-Activity
	 */
	private void goToMainMenu() {
		finish();
		// overridePendingTransition(android.R.anim.fade_in,
		// android.R.anim.fade_out);
		Intent startMainMenuIntent = new Intent(this, MainActivity.class);
		// startMainMenuIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		startActivity(startMainMenuIntent);
		// overridePendingTransition(android.R.anim.fade_in,
		// android.R.anim.fade_out);
	}

	/**
	 * Ein AsyncTask zur Initialisierung des Benutzers und der Vorlagen für den
	 * ersten Start.
	 */
	private class Initialization extends AsyncTask<Void, Void, Void> {
		@Override
		public void onPostExecute(Void v) {
			SharedPreferences settings = getSharedPreferences("Prefs", 0);
			settings.edit().putBoolean(INIIALIZED_TAG, true).commit();
			settings.edit().putString(VERSION_TAG, VERSION_VALUE).commit();
			Log.d(LOG_TAG, "Assets completely copied");
		}

		/**
		 * Kopiert alle Sudoku Vorlagen.
		 */
		private void copyAssets() {
			copyDirectory("sudokus");
		}

		/**
		 * Kopiert den Ordner mit dem angegebenen Pfad.
		 * 
		 * @param relPath
		 *            Der relative Pfad
		 */
		private void copyDirectory(String relPath) {
			AssetManager assetManager = getAssets();
			String[] files = null;
			try {
				files = assetManager.list(relPath);
				/*
				 * the user will try sudoku9x9 first => make it first in line to
				 * copy!
				 */
				if (relPath.equals("sudokus"))
					for (int i = 0; i < files.length; i++)
						if (files[i].equals(SudokuTypes.standard9x9.toString())) {
							String tmp = files[0];
							files[0] = files[i];
							files[i] = tmp;
							break;
						}

			} catch (IOException e) {
				Log.e(LOG_TAG, e.getMessage());
			}

			for (String filename : files) {
				String subFilePath = relPath + File.separator + filename;
				// sudoku pre path must be removed why cut off every time
				// instead off not prepending in first place?
				File subFile = new File(FileManager.getSudokuDir().getAbsolutePath(), subFilePath.substring(8));

				if (/* !subFile.exists() && */!subFile.isDirectory()) {
					InputStream in = null;
					OutputStream out = null;
					try {
						in = assetManager.open(subFilePath);
						out = new FileOutputStream(subFile.getAbsolutePath());
						copyFile(in, out);
						in.close();
						out.flush();
						out.close();
					} catch (Exception e) {
						Log.e(LOG_TAG, e.getMessage());
					}
				} else {
					copyDirectory(subFilePath);
				}
			}
		}

		/**
		 * Kopiert die Dateie zwischen den angegeben Streams
		 * 
		 * @param in
		 *            Der Eingabestream
		 * @param out
		 *            Der Ausgabestream
		 * @throws IOException
		 *             Wird geworfen, falls beim Lesen/Schreiben der Streams ein
		 *             Fehler auftritt
		 */
		private void copyFile(InputStream in, OutputStream out) throws IOException {
			byte[] buffer = new byte[1024];
			int read;
			while ((read = in.read(buffer)) != -1) {
				out.write(buffer, 0, read);
			}
		}

		@Override
		protected Void doInBackground(Void... params) {
			Log.d(LOG_TAG, "Starting to copy templates");
			copyAssets();
			return null;
		}

	}
}
