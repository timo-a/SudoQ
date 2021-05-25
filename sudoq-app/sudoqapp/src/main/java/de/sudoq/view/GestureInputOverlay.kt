package de.sudoq.view

import android.content.Context
import android.gesture.GestureOverlayView
import android.graphics.Color
import android.view.Gravity
import android.widget.TextView
import de.sudoq.R

class GestureInputOverlay(context: Context?) : GestureOverlayView(context) {
    private val title: TextView
    fun activateForEntry() {
        val noteTitle = context.getString(R.string.sf_sudoku_title_gesture_input_entry)
        setTitle(" $noteTitle ")
        visibility = VISIBLE
    }

    fun activateForNote() {
        val noteTitle = context.getString(R.string.sf_sudoku_title_gesture_input_note)
        setTitle(" $noteTitle ")
        visibility = VISIBLE
    }

    fun setTitle(title: String?) {
        removeView(this.title)
        this.title.text = title
        val layoutParams = LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT,
                Gravity.CENTER_HORIZONTAL)
        addView(this.title, layoutParams)
    }

    init {
        val gestureLayoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        layoutParams = gestureLayoutParams
        setBackgroundColor(Color.BLACK)
        background.alpha = 127
        visibility = INVISIBLE
        gestureStrokeType = GESTURE_STROKE_TYPE_MULTIPLE
        title = TextView(getContext())
        title.setTextColor(Color.YELLOW)
        title.textSize = 18f
    }
}