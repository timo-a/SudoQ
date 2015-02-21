package de.sudoq.view;

/**
 * Ein Interface für eine zoombare View, die einem FullScrollLayout als Child übergeben werden kann.
 */
public interface ZoomableView {
	/**
	 * Setzt den aktuellen Zoom-Faktor für diese View und refresht sie.
	 * 
	 * @param factor
	 *            Der Zoom-Faktor
	 * @return Gibt zurück, ob das Zoom-Event verarbeitet wurde
	 */
	public boolean zoom(float factor);
	
	/**
	 * Gibt den minimalen Zoomfaktor für die View zurück.
	 * @return den minimalen Zoomfaktor für die View
	 */
	public float getMinZoomFactor();
	
	/**
	 * Gibt den maximalen Zoomfaktor für die View zurück.
	 * @return den maximalen Zoomfaktor für die View
	 */
	public float getMaxZoomFactor();
}
