package ui;

import model.Literal;
import model.Proposition;

import java.awt.Color;
import java.util.List;
import javax.swing.*;

public class PeirceDiagram extends JPanel {
    private final Proposition proposition;

    public PeirceDiagram(Proposition proposition) {
        super();
        this.proposition = proposition;
        List<Literal> literals = proposition.getLiterals();
        for (Literal l : literals) {
            add(new LiteralDiagram(l));
        }
        if (proposition.getLevel() % 2 == 0) {
            setBackground(Color.WHITE);
        }
    }
}
