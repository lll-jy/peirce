package ui;

import model.CutLiteral;
import model.Literal;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class LiteralDiagram extends JPanel {
    private final Literal literal;
    private boolean isSelectMode;
    private boolean isSelected;

    public LiteralDiagram(Literal literal) {
        super();
        this.literal = literal;
        isSelectMode = false;
        isSelected = false;
        if (literal instanceof CutLiteral) {
            setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
            add(new PeirceDiagram(((CutLiteral) literal).getContent()));
            if (literal.getParent().getLevel() % 2 == 1) {
                setBackground(Color.WHITE);
            }
        } else {
            add(new JLabel(literal.toString()));
            if (literal.getParent().getLevel() % 2 == 0) {
                setBackground(Color.WHITE);
            }
        }
        addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (isSelectMode) {
                    System.out.println(literal);
                    if (!isSelected) {
                        if (literal instanceof CutLiteral) {
                            setBorder(BorderFactory.createLineBorder(Color.RED, 2));
                        } else {
                            getComponent(0).setForeground(Color.RED);
                        }
                        isSelected = true;
                    } else {
                        if (literal instanceof CutLiteral) {
                            setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
                        } else {
                            getComponent(0).setForeground(Color.BLACK);
                        }
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

    public void setSelectMode(boolean selectMode) {
        isSelectMode = selectMode;
        if (literal instanceof CutLiteral) {
            Component c = getComponent(0);
            assert c instanceof PeirceDiagram;
            ((PeirceDiagram) c).setSelectMode(selectMode);
        }
        if (!selectMode) {
            isSelected = false;
        }
    }
}
