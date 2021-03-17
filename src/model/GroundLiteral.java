package model;

public class GroundLiteral extends Literal {
    private final String variableName;

    public GroundLiteral(Proposition parent, String variableName) {
        super(parent);
        this.variableName = variableName;
    }
}
