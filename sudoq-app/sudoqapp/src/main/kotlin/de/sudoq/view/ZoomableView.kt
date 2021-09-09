package de.sudoq.view

/**
 * Ein Interface für eine zoombare View, die einem FullScrollLayout als Child übergeben werden kann.
 */
interface ZoomableView {
    /**
     * Setzt den aktuellen Zoom-Faktor für diese View und refresht sie.
     *
     * @param factor
     * Der Zoom-Faktor
     * @return Gibt zurück, ob das Zoom-Event verarbeitet wurde
     */
    fun zoom(factor: Float): Boolean

    /**
     * Gibt den minimalen Zoomfaktor für die View zurück.
     * @return den minimalen Zoomfaktor für die View
     */
    fun getMinZoomFactor(): Float

    /**
     * Gibt den maximalen Zoomfaktor für die View zurück.
     * @return den maximalen Zoomfaktor für die View
     */
    fun getMaxZoomFactor(): Float
}