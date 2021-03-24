package ui.diagram;

import model.Literal;
import model.Proposition;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

public class PropositionDiagram extends JPanel {
    private final Proposition proposition;
    private boolean isSelectMode;
    private final List<LiteralDiagram> literalDiagrams;
    private CutLiteralDiagram enclosingDiagram;

    public PropositionDiagram(Proposition proposition) {
        super();
        this.proposition = proposition;
        isSelectMode = false;
        this.literalDiagrams = new ArrayList<>();
        enclosingDiagram = null;
        List<Literal> literals = proposition.getLiterals();
        for (Literal l : literals) {
            LiteralDiagram literalDiagram = LiteralDiagram.createLiteralDiagram(l, this);
            literalDiagrams.add(literalDiagram);
            add(literalDiagram);
        }
        if (proposition.getLevel() % 2 == 0) {
            setBackground(Color.WHITE);
        }
        addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
            }

            @Override
            public void mousePressed(MouseEvent e) {
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                if (isSelectMode) {
                    setBackground(new Color(156, 10, 10, 101));
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (isSelectMode) {
                    if (proposition.getLevel() % 2 == 0) {
                        setBackground(Color.WHITE);
                    } else {
                        setBackground(new Color(238, 238, 238));
                    }
                }
            }
        });
    }

    public PropositionDiagram(Proposition proposition, CutLiteralDiagram enclosingDiagram) {
        this(proposition);
        this.enclosingDiagram = enclosingDiagram;
    }

    public void setSelectMode(boolean selectMode) {
        isSelectMode = selectMode;
        for (LiteralDiagram ld : literalDiagrams) {
            ld.setSelectMode(selectMode);
        }
    }

    public void unselectAll() {
        for (LiteralDiagram ld : literalDiagrams) {
            ld.unselectAll();
        }
    }

    public void unselectGrandchildren() {
        for (LiteralDiagram ld : literalDiagrams) {
            ld.unselectChild();
        }
    }

    public void unselectAncestors(boolean first) {
        if (enclosingDiagram != null) {
            enclosingDiagram.setSelected(false);
            enclosingDiagram.unselectAncestors();
        }
        if (!first) {
            for (LiteralDiagram ld : literalDiagrams) {
                ld.setSelected(false);
            }
        }
    }

    public List<LiteralDiagram> getSelectedLiterals() {
        List<LiteralDiagram> result = new ArrayList<>();
        for (LiteralDiagram ld : literalDiagrams) {
            if (ld.isSelected()) {
                result.add(ld);
            }
        }
        if (result.isEmpty()) {
            for (LiteralDiagram ld : literalDiagrams) {
                result = ld.getSelectedLiterals();
                if (!result.isEmpty()) {
                    return result;
                }
            }
            return new ArrayList<>();
        } else {
            return result;
        }
    }

    @Override
    public String toString() {
        return proposition.toString();
    }
}
