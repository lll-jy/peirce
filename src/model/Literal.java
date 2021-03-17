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
}
