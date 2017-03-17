package de.sudoq.model.sudoku;

import java.util.BitSet;

/**
 * Created by timo on 20.10.16.
 */
public class CandidateSet extends BitSet {

    private CandidateSet tmp;

    private CandidateSet getTmp(){//creates a singleton tmp set, but is it really neccessary? We have gc after all.
        if (tmp==null)
            tmp = new CandidateSet();
        return tmp;
    }






    /**
     * assigns the value of the parameter to itself
     */
    public void assignWith(BitSet bs){
        clear();
        or(bs);
    }

    /*
    * determines whether this is a subset of bs, i.e. ∀ i: bs_i == 1  =>  CurrentSet_i == 1
    * */
    public synchronized boolean isSubsetOf(BitSet bs){
        CandidateSet tmp = getTmp();
        tmp.assignWith(this);
        tmp.and(bs);
        return bs.equals(tmp); // => bs == bs & currentSet => bs ⊆ currentSetf
    }

    /**
     * This is a wrapper for @code{ get() } to make the code clearer. It does exactly the same thing!
     * @returns true if bit at index i is set otherwise false
     */
    public boolean isSet(int i){
        return get(i);
    }

    public int[] getSetBits(){
        int[] setBits = new int[cardinality()];
        int curser = 0;
        for (int i = nextSetBit(0); i >= 0; i = nextSetBit(i+1)) {
          // operate on index i here
          setBits[curser++]=i;

          if (i == Integer.MAX_VALUE) {
              break; // or (i+1) would overflow
          }
        }
        return setBits;
    }

    public synchronized boolean hasCommonElement(BitSet bs){
        CandidateSet tmp = getTmp();
        tmp.assignWith(this);
        tmp.and(bs);
        return !tmp.isEmpty();
    }
}
