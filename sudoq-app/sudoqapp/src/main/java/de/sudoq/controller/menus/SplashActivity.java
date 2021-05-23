/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.controller.menus;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.sudoq.R;
import de.sudoq.controller.SudoqCompatActivity;
import de.sudoq.controller.menus.Utility;
import de.sudoq.model.files.FileManager;
import de.sudoq.model.profile.Profile;
import de.sudoq.model.profile.ProfileManager;
import de.sudoq.model.sudoku.complexity.Complexity;
import de.sudoq.model.sudoku.sudokuTypes.SudokuTypes;

/**
 * Eine Splash Activity für die SudoQ-App, welche einen Splash-Screen zeigt,
 * sowie den FileManager initialisiert und die Daten für den ersten Start
 * vorbereitet.
 */
public class SplashActivity extends SudoqCompatActivity {
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

	private final static String INITIALIZED_TAG = "Initialized";

	private final static String VERSION_TAG = "version";
	private final static String NO_VERSION_YET = "0.0.0";
	protected final static String NEWEST_ASSET_VERSION = "1.1.0b";

	private static String currentVersionName = "";


	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.splash);

		// If there is no profile initialize one

		ProfileManager pm = new ProfileManager(getDir(getString(R.string.path_rel_profiles), Context.MODE_PRIVATE));
		if (pm.noProfiles()) {
			pm.initialize();
			pm.setName(getString(R.string.default_user_name));
			pm.saveChanges();
		} else {
			pm.loadCurrentProfile();
		}
		//confirm that there is a profile
		File profileDir = pm.getProfilesDir();
		String[] filenames = profileDir.list();

		Log.d("ProfileD", "onCreate: after init: " + StringUtils.join(filenames, ", "));
		if(filenames.length < 2)
			throw new IllegalStateException("Too few files. initialization was not successfull");

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
		try {
			currentVersionName = this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			Log.v(LOG_TAG, e.getMessage());
		}

		/* is this a new version? */
		String oldVersionName = settings.getString(VERSION_TAG, NO_VERSION_YET);

		if (updateSituation(oldVersionName) && !this.startedCopying) {
				
			/*hint*/
			alertIfNoAssetFolder();
			
			Log.v(LOG_TAG, "we will do an initialization");
			new Initialization().execute(null, null, null);
			startedCopying = true;
		}else
			Log.v(LOG_TAG, "we will not do an initialization");

		/* splash thread*/
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

	/* Specifies whether this is a regular start or an assets-update,
	 * i.e. version has changed and assets have to be copied
	 *
	 * 'protected' for unit test
	 */
	protected boolean updateSituation(String oldVersionName) {
		Boolean updateSituation;
		try {
			updateSituation = older(oldVersionName, NEWEST_ASSET_VERSION);
		}catch(Exception e){
			updateSituation = true; //when in doubt DO an update!
		}
		return updateSituation;
	}


	/** is version a older than b?
     * a,b = "12.68.87(abc..)"  **/
	boolean older(String a, String b) throws Exception {
		int[] aTokenized = versionToNumbers(a);
		int[] bTokenized = versionToNumbers(b);
		assert aTokenized.length == bTokenized.length;

		for(int i=0; i< aTokenized.length; i++){
			int aTok = aTokenized[i];
			int bTok = bTokenized[i];

			if(aTok < bTok)
				return true;
			else if(aTok > bTok)
				return false;
		}

		return false;
	}

	private int[] versionToNumbers (String version) throws Exception {
		String pattern = "(\\d+)[.](\\d+)[.](\\d+)([a-z]?)";
		Pattern r = Pattern.compile(pattern);
		Matcher m = r.matcher(version);
		m.find();

		int[] result = new int[4];

		if (m.groupCount()==4) {
			String letter = m.group(4);
			if (letter.length() == 1)
				result[3] = letter.charAt(0) - 'a' + 1;
		}

		for(int i : new int[]{1, 2, 3})
			result[i-1] = Integer.parseInt(m.group(i));


		return result;
	}


	private void alertIfNoAssetFolder(){
		try {
			String[] l = getAssets().list("");
			boolean foundSudokusInAssetfolder = Arrays.asList(l).contains(HEAD_DIRECTORY);
			//TODO make this work:
			//boolean fsaf = Stream.of(l).anyMatch(s -> s.equals(HEAD_DIRECTORY));
			if(!foundSudokusInAssetfolder){
				String msg =  "This app will probably crash once you try to start a new sudoku. "+
						"This is because the person who compiled this app forgot about the 'assets' folder. "+
						"Please tell him that!";
				Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
				Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
				Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
			}
			//Toast.makeText(this, l[0].equals(HEAD_DIRECTORY)+"", Toast.LENGTH_SHORT).show();

		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
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
		//setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
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
			settings.edit().putBoolean(INITIALIZED_TAG, true).commit();
			settings.edit().putString(VERSION_TAG, currentVersionName).commit();
			Log.d(LOG_TAG, "Assets completely copied");
		}

		/**
		 * Kopiert alle Sudoku Vorlagen.
		 */
		private void copyAssets() {
			/* sudoku types*/
			SudokuTypes[] types = SudokuTypes.values();
			/* ensure sudoku9x9 is first element ->  will be finished first.
			 * Reason: people will probably want to play 9x9 first
			 * kind of unnecessary because 9x9 is declared first in SudokuTypes, but maybe that will change*/
			types = swap99tothefront(types);
			
			/* actual copying*/
            for (SudokuTypes t : types) {

				String sourceType = HEAD_DIRECTORY                               + File.separator + t.toString() + File.separator; // e.g. .../standard9x9/
				String targetType = FileManager.getSudokuDir().getAbsolutePath() + File.separator + t.toString() + File.separator;

				copyFile(sourceType + t.toString() + ".xml",
						 targetType + t.toString() + ".xml");



				for (Complexity c : Complexity.playableValues()) {

					String sourceComplexity = sourceType + c.toString() + File.separator;
					String targetComplexity = targetType + c.toString() + File.separator;

					String[] fnames =getSubfiles(sourceType + c.toString());
					for (String filename: fnames){
                        copyFile( sourceComplexity + filename,
		                          targetComplexity + filename);
					}
				}
			}
		}

		private SudokuTypes[] swap99tothefront(SudokuTypes[] types){
			if(types[0]!=SudokuTypes.standard9x9) {
				/* find index */
				int pos9x9;
				for (pos9x9=0; pos9x9 < types.length; pos9x9++)
					if (types[pos9x9] == SudokuTypes.standard9x9)
						break;

				/* swap */
				types[pos9x9] = types[0];
				types[0] = SudokuTypes.standard9x9;
			}
			return types;
		}


		/** apache FileUtils.copyFile does not work!!! */


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

		/**TODO  do shorter with library
		 * Copies content from sourcePath to destination
		 *
		 * @param sourcePath
		 * @param destinationPath
		 */
		private void copyFile(String sourcePath, String destinationPath) {
			new File(destinationPath).getParentFile().mkdirs();

			File destination = new File(destinationPath);
			InputStream in;
			OutputStream out;
			try {
				in = getAssets().open(sourcePath);
				String abs = destination.getAbsolutePath();
				out = new FileOutputStream(abs);
				Utility.copyFileOnStreamLevel(in, out);
				in.close();
				out.flush();
				out.close();
			} catch (Exception e) {
				Log.e(LOG_TAG, e.getMessage());
				Log.e(LOG_TAG, "there seems to be an exception");
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
