package model;

import logic.exceptions.InvalidSelectionException;

import java.util.List;

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
    public int getLength() {
        return 1;
    }

    @Override
    public boolean isSameLiteral(Literal l) {
        if (l instanceof GroundLiteral) {
            return variableName.equals(((GroundLiteral) l).variableName);
        } else {
            return false;
        }
    }

    @Override
    public List<Literal> getSelectedLiterals(int s, int e) throws InvalidSelectionException {
        throw new InvalidSelectionException();
    }

    @Override
    public String toString() {
        return variableName + " ";
    }
}
