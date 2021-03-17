package model;

import java.util.ArrayList;
import java.util.List;

public class Proposition {
    private final int level;
    private final CutLiteral enclosingLiteral;
    private final List<Literal> literals;

    public Proposition() {
        this.level = 0;
        enclosingLiteral = null;
        literals = new ArrayList<>();
    }

    public Proposition(int level, CutLiteral enclosingLiteral) {
        this.level = level;
        this.enclosingLiteral = enclosingLiteral;
        literals = new ArrayList<>();
    }

    public void addLiterals(List<Literal> literals) {
        this.literals.addAll(literals);
    }

    public void addLiteral(Literal literal) {
        this.literals.add(literal);
    }

    public CutLiteral getEnclosingLiteral() {
        return enclosingLiteral;
    }

    public List<Literal> getLiterals() {
        return literals;
    }
}
