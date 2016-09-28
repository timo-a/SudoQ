package de.sudoq.model.sudoku.sudokuTypes;

/*import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
*/
import java.io.File;

import de.sudoq.model.xml.XmlHelper;
import de.sudoq.model.xml.XmlTree;

/**
 * Created by timo on 13.05.16.
 */
public class test4 {
    public static void main(String[] args){
        System.out.println("123");
        XmlHelper helper = new XmlHelper();
        SudokuType t = new SudokuType();
        try {
            XmlTree xt = helper.loadXml(new File("/home/timo/Code/android/Sudoq8_generalprobe/SudoQ/sudoq-app/res/standard9x9.xml"));
            t.fillFromXml(xt);
        }catch(Exception e){
            e.printStackTrace();
        }

  /*      Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String repr = gson.toJson(t);
        System.out.println(repr);
        SudokuType t2 = gson.fromJson(repr, SudokuType.class);*/
    }
}
