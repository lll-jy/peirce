package model;

import logic.exceptions.InvalidInferenceException;
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

    /**
     * Gets the content of the literal.
     * @return the content of the literal.
     */
    public Proposition getContent() {
        return content;
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
    public List<Literal> getAfterRemoveDoubleCut() throws InvalidInferenceException {
        List<Literal> literals = content.getLiterals();
        if (literals.size() != 1) {
            throw new InvalidInferenceException("This is not a double cut structure.");
        }
        Literal literal = literals.get(0);
        if (literal instanceof CutLiteral) {
            return ((CutLiteral) literal).content.getLiterals();
        }
        throw new InvalidInferenceException("This is not a double cut structure.");
    }

    @Override
    public Proposition getCursorProp(int pos) {
        return content.getCursorProp(pos);
    }

    @Override
    public void insertLiterals(int pos, List<Literal> literals) throws InvalidSelectionException {
        content.insertLiterals(pos, literals);
    }

    @Override
    public void increaseLevelBy(int inc) {
        content.increaseLevelBy(inc);
    }

    @Override
    public String toString() {
        return "[ " + content + "] ";
    }
}
