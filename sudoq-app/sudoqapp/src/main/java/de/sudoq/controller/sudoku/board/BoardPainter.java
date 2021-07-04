package de.sudoq.controller.sudoku.board;

import android.graphics.Canvas;
import android.graphics.Paint;

import de.sudoq.model.sudoku.Constraint;
import de.sudoq.model.sudoku.ConstraintType;
import de.sudoq.model.sudoku.Position;
import de.sudoq.model.sudoku.sudokuTypes.SudokuType;
import de.sudoq.view.SudokuLayout;

/**
 * Created by timo on 13.10.16.
 */
public class BoardPainter {

    SudokuLayout sl;
    SudokuType type;

    public BoardPainter(SudokuLayout sl, SudokuType type){
        this.sl =sl;
        this.type=type;
    }

    public void paintBoard(Paint paint, Canvas canvas, float edgeRadius){

        for (Constraint c: type) //for every constraint
            if (c.getType().equals(ConstraintType.BLOCK))         //which is a Block
                outlineConstraint(c, canvas, edgeRadius, paint); //paint the outline
    }

    /* highlighted constraint has a more intuitive version, update sometime */
    private void outlineConstraint(Constraint c, Canvas canvas, float edgeRadius, Paint paint){

        int   topMargin = sl.getCurrentTopMargin();
        int  leftMargin = sl.getCurrentLeftMargin();
        int  spacing    = sl.getCurrentSpacing();


        for (Position p: c) {
					/* determine whether the position p is in the (right|left|top|bottom) border of its block constraint.
					 * test for 0 to avoid illegalArgExc for neg. vals
					 * careful when trying to optimize this definition: blocks can be squiggly (every additional compound to row/col but extra as in hypersudoku is s.th. different)
					 * */
            boolean isLeft   = p.getX() == 0 || !c.includes(Position.get(p.getX() - 1, p.getY()    ));
            boolean isRight  =                  !c.includes(Position.get(p.getX() + 1, p.getY()    ));
            boolean isTop    = p.getY() == 0 || !c.includes(Position.get(p.getX(),     p.getY() - 1));
            boolean isBottom =                  !c.includes(Position.get(p.getX(),     p.getY() + 1));
			// (0,0) is in the top left

            int from = 1;
            int to   = spacing;

            for (int i = from; i <= to; i++) {//?
                //deklariert hier, weil wir es nicht früher brauchen, effizienter wäre weiter oben
                int cellSizeAndSpacing = sl.getCurrentCellViewSize() + spacing;
						/* these first 4 seem similar. drawing the black line around?*/
						/* cells that touch the edge: Paint your edge but leave space at the corners*/
                //paint.setColor(Color.GREEN);

                if (isLeft) {
                    float x = leftMargin + p.getX() *  cellSizeAndSpacing - i;

                    float startY = topMargin  +  p.getY()      * cellSizeAndSpacing + edgeRadius;
                    float  stopY = topMargin  + (p.getY() + 1) * cellSizeAndSpacing - edgeRadius - spacing;
                    canvas.drawLine(x, startY,     x, stopY, paint);
                }
                if (isRight) {
                    float x = leftMargin + (p.getX() + 1) * cellSizeAndSpacing - spacing - 1 + i;

                    float startY = topMargin  +  p.getY()      * cellSizeAndSpacing + edgeRadius;
                    float  stopY = topMargin  + (p.getY() + 1) * cellSizeAndSpacing - edgeRadius - spacing;
                    canvas.drawLine(x, startY,     x, stopY, paint);
                }
                if (isTop) {
                    float startX = leftMargin +  p.getX()      * cellSizeAndSpacing + edgeRadius;
                    float stopX  = leftMargin + (p.getX() + 1) * cellSizeAndSpacing - edgeRadius - spacing;

                    float y = topMargin  + p.getY() * cellSizeAndSpacing - i;
                    canvas.drawLine(startX, y,    stopX, y, paint);
                }
                if (isBottom) {
                    float startX = leftMargin +  p.getX()      * cellSizeAndSpacing + edgeRadius;
                    float stopX  = leftMargin + (p.getX() + 1) * cellSizeAndSpacing - edgeRadius - spacing;

                    float y = topMargin  + (p.getY() + 1) * cellSizeAndSpacing - spacing - 1 + i;
                    canvas.drawLine(startX, y,    stopX, y, paint);
                }

						/* Cells at corners of their block draw a circle for a round circumference*/
						/*TopLeft*/
                if (isLeft && isTop) {
                    //paint.setColor(Color.MAGENTA);
                    canvas.drawCircle(
                            leftMargin + p.getX() * cellSizeAndSpacing + edgeRadius, //center-x
                            topMargin  + p.getY() * cellSizeAndSpacing + edgeRadius, //center-y
                            edgeRadius + i,                                                                                          //radius
                            paint);
                }

						/* Top Right*/
                if (isRight && isTop) {
                    //paint.setColor(Color.BLUE);
                    canvas.drawCircle(
                            leftMargin + (p.getX() + 1) * cellSizeAndSpacing - spacing - edgeRadius,
                            topMargin  +  p.getY()      * cellSizeAndSpacing + edgeRadius,
                            edgeRadius + i,
                            paint);
                }

						/*Bottom Left*/
                if (isLeft && isBottom) {
                    //paint.setColor(Color.CYAN);
                    canvas.drawCircle(
                            leftMargin +  p.getX()      * cellSizeAndSpacing + edgeRadius,
                             topMargin + (p.getY() + 1) * cellSizeAndSpacing - edgeRadius - spacing,
                            edgeRadius + i,
                            paint);
                }

						/*BottomRight*/
                if (isRight && isBottom) {
                    //paint.setColor(Color.RED);
                    canvas.drawCircle(
                            leftMargin + (p.getX() + 1) * cellSizeAndSpacing - edgeRadius - spacing,
                            topMargin  + (p.getY() + 1) * cellSizeAndSpacing - edgeRadius - spacing,
                            edgeRadius + i,
                            paint);
                }


                //paint.setColor(Color.YELLOW);
						/*Now filling the edges (if there's no corner we still leave a gap. that gap is being filled now ) */
                boolean belowRightMember = c.includes(Position.get(p.getX() + 1, p.getY() + 1));
						/*For a cell on the right border, initializeWith edge to neighbour below
						 *
						 * !isBottom excludes:      corner to the left -> no neighbour directly below i.e. unwanted filling
						 *  3rd condition excludes: corner to the right-> member below right          i.e. unwanted filling
						 *
						 * */
                if (isRight && !isBottom && !belowRightMember) {
                    canvas.drawLine(
                            leftMargin + (p.getX() + 1) * cellSizeAndSpacing - spacing - 1 + i,
                             topMargin + (p.getY() + 1) * cellSizeAndSpacing - spacing - edgeRadius,
                            leftMargin + (p.getX() + 1) * cellSizeAndSpacing - spacing - 1 + i,
                             topMargin + (p.getY() + 1) * cellSizeAndSpacing + edgeRadius,
                            paint);
                }
						/*For a cell at the bottom, initializeWith edge to right neighbour */
                if (isBottom && !isRight && !belowRightMember) {
                    canvas.drawLine(
                            leftMargin + (p.getX() + 1) * cellSizeAndSpacing - edgeRadius - spacing,
                             topMargin + (p.getY() + 1) * cellSizeAndSpacing - spacing - 1 + i,
                            leftMargin + (p.getX() + 1) * cellSizeAndSpacing + edgeRadius,
                             topMargin + (p.getY() + 1) * cellSizeAndSpacing - spacing - 1 + i,
                            paint);
                }

						/*For a cell on the left border, initializeWith edge to upper neighbour*/
                if (isLeft && !isTop && (p.getX() == 0 || !c.includes(Position.get(p.getX() - 1, p.getY() - 1)))) {
                    canvas.drawLine(
                            leftMargin + p.getX() * cellSizeAndSpacing - i,
                             topMargin + p.getY() * cellSizeAndSpacing - spacing - edgeRadius,
                            leftMargin + p.getX() * cellSizeAndSpacing - i,
                             topMargin + p.getY() * cellSizeAndSpacing + edgeRadius,
                            paint);
                }
						/*For a cell at the top initializeWith to the left*/
                if (isTop && !isLeft && (p.getY() == 0 || !c.includes(Position.get(p.getX() - 1, p.getY() - 1)))) {
                    canvas.drawLine(
                            leftMargin + p.getX() * cellSizeAndSpacing - edgeRadius - spacing,
                             topMargin + p.getY() * cellSizeAndSpacing - i,
                            leftMargin + p.getX() * cellSizeAndSpacing + edgeRadius,
                             topMargin + p.getY() * cellSizeAndSpacing - i,
                            paint);
                }
            }
        }


    }

}
