package de.sudoq.controller.sudoku;

import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.assertEquals;

/**
 * Created by timo on 24.09.16.
 */
public class SudokuActivityTests {

    @Test
    public void getTimeTest(){
        assertEquals(      "00:25", SudokuActivity.getTimeString(       25));
        assertEquals(      "01:40", SudokuActivity.getTimeString(      100));
        assertEquals(      "16:40", SudokuActivity.getTimeString(    1_000));
        assertEquals(   "02:46:40", SudokuActivity.getTimeString(   10_000));
        assertEquals( "1 03:46:40", SudokuActivity.getTimeString(  100_000));
        assertEquals("11 13:46:40", SudokuActivity.getTimeString(1_000_000));

        /*assertEquals(      "00:25", reference(       25));
        assertEquals(      "01:40", reference(      100));
        assertEquals(      "16:40", reference(    1_000));
        assertEquals(   "02:46:40", reference(   10_000));
        assertEquals( "1 03:46:40", reference(  100_000));
        assertEquals("11 13:46:40", reference(1_000_000));*/
    }

    private static Date D = new Date();
    private String reference(int s){
        SimpleDateFormat sdf = new SimpleDateFormat("D HH:mm:ss");
        D.setTime(s*1000);
        return sdf.format(D);
    }

}
