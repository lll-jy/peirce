package model;

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
    public int getIndexOf(Literal l) {
        return literals.indexOf(l);
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

    public int getLength() {
        int count = 0;
        for (Literal l : literals) {
            count += l.getLength();
        }
        return count;
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
