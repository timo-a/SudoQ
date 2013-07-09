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
import java.util.HashSet;
import java.util.Set;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import de.sudoq.R;
import de.sudoq.controller.SudoqActivity;
import de.sudoq.model.files.FileManager;
import de.sudoq.model.profile.Profile;
import de.sudoq.model.sudoku.complexity.Complexity;
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

	private final static String HEAD_DIRECTORY = "sudokus";

	private final static String INIIALIZED_TAG = "Initialized";

	private final static String VERSION_TAG = "version";
	private final static String NO_VERSION_YET = "0.0.0";

	private static String currentVersionValue = "";

	private Set<SudokuTypes> collectTypesToBeReplaced(String oldVersion) {
		Set<SudokuTypes> replacies = new HashSet<SudokuTypes>();

		String replaceVersion = "1.0.4";

		if (replaceVersion.compareTo(oldVersion) >= 0) {
			replacies.add(SudokuTypes.standard16x16);
			replacies.add(SudokuTypes.standard9x9);
		}
		/*
		 * other versions that replace templates !example! replaceVersion =
		 * 1.0.6 if(repl.cpT(oldVersion) >=0 ){
		 * 
		 * }
		 */

		return replacies;
	}

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

		/* get version value */
		try
		{
			currentVersionValue = this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName;
		} catch (NameNotFoundException e)
		{
			Log.v(LOG_TAG, e.getMessage());
		}

		/* is this a new version? */
		String oldVersionName = settings.getString(VERSION_TAG, NO_VERSION_YET);
		Boolean updateSituation = !oldVersionName.equals(currentVersionValue);

		if (updateSituation && !this.startedCopying) {
			Set<SudokuTypes> typesToBeReplaced = collectTypesToBeReplaced(oldVersionName);
			new Initialization(typesToBeReplaced).execute(null, null, null);
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

		private Set<SudokuTypes> typesToBeReplaced;

		public Initialization(Set<SudokuTypes> typesToBeReplaced) {
			this.typesToBeReplaced = typesToBeReplaced;
		}

		@Override
		public void onPostExecute(Void v) {
			SharedPreferences settings = getSharedPreferences("Prefs", 0);
			settings.edit().putBoolean(INIIALIZED_TAG, true).commit();
			settings.edit().putString(VERSION_TAG, currentVersionValue).commit();
			Log.d(LOG_TAG, "Assets completely copied");
		}

		/**
		 * Kopiert alle Sudoku Vorlagen.
		 */
		private void copyAssets() {
			/* copy sudoku9x9 first as people play it first */
			SudokuTypes[] types = SudokuTypes.values();
			for (int i = 0; i < types.length; i++)
				if (types[i].equals(SudokuTypes.standard9x9.toString())) {
					SudokuTypes tmp = types[0];
					types[0] = types[i];
					types[i] = tmp;
					break;
				}
			
			for (SudokuTypes t : types) {
				File typeDir = new File(FileManager.getSudokuDir().getAbsoluteFile() + File.separator + t.toString());
				Boolean notExistant = !typeDir.exists();
				Boolean needsReplace = typesToBeReplaced.contains(t);
				if (notExistant || needsReplace) {
					typeDir.mkdir();
				
					for (Complexity c : Complexity.playableValues()) {
						String assetsPath = HEAD_DIRECTORY + File.separator + t.toString() + File.separator + c.toString();
						String relPath = typeDir.getAbsolutePath() + File.separator + c.toString();
						File f = new File(relPath);
						f.mkdir();
						
						String[] fnames =getSubfiles(assetsPath);
						for (String filename: fnames){
							copyFile(assetsPath+File.separator+filename, t.toString() + File.separator + c.toString() + File.separator+filename);
						}
					}
				}
			}
		}

				/* get all files/directories in relPath */
		private String[] getSubfiles(String relPath) {
			String[] files = null;
			try {
				files = getAssets().list(relPath);
			} catch (IOException e) {
				Log.e(LOG_TAG, e.getMessage());
			}
			return files;
		}

		/**
		 * Copies content from sourcePath to destination
		 * 
		 * @param assetManager
		 * @param sourcePath
		 * @param destination
		 */
		private void copyFile(String sourcePath, String destinationPath) {
			File destination = new File(FileManager.getSudokuDir().getAbsolutePath(), destinationPath);
			InputStream in = null;
			OutputStream out = null;
			try {
				in = getAssets().open(sourcePath);
				String abs = destination.getAbsolutePath();
				out = new FileOutputStream(abs);
				copyFileOnStreamLevel(in, out);
				in.close();
				out.flush();
				out.close();
			} catch (Exception e) {
				Log.e(LOG_TAG, e.getMessage());
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
		private void copyFileOnStreamLevel(InputStream in, OutputStream out) throws IOException {
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
