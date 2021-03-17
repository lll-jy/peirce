package model;

/**
 * Literals that constitutes only one variable.
 */
public class GroundLiteral extends Literal {
    private final String variableName;

    /**
     * Initializes a ground literal.
     * @param parent the parent of the literal.
     * @param variableName the name of the variable in this literal.
     */
    public GroundLiteral(Proposition parent, String variableName) {
        super(parent);
        this.variableName = variableName;
    }

    @Override
    public String toString() {
        return variableName + " ";
    }
}
