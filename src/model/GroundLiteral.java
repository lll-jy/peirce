package model;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GroundLiteral)) return false;
        GroundLiteral that = (GroundLiteral) o;
        return Objects.equals(variableName, that.variableName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(variableName);
    }
}
