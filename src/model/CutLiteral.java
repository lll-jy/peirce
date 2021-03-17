package model;

public class CutLiteral extends Literal {
    private Proposition content;

    public CutLiteral(Proposition parent, Proposition content) {
        super(parent);
        this.content = content;
    }

    public void setContent(Proposition content) {
        this.content = content;
    }
}
