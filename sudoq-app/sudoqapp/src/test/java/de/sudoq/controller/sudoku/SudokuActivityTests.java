package de.sudoq.controller.sudoku;

import org.junit.jupiter.api.Test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Stack;

import de.sudoq.model.sudoku.Position;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Created by timo on 24.09.16.
 */
public class SudokuActivityTests {

    @Test
    public void getTimeTest(){
        assertEquals(      "00:25", SudokuActivity.getTimeString(       25));
        assertEquals(      "01:40", SudokuActivity.getTimeString(      100));
        assertEquals(      "16:40", SudokuActivity.getTimeString(    1_000));
        assertEquals(    "2:46:40", SudokuActivity.getTimeString(   10_000));
        assertEquals( "1 03:46:40", SudokuActivity.getTimeString(  100_000));
        assertEquals("11 13:46:40", SudokuActivity.getTimeString(1_000_000));

        Stack<String> itemStack= new Stack<>();

        Position g=null;
        a(g);
        System.out.println(g!=null);

        final String a = "a";
        final String b = "b";

        itemStack.add(a);
        itemStack.add(b);

        String[] item0 = new String[0];
        final String [] ss = itemStack.toArray(item0);

        System.out.println(ss.length);

    }

    private static void a(Position pos){
        pos = Position.Companion.get(9,8);
    }

    private static Date D = new Date();
    private String reference(int s){
        SimpleDateFormat sdf = new SimpleDateFormat("D HH:mm:ss");
        D.setTime(s*1000);
        return sdf.format(D);
    }

}
