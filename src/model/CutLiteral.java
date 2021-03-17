package model;

public class CutLiteral extends Literal {
    private final Proposition content;

    public CutLiteral(Proposition parent, Proposition content) {
        super(parent);
        this.content = content;
    }
}
