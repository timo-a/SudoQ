package de.sudoq.model.solvingAssistant;

enum class HintTypes {
    LastDigit,

    LastCandidate,

    LeftoverNote,

    NakedSingle,
    NakedPair,
    NakedTriple,
    NakedQuadruple,
    NakedQuintuple,
    Naked__6_tuple,
    Naked__7_tuple,
    Naked__8_tuple,
    Naked__9_tuple,
    Naked_10_tuple,
    Naked_11_tuple,
    Naked_12_tuple,
    Naked_13_tuple, //most fields per constraint is samurai with 25, naked complement hidden -> ceil(25/2) == 13

    HiddenSingle,
    HiddenPair,
    HiddenTriple,
    HiddenQuadruple,
    HiddenQuintuple,
    Hidden__6_tuple,
    Hidden__7_tuple,
    Hidden__8_tuple,
    Hidden__9_tuple,
    Hidden_10_tuple,
    Hidden_11_tuple,
    Hidden_12_tuple,
    Hidden_13_tuple, //most fields per constraint is samurai with 25, naked complement hidden -> ceil(25/2) == 13

    LockedCandidatesExternal,
    XWing,

    NoNotes, //this is for when the user doesn't fill out the notes - DON'T USE IN GENERATOR!
    Backtracking

}
