package model;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CutLiteral)) return false;
        CutLiteral that = (CutLiteral) o;
        return Objects.equals(content, that.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(content);
    }
}
