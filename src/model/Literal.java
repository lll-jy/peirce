package model;

import logic.exceptions.InvalidInferenceException;
import logic.exceptions.InvalidSelectionException;

import java.util.List;

/**
 * Literals of a proposition connected by conjunction.
 */
public abstract class Literal {
    private Proposition parent;

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
     * Updates the parent of the literal.
     * @param parent the parent proposition.
     */
    public void setParent(Proposition parent) {
        this.parent = parent;
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

    /**
     * Checks whether this literal covers the selected part, excluding the brackets for cut.
     * @param s the start token index of selected part.
     * @param e the end token index of selected part (exclusive).
     * @return true if the literal is a cut literal and the selected part are completely in the bracket of the cut.
     */
    public boolean covers(int s, int e) {
        return s > getStartIndex() && e < getLastIndex();
    }

    /**
     * Checks whether this literal is completely covered by the selected part.
     * @param s the start token index of selected part.
     * @param e the end token index of selected part (exclusive).
     * @return true if the literal, including its brackets for cut, is completely covered by the selected part.
     */
    public boolean isCoveredBy(int s, int e) {
        return s <= getStartIndex() && e >= getLastIndex();
    }

    /**
     * Checks whether the cursor is in the brackets for cut of this literal.
     * @param pos the position of cursor in terms of token index.
     * @return true if the literal is a cut literal and the cursor is in the brackets of the cut.
     */
    public boolean cursorIn(int pos) {
        return pos > getStartIndex() && pos < getLastIndex();
    }

    /**
     * Checks whether this literal is selected.
     * @param s the start token index of the selected part.
     * @param e the end token index of the selected part.
     * @return true if this literal is selected and no enclosing cut is selected.
     */
    public boolean isSelected(int s, int e) {
        return isCoveredBy(s, e) && (parent.cursorInShallow(s) && parent.cursorInShallow(e));
    }

    /**
     * Gets the list of selected literals inside this literal
     * @param s the start token index of the selected part.
     * @param e the end token index of the selected part.
     * @return the list of selected literals, excluding those enclosed by other selected literals.
     * @throws InvalidSelectionException if the selected part is invalid.
     */
    abstract public List<Literal> getSelectedLiterals(int s, int e) throws InvalidSelectionException;

    /**
     * Gets the list of literals after removing outer double cut of this literal.
     * @return the literals inside the outer double cut of this literal.
     * @throws InvalidInferenceException if the literal is not in the form of double cut.
     */
    abstract public List<Literal> getAfterRemoveDoubleCut() throws InvalidInferenceException;

    /**
     * Helps to get the proposition where the cursor is in but not in any of its children.
     * @param pos the cursor position in terms of token index.
     * @return the proposition where the cursor is in.
     */
    abstract public Proposition getCursorProp(int pos);

    /**
     * Helps to insert a list of literals to where the cursor points at.
     * @param pos the cursor position in terms of token index.
     * @param literals the list of literals to insert.
     * @throws InvalidSelectionException if the position is invalid.
     */
    public void insertLiterals(int pos, List<Literal> literals) throws InvalidSelectionException {
    };

    /**
     * Checks whether this literal can be removed by some inference rule.
     * @return true if it can be removed.
     */
    public boolean canDelete() {
        // Erasure
        if (parent.getLevel() % 2 == 0 && !parent.isSingleLiteralProp()) {
            return true;
        }
        // Deiteration
        return parent.appearsInFrame(this);
    };

    /**
     * Helps to check whether a literal of a given form appears in some frames under a proposition.
     * @param literal the literal to find.
     * @return true if it appears.
     */
    abstract public boolean appearsInFrame(Literal literal);

    /**
     * Increases the level of the literal and its enclosing content.
     * @param inc the number of levels to increase.
     */
    abstract public void increaseLevelBy(int inc);
}
