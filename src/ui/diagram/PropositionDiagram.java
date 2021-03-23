package ui.diagram;

import model.Literal;
import model.Proposition;

import java.awt.Color;
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
}
