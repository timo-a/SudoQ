/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

/**
 * Eine ScrollView, welche sowohl horizontales, als auch vertikales Scrollen
 * ermöglicht.
 */
public class FullScrollLayout extends LinearLayout {
	/**
	 * Der Log-Tag
	 */
	private static final String LOG_TAG = FullScrollLayout.class.getSimpleName();

	/**
	 * Der aktuelle Zoom Faktor.
	 */
	private float zoomFactor;

	/**
	 * Der View, der horizontales Scrollen erlaubt und im View für vertikales
	 * Scrollen enthalten ist.
	 */
	private HorizontalScroll horizontalScrollView;

	/**
	 * Der View, der vertikales Scrollen erlaubt und den View für horizontales
	 * Scrollen enthällt.
	 */
	private VerticalScroll verticalScrollView;

	/**
	 * Aktueller X- und Y-Wert der ScrollViews.
	 */
	private Point current=new Point(0,0);

	/**
	 * Der Zoom-Gesten-Detektor
	 */
	private ScaleGestureDetector scaleGestureDetector;

	/**
	 * Die View, die sich in diesem FullScrollLayout befindet.
	 */
	private ZoomableView childView;
	
	
	/**
	 * Instanziiert ein neues ScrollLayout mit den gegebenen Parametern
	 * 
	 * @param context
	 *            der Applikationskontext
	 * @param set
	 *            das Android AttributeSet
	 */
	public FullScrollLayout(Context context, AttributeSet set) {
		super(context, set);
		initialize();
		this.scaleGestureDetector = new ScaleGestureDetector(context, new ScaleGestureListener());
	}

	/**
	 * Instanziiert ein neues FullScrollLayout, welches auf Wunsch als
	 * qudratisch festgelegt wird.
	 * 
	 * @param context
	 *            Der Kontext, in dem dieses Layout angelegt wird
	 */
	public FullScrollLayout(Context context) {
		super(context);
		initialize();
		this.scaleGestureDetector = new ScaleGestureDetector(context, new ScaleGestureListener());
	}

	/**
	 * Initialisiert ein neues Layout.
	 */
	private void initialize() {
		this.removeAllViews();

		if (this.zoomFactor == 0) {
			this.zoomFactor = 1.0f;
		}

		this.  verticalScrollView = new   VerticalScroll(getContext());
		this.horizontalScrollView = new HorizontalScroll(getContext());

		this.verticalScrollView.addView(this.horizontalScrollView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		this.addView(this.verticalScrollView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
	}

	/**
	 * Fügt eine View zu diesem Layout hinzu. Ist bereits eine vorhanden, so
	 * wird diese gelöscht. Die spezifizierte View muss ZoomableView implementiere, sonst wird nichts getan.
	 */
	@Override
	public void addView(View v) {
		if (v instanceof ZoomableView) {
			this.horizontalScrollView.removeAllViews();
			this.childView = (ZoomableView) v;
			this.horizontalScrollView.addView(v, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		}
	}

	/**
	 * Verarbeitet TouchEvents mit einem Finger, also klicken und scrollen.
	 * 
	 * @param event
	 *            Das MotionEvent
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getPointerCount() == 1) {//just one finger -> scroll
			this.horizontalScrollView.performTouch(event);
			this.  verticalScrollView.performTouch(event);
		} else if (this.childView != null) {//2 or more fingers
			this.scaleGestureDetector.onTouchEvent(event);
		}
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void scrollTo(int x, int y) {
		// Has to be changed because the tree algo uses different coords.
		current.x = x - getWidth()  / 2;
		current.y = y - getHeight() / 2; //so apparently "tree algo" needs this... it is parameter are passed with inverse transformation in onscale, to even it out

		this.verticalScrollView.post(new Runnable() {
			public void run() {
				verticalScrollView.scrollTo((int) current.x, (int) current.y);
				current.y = verticalScrollView.getScrollY();
			}
		});
		this.horizontalScrollView.post(new Runnable() {
			public void run() {
				horizontalScrollView.scrollTo((int) current.x, (int) current.y);
				current.x = horizontalScrollView.getScrollX();
			}
		});
	}

	/**
	 * Setzt den Zoom zurück.
	 */
	public void resetZoom() {
		this.childView.zoom(1.0f);
		setZoomFactor(1.0f);
		scrollTo(0, 0);
	}

	/**
	 * Gibt den aktuellen Zoom-Faktor dieses Layouts zurück.
	 * 
	 * @return Der aktuelle Zoom-Faktor
	 */
	public float getZoomFactor() {
		return this.zoomFactor;
	}

	/**
	 * Setzt den aktuellen Zoom-Faktor des Layouts.
	 * 
	 * @param newZoom
	 *            Der neue Zoomfaktor
	 */
	public void setZoomFactor(float newZoom) {
		this.zoomFactor = newZoom;
	}

	/**
	 * Gibt den aktuell gescrollten X-Wert zurück.
	 * 
	 * @return der aktuell gescrollte X-Wert
	 */
	public float getScrollValueX()
	{
		return this.current.x;
	}

	/**
	 * Gibt den aktuell gescrollten Y-Wert zurück.
	 * 
	 * @return der aktuell gescrollte Y-Wert
	 */
	public float getScrollValueY()
	{
		return this.current.y;
	}

	/**
	 * Diese Klasse überschreibt das onTouch-Event der ScrollView, sodass dieses
	 * an dieses FullScrollLayout weitergereicht wird. Durch die
	 * performTouch-Methode kann das Event zurückgereicht werden.
	 */
	private class VerticalScroll extends ScrollView {
		/**
		 * Instanziiert eine neue vertikale ScrollView.
		 * 
		 * @param context
		 *            Der Kontext
		 */
		public VerticalScroll(Context context) {
			super(context);
			setVerticalScrollBarEnabled(false);
			setHorizontalScrollBarEnabled(false);
		}

		@Override
		public boolean onTouchEvent(MotionEvent event) {
			return false;
		}

		/**
		 * Für das übergebene Touch-Event aus.
		 * 
		 * @param event
		 *            Das auszuführende Touch-Event
		 */
		public void performTouch(MotionEvent event) {
			try {
				event.getX();
				event.getY();
				super.onTouchEvent(event);
				current.y = this.getScrollY();
			} catch (Exception e) {
				// Old android versions sometimes throw an exception when
				// putting and Event of one view in the onTouch of
				// another view. We just catch that and do nothing
			}
		}
	}

	/**
	 * Diese Klasse überschreibt das onTouch-Event der HorizontalScrollView,
	 * sodass dieses an dieses FullScrollLayout weitergereicht wird. Durch die
	 * performTouch-Methode kann das Event zurückgereicht werden.
	 */
	private class HorizontalScroll extends HorizontalScrollView {
		/**
		 * Instanziiert eine neue horizontale ScrollView.
		 * 
		 * @param context
		 *            Der Kontext
		 */
		public HorizontalScroll(Context context) {
			super(context);
			setVerticalScrollBarEnabled(false);
			setHorizontalScrollBarEnabled(false);
		}

		@Override
		public boolean onTouchEvent(MotionEvent event) {
			return false;
		}

		/**
		 * Für das übergebene Touch-Event aus.
		 * 
		 * @param event
		 *            Das auszuführende Touch-Event
		 */
		public void performTouch(MotionEvent event) {
			try {
				event.getX();
				event.getY();
				super.onTouchEvent(event);
				current.x = this.getScrollX();
			} catch (Exception e) {
				// Old android versions sometimes throw an exception when
				// putting and Event of one view in the onTouch of
				// another view. We just catch that and do nothing
			}
		}
	}

	private class ScaleGestureListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {

		Point focus;
		@Override
		public boolean onScale(ScaleGestureDetector detector) {
			/* TODO still buggy, if sudokuLayout.maxzoom is unrestrained focuspoint appears ~1 cell next to where it is supposed to be. try out by painting focus point in hintpainter */
			if(detector.getScaleFactor() < 0.01) return false;

			/* compute the new absolute zoomFactor (∈ [1,2]) by multiplying the old one with the scale Factor*/
			float scaleFactor = detector.getScaleFactor();
			float newZoom = zoomFactor * scaleFactor;

			// Don't let the object get too large/small.
			float lowerLimit = childView.getMinZoomFactor();
			float upperLimit = childView.getMaxZoomFactor();
			newZoom = Math.max(Math.min(newZoom, upperLimit), lowerLimit);//ensure newZoom ∈ [lowerLim,upperLim]
			
			if (!childView.zoom(newZoom)) {	return false; }
			
			zoomFactor = newZoom;

			/* NB: we scale in comparison to the case zoom = 1.0, not in comparison to the current one
			 * if we just scale everything on the canvas, fp (focusPoint) and tl (i.e. topleft corner of window) are out of sync.
			 * especially tl is initially 0,0 so it doesn't scale anywhere...
			 *
			 * in the normalized case fp-tl is fp (bec tl==0)
			 * we want that distance to be kept so we subtract it from the scaled value for fp namely fp* zoom.
			 * hence lt = focus * zoom - focus
			 * */
			current.x = focus.x * zoomFactor - focus.x;
			current.y = focus.y * zoomFactor - focus.y;
			/* even out the transformation on scrollTo */
			transform(current);
			scrollTo2(current);
			Log.d(LOG_TAG, "Scaled to: "+current.toString()+"    "
					      +"Focus: "    +focus  .toString()+"   "
					      +"zoom: "+zoomFactor);
			return true;
		}

		@Override
		public boolean onScaleBegin(ScaleGestureDetector scaleGestureDetector) {
			Log.d(LOG_TAG, "On scale begin-------------------------(");
			focus = new Point(scaleGestureDetector.getFocusX()
			                 ,scaleGestureDetector.getFocusY());
			((SudokuLayout)childView).focusX = focus.x;//only for debug
			((SudokuLayout)childView).focusY = focus.y;
			return true;
		}

		@Override
		public void onScaleEnd(ScaleGestureDetector scaleGestureDetector) {
			Log.d(LOG_TAG, "On scale end----------------)");
		}

		/* transform to even out inverse transformation in scrollTo(x,y). A trafo exists there because of "tree algo" */
		private void transform(Point p){
			p.x += getWidth()  / 2;
			p.y += getHeight() / 2;
		}

		private void scrollTo2(Point p){
			/* save for log */
			current.x = p.x;
			current.y = p.y;
			scrollTo((int)p.x, (int)p.y);
		}



	}
	private class Point{
		float x,y;
		public Point(float x, float y){
			this.x=x;
			this.y=y;
		}
		public String toString(){return (int)x+","+(int)y;}
	}
}
