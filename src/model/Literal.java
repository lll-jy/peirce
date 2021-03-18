package model;

/**
 * Literals of a proposition connected by conjunction.
 */
public abstract class Literal {
    private final Proposition parent;

    /**
     * Initializes a literal.
     * @param parent the proposition this literal directly belongs to as a conjunction literal.
     */
    public Literal(Proposition parent) {
        this.parent = parent;
    }

    /**
     * Gets the parent of the literal.
     * @return the parent proposition.
     */
    public Proposition getParent() {
        return parent;
    }

    /**
     * Checks whether a given literal has the same content as this one.
     * @param l the given literal to check.
     * @return true if they contain the same content.
     */
    abstract public boolean isSameLiteral(Literal l);

    /**
     * Gets the token index of the first token of this literal.
     * @return the token index of the first token of this literal.
     */
    public int getStartIndex() {
        int base = parent.getStartIndex();
        int index = parent.getIndexOf(this);
        return base + index;
    }

    /**
     * Gets the token index of the last token (exclusive) of this literal.
     * @return the token index at the end of the literal.
     */
    public int getLastIndex() {
        return getStartIndex() + getLength();
    }

    /**
     * Gets the length in terms of tokens of the literal, where a token is either a bracket or a variable name.
     * @return the length of the literal.
     */
    abstract public int getLength();
}
