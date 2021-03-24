package ui.diagram;

import model.CutLiteral;
import model.GroundLiteral;
import model.Literal;

import javax.swing.*;

public abstract class LiteralDiagram extends JPanel {
    protected final Literal literal;
    protected PropositionDiagram parent;
    protected boolean isSelectMode;
    protected boolean isSelected;

    protected LiteralDiagram(Literal literal, PropositionDiagram parent) {
        super();
        this.literal = literal;
        this.parent = parent;
        isSelectMode = false;
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
}
