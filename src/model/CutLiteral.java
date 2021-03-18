package model;

import logic.exceptions.InvalidSelectionException;

import java.util.List;

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
    public int getLength() {
        return 2 + content.getLength();
    }

    @Override
    public boolean isSameLiteral(Literal l) {
        if (l instanceof CutLiteral) {
            return content.hasSameLiterals(((CutLiteral) l).content);
        } else {
            return false;
        }
    }

    @Override
    public List<Literal> getSelectedLiterals(int s, int e) throws InvalidSelectionException {
        return content.getSelectedLiterals(s, e);
    }

    @Override
    public String toString() {
        return "[ " + content + "] ";
    }
}
