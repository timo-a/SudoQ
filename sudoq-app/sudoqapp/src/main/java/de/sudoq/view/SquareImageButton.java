package de.sudoq.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageButton;

public class SquareImageButton extends ImageButton {

	public SquareImageButton(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int temp = Math.max(widthMeasureSpec, heightMeasureSpec);
		super.onMeasure(temp, temp); 
	}
	
	
}
