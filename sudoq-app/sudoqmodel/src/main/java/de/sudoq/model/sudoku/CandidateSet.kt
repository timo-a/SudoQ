package de.sudoq.model.sudoku

import java.util.*

/**
 * Created by timo on 20.10.16.
 */
class CandidateSet : BitSet() {
    //creates a singleton tmp set, but is it really neccessary? We have gc after all.
    private var tmp: CandidateSet? = null
        private get() { //creates a singleton tmp set, but is it really neccessary? We have gc after all.
            if (field == null) field = CandidateSet()
            return field
        }

    /**
     * assigns the value of the parameter to itself
     * @param bs bit set to assign itself with
     */
    fun assignWith(bs: BitSet?) {
        clear()
        or(bs)
    }

    /*
    * determines whether this is a subset of bs, i.e. ∀ i: bs_i == 1  =>  CurrentSet_i == 1
    * */
    @Synchronized
    fun isSubsetOf(bs: BitSet): Boolean {
        val tmp = tmp!!
        tmp.assignWith(this)
        tmp.and(bs)
        return bs == tmp // => bs == bs & currentSet => bs ⊆ currentSetf
    }

    /**
     * This is a wrapper for @code{ get() } to make the code clearer. It does exactly the same thing!
     * @param i the bit index
     * @return true if bit at index i is set otherwise false
     */
    fun isSet(i: Int): Boolean {
        return get(i)
    }// or (i+1) would overflow

    // operate on index i here
    val setBits: IntArray
        get() {
            val setBits = IntArray(cardinality())
            var curser = 0
            var i = nextSetBit(0)
            while (i >= 0) {

                // operate on index i here
                setBits[curser++] = i
                if (i == Int.MAX_VALUE) {
                    break // or (i+1) would overflow
                }
                i = nextSetBit(i + 1)
            }
            return setBits
        }

    @Synchronized
    fun hasCommonElement(bs: BitSet?): Boolean {
        val tmp = tmp!!
        tmp.assignWith(this)
        tmp.and(bs)
        return !tmp.isEmpty
    }

    companion object {
        fun fromBitSet(bs: BitSet?): CandidateSet {
            val cs = CandidateSet()
            cs.or(bs)
            return cs
        }
    }
}