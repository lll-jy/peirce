package model;

import logic.exceptions.InvalidSelectionException;

import java.util.ArrayList;
import java.util.List;

/**
 * Propositions data structure.
 */
public class Proposition {
    private final int level;
    private final CutLiteral enclosingLiteral;
    private final List<Literal> literals;

    /**
     * Constructs a proposition that is of the base form, i.e. not a proposition contained in any other propositions.
     */
    public Proposition() {
        this.level = 0;
        enclosingLiteral = null;
        literals = new ArrayList<>();
    }

    /**
     * Constructs a proposition.
     * @param level the level of enclosing frames of the proposition.
     * @param enclosingLiteral the enclosing literal of the proposition, null if it is the base proposition.
     */
    public Proposition(int level, CutLiteral enclosingLiteral) {
        this.level = level;
        this.enclosingLiteral = enclosingLiteral;
        literals = new ArrayList<>();
    }

    /**
     * Adds a list of literals to the proposition.
     * @param literals the list of literals to add.
     */
    public void addLiterals(List<Literal> literals) {
        this.literals.addAll(literals);
    }

    /**
     * Adds a literal to the proposition.
     * @param literal the literal to add to the proposition.
     */
    public void addLiteral(Literal literal) {
        this.literals.add(literal);
    }

    /**
     * Gets the level of this proposition.
     * @return the level of the proposition.
     */
    public int getLevel() {
        return level;
    }

    public CutLiteral getEnclosingLiteral() {
        return enclosingLiteral;
    }

    /**
     * Gets the list of literals of proposition.
     * @return the list of literals.
     */
    public List<Literal> getLiterals() {
        return literals;
    }

    /**
     * Checks whether a given proposition has the same list of literals as this proposition.
     * @param p the other proposition to test.
     * @return true if they have the same literal list regardless of order.
     */
    public boolean hasSameLiterals(Proposition p) {
        if (p.literals.size() != literals.size()) {
            return false;
        } else {
            List<Literal> copy = new ArrayList<>(literals);
            for (Literal l : p.literals) {
                boolean removed = false;
                for (Literal cl : copy) {
                    if (cl.isSameLiteral(l)) {
                        copy.remove(cl);
                        removed = true;
                        break;
                    }
                }
                if (!removed) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Checks whether this is the base proposition.
     * @return true if it is the base proposition.
     */
    public boolean isBaseProp() {
        return level == 0;
    }

    /**
     * Gets the index of a given literal in this proposition.
     * @param l the literal to locate.
     * @return the index of the literal.
     */
    private int getIndexOf(Literal l) {
        return literals.indexOf(l);
    }

    /**
     * Gets accumulative length before a given literal in the proposition.
     * @param l the literal as end delimit.
     * @return the length before the given literal.
     */
    public int getLengthBefore(Literal l) {
        int index = getIndexOf(l);
        int acc = 0;
        for (int i = 0; i < index; i++) {
            acc += literals.get(i).getLength();
        }
        return acc;
    }

    /**
     * Gets the start index of this proposition with respect to the entire proposition.
     * @return the start token index of the proposition.
     */
    public int getStartIndex() {
        if (isBaseProp()) {
            return 0;
        } else {
            assert enclosingLiteral != null;
            int base = enclosingLiteral.getStartIndex();
            return base + 1;
        }
    }

    /**
     * Gets the last index (exclusive) of the proposition with respect to the entire proposition.
     * @return the token index after the proposition.
     */
    public int getLastIndex() {
        return getStartIndex() + getLength();
    }

    /**
     * Gets the number of tokens of this proposition, including variable names and brackets.
     * @return the number of tokens.
     */
    public int getLength() {
        int count = 0;
        for (Literal l : literals) {
            count += l.getLength();
        }
        return count;
    }

    public boolean covers(int s, int e) {
        return s >= getStartIndex() && e <= getLastIndex();
    }

    /**
     * Checks whether the cursor position is in this proposition.
     * @param pos the cursor position in terms of token index.
     * @return true if the cursor is in this proposition.
     */
    public boolean cursorIn(int pos) {
        return pos >= getStartIndex() && pos <= getLastIndex();
    }

    /**
     * Checks whether the cursor position is in this proposition but not any children of it.
     * @param pos the cursor position in terms of token index.
     * @return true if the cursor is in this proposition but not in any children of this proposition.
     */
    public boolean cursorInShallow(int pos) {
        if (!cursorIn(pos)) {
            return false;
        } else {
            for (Literal l : literals) {
                if (l.cursorIn(pos)) {
                    return false;
                }
            }
            return true;
        }
    }

    /**
     * Gets the list of selected literals.
     * @param s the start token index selected.
     * @param e the end token index selected (exclusive).
     * @return the list of selected literals, excluding those enclosed in some selected literals.
     * @throws InvalidSelectionException if the selected part is not valid.
     */
    public List<Literal> getSelectedLiterals(int s, int e) throws InvalidSelectionException {
        if (cursorInShallow(s) && cursorInShallow(e)) {
            List<Literal> selected = new ArrayList<>();
            for (Literal l : literals) {
                if (l.isSelected(s, e)) {
                    selected.add(l);
                }
            }
            return selected;
        } else if (!(cursorInShallow(s) || cursorInShallow(e))) {
            for (Literal l : literals) {
                if (l.covers(s, e)) {
                    return l.getSelectedLiterals(s, e);
                }
            }
        }
        throw new InvalidSelectionException();
    }

    /**
     * Gets the proposition where the cursor is in but not in any of its children.
     * @param pos cursor position in terms of token index.
     * @return the unique proposition for which {@code cursorInShallow(pos)} returns true.
     */
    public Proposition getCursorProp(int pos) {
        if (cursorInShallow(pos)) {
            return this;
        } else {
            for (Literal l : literals) {
                if (l.cursorIn(pos)) {
                    return l.getCursorProp(pos);
                }
            }
        }
        assert false;
        return null;
    }

    /**
     * Replaces the list of literals in the proposition with a new list of literals in the corresponding place.
     * @param original the literals to remove.
     * @param current the literals to add in place of the removed ones.
     */
    public void replaceLiterals(List<Literal> original, List<Literal> current) {
        assert literals.containsAll(original);
        int index = literals.indexOf(original.get(0));
        literals.removeAll(original);
        literals.addAll(index, current);
    }

    /**
     * Inserts a list of literals in the proposition where the cursor points to.
     * @param pos the cursor proposition in terms of token index.
     * @param literals the list of literals to insert.
     */
    public void insertLiterals(int pos, List<Literal> literals) {
        if (cursorInShallow(pos)) {
            int index = 0;
            int pointer = -1;
            int len = this.literals.size();
            while (index < len) {
                int nextPointer = this.literals.get(index).getStartIndex();
                if (pointer < pos && nextPointer >= pos) {
                    break;
                } else {
                    index++;
                    pointer = nextPointer;
                }
            }
            this.literals.addAll(index, literals);
        } else {
            for (Literal l : this.literals) {
                if (l.cursorIn(pos)) {
                    l.insertLiterals(pos, literals);
                }
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Literal l : literals) {
            sb.append(l.toString());
        }
        return sb.toString();
    }
}
