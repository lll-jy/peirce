package ui.diagram;

import model.CutLiteral;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class CutLiteralDiagram extends LiteralDiagram {
    private PropositionDiagram content;

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
                    if (!isSelected) {
                        setBorder(BorderFactory.createLineBorder(Color.RED, 2));
                        isSelected = true;
                    } else {
                        setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
                        isSelected = false;
                    }
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
}
