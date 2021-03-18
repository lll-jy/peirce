package model;

import logic.exceptions.InvalidSelectionException;

import java.util.List;

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
        return parent.getStartIndex() + parent.getLengthBefore(this);
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

    public boolean covers(int s, int e) {
        return s > getStartIndex() && e < getLastIndex();
    }

    public boolean isCoveredBy(int s, int e) {
        return s <= getStartIndex() && e >= getLastIndex();
    }

    public boolean cursorIn(int pos) {
        return pos > getStartIndex() && pos < getLastIndex();
    }

    public boolean isSelected(int s, int e) {
        return isCoveredBy(s, e) && (parent.cursorInShallow(s) && parent.cursorInShallow(e));
    }

    abstract public List<Literal> getSelectedLiterals(int s, int e) throws InvalidSelectionException;
}
