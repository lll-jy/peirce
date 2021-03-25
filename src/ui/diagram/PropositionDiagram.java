package ui.diagram;

import static javax.swing.JOptionPane.YES_OPTION;
import model.Literal;
import model.Proposition;
import ui.ProofPanel;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

/**
 * A diagram of a proposition in Peirce's Alpha system.
 */
public class PropositionDiagram extends JPanel {
    private final Proposition proposition;
    private boolean isSelectMode;
    private boolean isPasteMode;
    private boolean isDcMode;
    private final List<LiteralDiagram> literalDiagrams;
    private CutLiteralDiagram enclosingDiagram;

    /**
     * Creates a diagram corresponding to the given proposition.
     * @param proposition the proposition to draw.
     */
    public PropositionDiagram(Proposition proposition) {
        super();
        this.proposition = proposition;
        isSelectMode = false;
        isPasteMode = false;
        isDcMode = false;
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
                if (isPasteMode && isDcMode) {
                    Object[] options = {"Add empty double cut", "Paste"};
                    int dc = JOptionPane.showOptionDialog(JOptionPane.getRootFrame(),
                            "Which action do you want to perform?",
                            "Action Inquiry",
                            JOptionPane.YES_NO_CANCEL_OPTION,
                            JOptionPane.WARNING_MESSAGE,
                            null,
                            options,
                            options[1]);
                    if (dc == YES_OPTION) {
                        ProofPanel.insertDoubleCut.accept(proposition);
                    } else {
                        ProofPanel.paste.accept(proposition);
                    }
                } else if (isPasteMode) {
                    ProofPanel.paste.accept(proposition);
                } else if (isDcMode) {
                    ProofPanel.insertDoubleCut.accept(proposition);
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
                if (isPasteMode || isDcMode) {
                    Color highlightColor = new Color(255, 217, 27, 208);
                    setBackground(highlightColor);
                    getParent().setBackground(highlightColor);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (isPasteMode || isDcMode) {
                    if (proposition.getLevel() % 2 == 0) {
                        setBackground(Color.WHITE);
                        getParent().setBackground(Color.WHITE);
                    } else {
                        Color defaultBackgroundColor = new Color(238, 238, 238);
                        setBackground(defaultBackgroundColor);
                        getParent().setBackground(defaultBackgroundColor);
                    }
                }
            }
        });
    }

    /**
     * Creates a diagram corresponding to the given proposition wrapped in an enclosing cut.
     * @param proposition the proposition to draw.
     * @param enclosingDiagram the enclosing cut diagram.
     */
    public PropositionDiagram(Proposition proposition, CutLiteralDiagram enclosingDiagram) {
        this(proposition);
        this.enclosingDiagram = enclosingDiagram;
    }

    /**
     * Sets the select mode of the diagram.
     * @param selectMode the new select mode.
     */
    public void setSelectMode(boolean selectMode) {
        isSelectMode = selectMode;
        for (LiteralDiagram ld : literalDiagrams) {
            ld.setSelectMode(selectMode);
        }
    }

    /**
     * Sets the paste mode of the diagram.
     * @param mode the new paste mode.
     */
    public void setPasteMode(boolean mode) {
        isPasteMode = mode;
        for (LiteralDiagram ld : literalDiagrams) {
            ld.setPasteMode(mode);
        }
        if (proposition.getLevel() % 2 == 0) {
            setBackground(Color.WHITE);
        } else {
            setBackground(new Color(238, 238, 238));
        }
    }

    /**
     * Unselect all children and descendants.
     */
    public void unselectAll() {
        for (LiteralDiagram ld : literalDiagrams) {
            ld.unselectAll();
        }
    }

    /**
     * Unselect all descendants except the direct children.
     */
    public void unselectGrandchildren() {
        for (LiteralDiagram ld : literalDiagrams) {
            ld.unselectChild();
        }
    }

    /**
     * Unselect all ancestors of the diagram.
     * @param first true if it is the first round if recursive call.
     */
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

    /**
     * Gets the list of selected literal diagrams in this diagram.
     * @return the list of selected literal diagrams.
     */
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

    /**
     * Sets the double cut mode of the diagram.
     * @param mode the new double cut mode.
     */
    public void setDcMode(boolean mode) {
        isDcMode = mode;
        for (LiteralDiagram ld : literalDiagrams) {
            ld.setDcMode(mode);
        }
        if (proposition.getLevel() % 2 == 0) {
            setBackground(Color.WHITE);
        } else {
            setBackground(new Color(238, 238, 238));
        }
    }

    @Override
    public String toString() {
        return proposition.toString();
    }
}
