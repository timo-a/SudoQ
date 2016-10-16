//package de.sudoq.model;

/**
 * Created by timo on 13.05.16.
 */
public class Test3 {
    public static void main(String[] args){

        int sum=0;
        for (Character c: "¹²³⁴⁵⁶⁷⁸⁹".toCharArray())
            sum += Character.getNumericValue(c);
        //System.out.println(sum);
        System.out.println("a  b     c  d".split("\\s+").length);

    }
}
