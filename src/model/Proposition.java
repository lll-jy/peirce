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

    public List<Literal> getLiterals() {
        return literals;
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
