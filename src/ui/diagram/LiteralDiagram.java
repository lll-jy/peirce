package ui.diagram;

import model.CutLiteral;
import model.GroundLiteral;
import model.Literal;

import javax.swing.*;
import java.util.List;

/**
 * Diagrams of a literal of Peirce's Alpha system.
 */
public abstract class LiteralDiagram extends JPanel {
    protected final Literal literal;
    protected PropositionDiagram parent;
    protected boolean isSelectMode;
    protected boolean isPasteMode;
    protected boolean isDcMode;
    protected boolean isSelected;

    /**
     * Creates a literal diagram instance.
     * @param literal the literal to draw.
     * @param parent the proposition diagram holding this literal.
     */
    protected LiteralDiagram(Literal literal, PropositionDiagram parent) {
        super();
        this.literal = literal;
        this.parent = parent;
        isSelectMode = false;
        isPasteMode = false;
        isDcMode = false;
        isSelected = false;
    }

    /**
     * Creates a new literal diagram.
     * @param literal the literal to create.
     * @param parent the proposition diagram holding this literal.
     * @return the created literal diagram.
     */
    public static LiteralDiagram createLiteralDiagram(Literal literal, PropositionDiagram parent) {
        if (literal instanceof  GroundLiteral) {
            return new GroundLiteralDiagram((GroundLiteral) literal, parent);
        } else {
            return new CutLiteralDiagram((CutLiteral) literal, parent);
        }
    }

    /**
     * Sets the select mode of the diagram.
     * @param selectMode the new select mode.
     */
    public void setSelectMode(boolean selectMode) {
        isSelectMode = selectMode;
        if (!selectMode) {
            isSelected = false;
        }
    }

    /**
     * Sets the paste mode of the diagram.
     * @param mode the new paste mode.
     */
    public void setPasteMode(boolean mode) {
        isPasteMode = mode;
    }

    /**
     * Sets the selected status of the diagram and invalidate those incompatible with this new state.
     * @param selected the new selected status.
     */
    public void setSelected(boolean selected) {
        isSelected = selected;
        if (selected) {
            parent.unselectGrandchildren();
            parent.unselectAncestors(true);
        }
    }

    /**
     * Unselect all descendants, including itself.
     */
    public void unselectAll() {
        setSelected(false);
    }

    /**
     * Unselect the diagram directly enclosed in this one.
     */
    public abstract void unselectChild();

    /**
     * Checks whether this diagram is selected.
     * @return true if it is selected.
     */
    public boolean isSelected() {
        return isSelected;
    }

    /**
     * Gets the selected literal diagrams in this diagram.
     * @return the selected literal diagrams.
     */
    public abstract List<LiteralDiagram> getSelectedLiterals();

    @Override
    public String toString() {
        return literal.toString();
    }

    /**
     * Gets the literal of the diagram.
     * @return the literal.
     */
    public Literal getLiteral() {
        return literal;
    }

    /**
     * Sets the double cut mode of the diagram.
     * @param mode the new double cut mode.
     */
    public void setDcMode(boolean mode) {
        isDcMode = mode;
    }
}
