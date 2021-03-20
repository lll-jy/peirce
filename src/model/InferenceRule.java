package model;

/**
 * The set of inference rules applicable to the Peirce Alpha system.
 */
public enum InferenceRule {
    ERASURE, INSERTION, ITERATION, DEITERATION, DOUBLE_CUT_INTRO, DOUBLE_CUT_ELIM, REMOVE_BOTH, INSERT_BOTH;

    public String printString() {
        return switch (this) {
            case ERASURE -> "Erasure";
            case INSERTION -> "Insertion";
            case ITERATION -> "Iteration";
            case DEITERATION -> "Deiteration";
            case DOUBLE_CUT_INTRO -> "Double Cut Introduction";
            case DOUBLE_CUT_ELIM -> "Double Cut Elimination";
            case INSERT_BOTH -> "Insertion and Iteration";
            case REMOVE_BOTH -> "Erasure and Deiteration";
        };
    }
}
