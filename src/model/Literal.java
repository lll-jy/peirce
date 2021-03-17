package model;

public abstract class Literal {
    private final Proposition parent;

    public Literal(Proposition parent) {
        this.parent = parent;
    }
}
