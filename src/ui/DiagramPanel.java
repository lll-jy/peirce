package ui;

import model.Proposition;

import javax.swing.*;
import java.awt.*;

public class DiagramPanel extends JPanel {
    private Proposition proposition;
    private final JPanel canvasPanel;
    private boolean isSelectMode;
    private PeirceDiagram diagram;

    public DiagramPanel(String title, Proposition proposition, int width, int height) {
        super();
        this.proposition = proposition;
        isSelectMode = false;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        JPanel labelPanel = new JPanel();
        add(labelPanel);
        labelPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 3, 3));
        labelPanel.add(new JLabel(title));
        canvasPanel = new JPanel();
        JScrollPane canvas = new JScrollPane(canvasPanel);
        canvas.setBackground(Color.WHITE);
        canvasPanel.setBackground(Color.WHITE);
        canvas.setPreferredSize(new Dimension(width, height));
        add(canvas);
        constructDiagram();
        setBackground(Color.WHITE);
    }

    private void constructDiagram() {
        canvasPanel.removeAll();
        diagram = new PeirceDiagram(proposition);
        canvasPanel.add(diagram);
        canvasPanel.revalidate();
        canvasPanel.repaint();
    }

    public void refresh(Proposition proposition) {
        this.proposition = proposition;
        constructDiagram();
    }

    public void setSelectMode(boolean mode) {
        isSelectMode = mode;
        diagram.setSelectMode(mode);
    }

    public boolean isSelectMode() {
        return isSelectMode;
    }
}
