package ui;

import model.Proposition;

import javax.swing.*;
import java.awt.*;

public class DiagramPanel extends JPanel {
    private final String title;
    private final int width;
    private final int height;
    private Proposition proposition;
    private final JPanel labelPanel;

    public DiagramPanel(String title, Proposition proposition, int width, int height) {
        super();
        this.title = title;
        this.proposition = proposition;
        this.width = width;
        this.height = height;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        labelPanel = new JPanel();
        add(labelPanel);
        labelPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 3, 3));
        labelPanel.add(new JLabel(title));
        constructDiagram();
        setBackground(Color.WHITE);
    }

    private void constructDiagram() {
        JPanel canvasPanel = new JPanel();
        JScrollPane canvas = new JScrollPane(canvasPanel);
        canvas.setBackground(Color.WHITE);
        canvasPanel.setBackground(Color.WHITE);
        PeirceDiagram diagram = new PeirceDiagram(proposition);
        canvasPanel.add(diagram);
        canvas.setPreferredSize(new Dimension(width, height));
        add(canvas);
    }

    public void refresh(Proposition proposition) {
        removeAll();
        add(labelPanel);
        this.proposition = proposition;
        constructDiagram();
    }
}
