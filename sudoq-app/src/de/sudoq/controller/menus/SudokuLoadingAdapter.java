/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Haiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.controller.menus;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.TimeZone;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import de.sudoq.R;
import de.sudoq.model.files.FileManager;
import de.sudoq.model.game.GameData;
import de.sudoq.model.sudoku.complexity.Complexity;
import de.sudoq.model.sudoku.sudokuTypes.SudokuTypes;

/**
 * Adapter für die Anzeige aller Spiele des Spielers
 */
public class SudokuLoadingAdapter extends ArrayAdapter<GameData> {
	private static final float THUMBNAIL_SIZE = 0.5f;
	private static final String LOG_TAG = SudokuLoadingAdapter.class.getSimpleName();
	private final Context context;
	private final List<GameData> gameDatas;
	private String[] typeStrings;
	private String[] complexityStrings;

	/**
	 * Erzeugt einen neuen SudokuLoadingAdpater mit den gegebenen Parametern
	 * 
	 * @param context
	 *            der Applikationskontext
	 * @param games
	 *            die Liste der games
	 */
	public SudokuLoadingAdapter(Context context, List<GameData> games) {
		super(context, R.layout.sudokuloadingitem, games);
		this.context = context;
		this.gameDatas = games;
		initialiseTypes();
		initialiseComplexities();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.sudokuloadingitem, parent, false);
		ImageView thumbnail = (ImageView) rowView.findViewById(R.id.sudoku_loading_item_thumbnail);
		TextView sudokuType = (TextView) rowView.findViewById(R.id.type_label);
		TextView sudokuComplexity = (TextView) rowView.findViewById(R.id.complexity_label);
		TextView sudokuTime = (TextView) rowView.findViewById(R.id.time_label);
		TextView sudokuState = (TextView) rowView.findViewById(R.id.state_label);

		File currentThumbnailFile = FileManager.getGameThumbnailFile(gameDatas.get(position).getId());
		try {
			Bitmap currentThumbnailBitmap = BitmapFactory.decodeStream(new FileInputStream(currentThumbnailFile));
			int thumbnailWidth = currentThumbnailBitmap.getWidth();
			int thumbnailHeight = currentThumbnailBitmap.getHeight();

			Matrix matrix = new Matrix();//identity matrix
			matrix.postScale(THUMBNAIL_SIZE, THUMBNAIL_SIZE);//scaled matrix
			
			Bitmap resizedBitmap = Bitmap.createBitmap(currentThumbnailBitmap, 0, 0, thumbnailWidth, thumbnailHeight, matrix, false);

			//Setting Layout of the ImageView
			LinearLayout.LayoutParams visibleLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			visibleLayoutParams.gravity = Gravity.CENTER;
			thumbnail.setLayoutParams(visibleLayoutParams);
			thumbnail.setImageBitmap(resizedBitmap);
			thumbnail.setVisibility(View.VISIBLE);
			
		} catch (FileNotFoundException e) {
			Log.w(LOG_TAG, this.context.getString(R.string.error_thumbnail_load));
		}
		
		sudokuType.setText(typeStrings[gameDatas.get(position).getType().ordinal()]);
		sudokuComplexity.setText(complexityStrings[gameDatas.get(position).getComplexity().ordinal()]);

		TimeZone tz = TimeZone.getDefault();
		SimpleDateFormat sdf = new SimpleDateFormat(context.getString(R.string.time_format));
		sdf.setTimeZone(tz);

		String date = sdf.format(gameDatas.get(position).getPlayedAt());

		sudokuTime.setText(date);

		if (gameDatas.get(position).isFinished()) {
			// TODO finished durch Haken ersetzen
			sudokuState.setText(context.getString(R.string.game_finished));
			sudokuType.setTextColor(Color.GRAY);
			sudokuComplexity.setTextColor(Color.GRAY);
			sudokuTime.setTextColor(Color.GRAY);
			sudokuState.setTextColor(Color.GRAY);
		}

		return rowView;
	}

	private void initialiseTypes() {
		typeStrings = new String[SudokuTypes.values().length];
		typeStrings[SudokuTypes.standard4x4.ordinal()] = context.getString(R.string.sudoku_type_standard_4x4);
		typeStrings[SudokuTypes.standard6x6.ordinal()] = context.getString(R.string.sudoku_type_standard_6x6);
		typeStrings[SudokuTypes.standard9x9.ordinal()] = context.getString(R.string.sudoku_type_standard_9x9);
		typeStrings[SudokuTypes.standard16x16.ordinal()] = context.getString(R.string.sudoku_type_standard_16x16);
		typeStrings[SudokuTypes.Xsudoku.ordinal()] = context.getString(R.string.sudoku_type_xsudoku);
		typeStrings[SudokuTypes.HyperSudoku.ordinal()] = context.getString(R.string.sudoku_type_hyper);
		typeStrings[SudokuTypes.stairstep.ordinal()] = context.getString(R.string.sudoku_type_stairstep_9x9);
		typeStrings[SudokuTypes.squigglya.ordinal()] = context.getString(R.string.sudoku_type_squiggly_a_9x9);
		typeStrings[SudokuTypes.squigglyb.ordinal()] = context.getString(R.string.sudoku_type_squiggly_b_9x9);
		typeStrings[SudokuTypes.samurai.ordinal()] = context.getString(R.string.sudoku_type_samurai);
		
	}

	private void initialiseComplexities() {
		complexityStrings = new String[Complexity.values().length];
		complexityStrings[Complexity.easy.ordinal()] = context.getString(R.string.complexity_easy);
		complexityStrings[Complexity.medium.ordinal()] = context.getString(R.string.complexity_medium);
		complexityStrings[Complexity.difficult.ordinal()] = context.getString(R.string.complexity_difficult);
		complexityStrings[Complexity.infernal.ordinal()] = context.getString(R.string.complexity_infernal);
	}
}