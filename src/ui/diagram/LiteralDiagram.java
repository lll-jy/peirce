package ui.diagram;

import model.CutLiteral;
import model.GroundLiteral;
import model.Literal;

import javax.swing.*;
import java.util.List;

public abstract class LiteralDiagram extends JPanel {
    protected final Literal literal;
    protected PropositionDiagram parent;
    protected boolean isSelectMode;
    protected boolean isPasteMode;
    protected boolean isSelected;

    protected LiteralDiagram(Literal literal, PropositionDiagram parent) {
        super();
        this.literal = literal;
        this.parent = parent;
        isSelectMode = false;
        isPasteMode = false;
        isSelected = false;
    }

    public static LiteralDiagram createLiteralDiagram(Literal literal, PropositionDiagram parent) {
        if (literal instanceof  GroundLiteral) {
            return new GroundLiteralDiagram((GroundLiteral) literal, parent);
        } else {
            return new CutLiteralDiagram((CutLiteral) literal, parent);
        }
    }

    public void setSelectMode(boolean selectMode) {
        isSelectMode = selectMode;
        if (!selectMode) {
            isSelected = false;
        }
    }

    public void setPasteMode(boolean mode) {
        isPasteMode = mode;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
        if (selected) {
            parent.unselectGrandchildren();
            parent.unselectAncestors(true);
        }
    }

    public void unselectAll() {
        setSelected(false);
    }

    public abstract void unselectChild();

    public boolean isSelected() {
        return isSelected;
    }

    public abstract List<LiteralDiagram> getSelectedLiterals();

    @Override
    public String toString() {
        return literal.toString();
    }

    public Literal getLiteral() {
        return literal;
    }
}
