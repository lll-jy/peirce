package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    public List<Literal> getLiterals() {
        return literals;
    }

    /**
     * Checks whether a given proposition has the same list of literals as this proposition.
     * @param p the other proposition to test.
     * @return true if they have the same literal list regardless of order.
     */
    private boolean hasSameLiterals(Proposition p) {
        if (p.literals.size() != literals.size()) {
            return false;
        } else {
            List<Literal> copy = new ArrayList<>(literals);
            for (Literal l : p.literals) {
                if (copy.contains(l)) {
                    copy.remove(l);
                } else {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Literal l : literals) {
            sb.append(l.toString());
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Proposition)) return false;
        Proposition that = (Proposition) o;
        return hasSameLiterals(that);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getLiterals());
    }
}
