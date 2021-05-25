package de.sudoq.view

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageButton

class SquareImageButton(context: Context?, attrs: AttributeSet?) : ImageButton(context, attrs) {
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val temp = Math.max(widthMeasureSpec, heightMeasureSpec)
        super.onMeasure(temp, temp)
    }
}