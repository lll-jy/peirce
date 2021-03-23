package ui;

import model.Proposition;

import javax.swing.*;
import java.awt.*;

public class DiagramPanel extends JPanel {
    private final String title;
    private Proposition proposition;
    private final JPanel labelPanel;

    public DiagramPanel(String title, Proposition proposition) {
        super();
        this.title = title;
        this.proposition = proposition;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        labelPanel = new JPanel();
        add(labelPanel);
        labelPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 3, 3));
        labelPanel.add(new JLabel(title));
        add(new PeirceDiagram(proposition));
        setBackground(Color.WHITE);
    }

    public void refresh(Proposition proposition) {
        removeAll();
        add(labelPanel);
        this.proposition = proposition;
        add(new PeirceDiagram(proposition));
    }
}
