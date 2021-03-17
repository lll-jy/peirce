package model;

/**
 * Cut form propositions as a literal.
 */
public class CutLiteral extends Literal {
    private Proposition content;

    /**
     * Constructs a cut literal instance.
     * @param parent the parent of this literal.
     * @param content the proposition enclosed in the cut.
     */
    public CutLiteral(Proposition parent, Proposition content) {
        super(parent);
        this.content = content;
    }

    /**
     * Updates the content of the literal.
     * @param content the content to update.
     */
    public void setContent(Proposition content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "[ " + content + "] ";
    }
}
