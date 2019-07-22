package de.sudoq.playaround;

import java.util.List;
import java.util.Random;
import java.util.Stack;

public class DebugRandom extends Random {

    Stack<Integer> params;

    public DebugRandom(int i){
        super(i);
        params = new Stack<Integer>();
    }

    public int nextInt(int i){
        params.push(i);
        return super.nextInt(i);
    }

    public List<Integer> getParamTrace(){
        return params;
    }
}
