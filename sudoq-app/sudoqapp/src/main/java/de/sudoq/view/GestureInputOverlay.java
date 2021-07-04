package de.sudoq.view;

import android.content.Context;
import android.gesture.GestureOverlayView;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;

import de.sudoq.R;

public class GestureInputOverlay extends GestureOverlayView {

    private TextView title;

    public GestureInputOverlay(Context context) {
        super(context);
        LayoutParams gestureLayoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        setLayoutParams(gestureLayoutParams);
        setBackgroundColor(Color.BLACK);
        getBackground().setAlpha(127);
        setVisibility(View.INVISIBLE);

        setGestureStrokeType(GestureOverlayView.GESTURE_STROKE_TYPE_MULTIPLE);


        title = new TextView(getContext());
        title.setTextColor(Color.YELLOW);
        title.setTextSize(18);
    }

    public void activateForEntry() {
        String noteTitle = getContext().getString(R.string.sf_sudoku_title_gesture_input_entry);
        setTitle(" " + noteTitle + " ");
        setVisibility(View.VISIBLE);
    }

    public void activateForNote() {
        String noteTitle = getContext().getString(R.string.sf_sudoku_title_gesture_input_note);
        setTitle(" " + noteTitle + " ");
        setVisibility(View.VISIBLE);
    }

    public void setTitle(String title) {
        removeView(this.title);
        this.title.setText(title);
        LayoutParams layoutParams = new GestureOverlayView.LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT,
                Gravity.CENTER_HORIZONTAL);
        addView(this.title, layoutParams);
    }


}
