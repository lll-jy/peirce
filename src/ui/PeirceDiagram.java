package ui;

import model.Literal;
import model.Proposition;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;
import javax.swing.*;

public class PeirceDiagram extends JPanel {
    private final Proposition proposition;
    private boolean isSelectMode;

    public PeirceDiagram(Proposition proposition) {
        super();
        this.proposition = proposition;
        isSelectMode = false;
        List<Literal> literals = proposition.getLiterals();
        for (Literal l : literals) {
            add(new LiteralDiagram(l));
        }
        if (proposition.getLevel() % 2 == 0) {
            setBackground(Color.WHITE);
        }
    }

    public void setSelectMode(boolean selectMode) {
        isSelectMode = selectMode;
        for (Component c : getComponents()) {
            assert c instanceof LiteralDiagram;
            ((LiteralDiagram) c).setSelectMode(selectMode);
        }
    }
}
