/*
 * SudoQ is a Sudoku-App for Android Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.controller.menus

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Matrix
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import de.sudoq.R
import de.sudoq.model.game.GameData
import de.sudoq.persistence.game.GameRepo
import de.sudoq.model.profile.ProfileManager
import de.sudoq.persistence.profile.ProfileRepo
import de.sudoq.persistence.profile.ProfilesListRepo
import de.sudoq.persistence.sudokuType.SudokuTypeRepo
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Adapter für die Anzeige aller Spiele des Spielers
 *
 * @property @param games die Liste der games
 */
class SudokuLoadingAdapter(context: Context, private val gameDatas: List<GameData>) :
    ArrayAdapter<GameData?>(context, R.layout.sudokuloadingitem, gameDatas) {
    //todo make non nullable

    private val profilesDir = context.getDir(context.getString(R.string.path_rel_profiles), AppCompatActivity.MODE_PRIVATE)
    private val sudokuDir = context.getDir(context.getString(R.string.path_rel_sudokus), AppCompatActivity.MODE_PRIVATE)
    private val sudokuTypeRepo = SudokuTypeRepo(sudokuDir)


    /**
     * {@inheritDoc}
     */
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val rowView = inflater.inflate(R.layout.sudokuloadingitem, parent, false)
        val thumbnail = rowView.findViewById<View>(R.id.sudoku_loading_item_thumbnail) as ImageView
        val sudokuType = rowView.findViewById<View>(R.id.type_label) as TextView
        val sudokuComplexity = rowView.findViewById<View>(R.id.complexity_label) as TextView
        val sudokuTime = rowView.findViewById<View>(R.id.time_label) as TextView
        val sudokuState = rowView.findViewById<View>(R.id.state_label) as TextView
        val pm = ProfileManager(profilesDir, ProfileRepo(profilesDir), ProfilesListRepo(profilesDir))
        pm.loadCurrentProfile()
        val gameRepo = GameRepo(pm.profilesDir!!, pm.currentProfileID, sudokuTypeRepo)
        val currentThumbnailFile = gameRepo.getGameThumbnailFile(gameDatas[position].id)
        try {
            val currentThumbnailBitmap =
                BitmapFactory.decodeStream(FileInputStream(currentThumbnailFile))
            val thumbnailWidth = currentThumbnailBitmap.width
            val thumbnailHeight = currentThumbnailBitmap.height
            val matrix = Matrix() //identity matrix
            matrix.postScale(THUMBNAIL_SIZE, THUMBNAIL_SIZE) //scaled matrix
            val resizedBitmap = Bitmap.createBitmap(
                currentThumbnailBitmap,
                0,
                0,
                thumbnailWidth,
                thumbnailHeight,
                matrix,
                false
            )

            //Setting Layout of the ImageView
            val visibleLayoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            visibleLayoutParams.gravity = Gravity.CENTER
            thumbnail.layoutParams = visibleLayoutParams
            thumbnail.setImageBitmap(resizedBitmap)
        } catch (e: FileNotFoundException) {
            Log.w(LOG_TAG, context.getString(R.string.error_thumbnail_load))
        } catch (e: OutOfMemoryError) {
            Toast.makeText(context, context.getString(R.string.toast_stop_that), Toast.LENGTH_LONG)
                .show()
            (context as SudokuLoadingActivity).finish()
        }
        sudokuType.text = Utility.type2string(context, gameDatas[position].type)
        sudokuComplexity.text = Utility.complexity2string(context, gameDatas[position].complexity)
        val tz = TimeZone.getDefault()
        val sdf = SimpleDateFormat(context.getString(R.string.time_format))
        sdf.timeZone = tz
        val date = sdf.format(gameDatas[position].playedAt)
        sudokuTime.text = date
        if (gameDatas[position].isFinished) {
            sudokuState.text = context.getString(R.string.check_mark)
            sudokuState.setTextColor(Color.GREEN)
            sudokuType.setTextColor(Color.GRAY)
            sudokuComplexity.setTextColor(Color.GRAY)
            sudokuTime.setTextColor(Color.GRAY)
        } else {
            sudokuState.text = ""
        }
        return rowView
    }

    companion object {
        private const val THUMBNAIL_SIZE = 0.5f
        private val LOG_TAG = SudokuLoadingAdapter::class.java.simpleName
    }
}