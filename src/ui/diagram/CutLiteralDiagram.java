package ui.diagram;

import model.CutLiteral;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;

/**
 * A literal diagram of a cut literal.
 */
public class CutLiteralDiagram extends LiteralDiagram {
    private final PropositionDiagram content;

    /**
     * Creates a cut literal diagram.
     * @param literal the literal of this diagram.
     * @param parent the parent diagram holding this literal diagram.
     */
    public CutLiteralDiagram(CutLiteral literal, PropositionDiagram parent) {
        super(literal, parent);
        setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        content = new PropositionDiagram(literal.getContent(), this);
        add(content);
        if (literal.getParent().getLevel() % 2 == 1) {
            setBackground(Color.WHITE);
        }
        addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (isSelectMode) {
                    setSelected(!isSelected);
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }
        });
    }

    @Override
    public void setSelectMode(boolean selectMode) {
        super.setSelectMode(selectMode);
        content.setSelectMode(selectMode);
    }

    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);
        if (selected) {
            setBorder(BorderFactory.createLineBorder(Color.RED, 2));
        } else {
            setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        }
    }

    @Override
    public void unselectChild() {
        content.unselectAll();
    }

    /**
     * Unselect all ancestors of this cut literal diagram.
     */
    public void unselectAncestors() {
        parent.unselectAncestors(false);
    }

    @Override
    public void unselectAll() {
        super.unselectAll();
        content.unselectAll();
    }

    @Override
    public List<LiteralDiagram> getSelectedLiterals() {
        return content.getSelectedLiterals();
    }

    @Override
    public void setPasteMode(boolean mode) {
        super.setPasteMode(mode);
        content.setPasteMode(mode);
    }

    @Override
    public void setDcMode(boolean mode) {
        super.setDcMode(mode);
        content.setDcMode(mode);
    }
}
