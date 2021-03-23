package ui;

import model.CutLiteral;
import model.Literal;

import javax.swing.*;
import java.awt.*;

public class LiteralDiagram extends JPanel {
    private final Literal literal;

    public LiteralDiagram(Literal literal) {
        super();
        this.literal = literal;
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
    }
}
