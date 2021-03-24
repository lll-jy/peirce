package ui.diagram;

import model.GroundLiteral;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

public class GroundLiteralDiagram extends LiteralDiagram {
    private final JLabel label;

    public GroundLiteralDiagram(GroundLiteral literal, PropositionDiagram parent) {
        super(literal, parent);
        label = new JLabel(literal.toString());
        add(label);
        if (literal.getParent().getLevel() % 2 == 0) {
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
    public void setSelected(boolean selected) {
        super.setSelected(selected);
        if (selected) {
            label.setForeground(Color.RED);
        } else {
            label.setForeground(Color.BLACK);
        }
    }

    @Override
    public void unselectChild() {
    }

    @Override
    public List<LiteralDiagram> getSelectedLiterals() {
        return new ArrayList<>();
    }
}
