/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
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

import android.view.Menu;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import de.sudoq.R;
import de.sudoq.controller.SudoqActivitySherlock;
import de.sudoq.model.files.FileManager;
import de.sudoq.model.profile.Profile;
import de.sudoq.model.sudoku.complexity.Complexity;
import de.sudoq.model.sudoku.sudokuTypes.SudokuTypes;

/**
 * Eine Splash Activity für die SudoQ-App, welche einen Splash-Screen zeigt,
 * sowie den FileManager initialisiert und die Daten für den ersten Start
 * vorbereitet.
 */
public class SplashActivity extends SudoqActivitySherlock {
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
	private final static String NEWEST_ASSET_VERSION = "1.0.6";

	private static String currentVersionValue = "";

	/* is version a older than b? 
	 * a,b = "x.y.z"  */
	boolean older(String a, String b){
		String[] aTokenized = a.split("[.]");
		String[] bTokenized = b.split("[.]");
		assert aTokenized.length == bTokenized.length;
				
		for(int i=0; i< aTokenized.length; i++){
			int aTok = Integer.parseInt(aTokenized[i]);
			int bTok = Integer.parseInt(bTokenized[i]);
			
			if(aTok < bTok)
				return true;
			else if(aTok > bTok)
				return false;
		}
		
		return false;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
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
		/* Specifies whether this is a regular start or an assets-update, i.e. version has changed and assets have to be copied*/
		Boolean updateSituation = older(oldVersionName, NEWEST_ASSET_VERSION);

		if (updateSituation && !this.startedCopying) {
				
			/*hint*/
			try {
				boolean foundSudokusinAssetfolder = false;
				String[] l = getAssets().list("");
				for(String s: l)
					if (s.equals(HEAD_DIRECTORY))
						foundSudokusinAssetfolder = true;
				if(!foundSudokusinAssetfolder){
					String msg =  "This app will probably crash once you try to start a new sudoku. "+
	                         "This is pecause the person who compiled this app forgot about the 'assets' folder. "+
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
			/*end of hint*/
			
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
			settings.edit().putBoolean(INIIALIZED_TAG, true).commit();
			settings.edit().putString(VERSION_TAG, currentVersionValue).commit();
			Log.d(LOG_TAG, "Assets completely copied");
		}

		/**
		 * Kopiert alle Sudoku Vorlagen.
		 */
		private void copyAssets() {
			/* sudoku types*/
			SudokuTypes[] types = SudokuTypes.values();
			
			/* swap sudoku9x9 with whatever comes at 0th position.
			 * -> sudoku9x9 will be finished first.
			 * Reason: people will probably want to play 9x9 first */
			//TODO superfluous, since 9x9 always first?
			for (int i = 0; i < types.length; i++)
				if (types[i] == SudokuTypes.standard9x9) {
					types[i] = types[0];
					types[0] = SudokuTypes.standard9x9;
					break;
				}
			
			/* actual copying*/ //apache FileUtils.copyDirectory does not work!
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
				copyFileOnStreamLevel(in, out);
				in.close();
				out.flush();
				out.close();
			} catch (Exception e) {
				Log.e(LOG_TAG, e.getMessage());
				Log.e(LOG_TAG, "there seems to be an exception");
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
