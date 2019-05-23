package de.sudoq.model.solvingAssistant;

/**
 * Created by timo on 27.09.16.
 */
public enum HintTypes {
    LastDigit,

    LastCandidate,

    LeftoverNote,

    NakedSingle,
    NakedPair,
    NakedTriple,
    NakedQuadruple,
    NakedQuintuple,

    HiddenSingle,
    HiddenPair,
    HiddenTriple,
    HiddenQuadruple,
    HiddenQuintuple,

    LockedCandidatesExternal,
    XWing,

    NoNotes, //this is for when the user doesn't fill out the notes - DON'T USE IN GENERATOR!
    Backtracking;

}
