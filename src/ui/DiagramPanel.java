package ui;

import model.Proposition;
import ui.diagram.LiteralDiagram;
import ui.diagram.PropositionDiagram;

import javax.swing.*;
import java.awt.FlowLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;

/**
 * A panel including a diagram title and the diagram of a proposition in Peirce's Alpha system.
 */
public class DiagramPanel extends JPanel {
    private Proposition proposition;
    private final JPanel canvasPanel;
    private boolean isSelectMode;
    private boolean isPasteMode;
    private boolean isDcMode;
    private PropositionDiagram diagram;

    /**
     * Creates a panel of diagram.
     * @param title the title of the diagram.
     * @param proposition the proposition the diagram needs to draw.
     * @param width the preferred width of the diagram.
     * @param height the preferred height of the diagram.
     */
    public DiagramPanel(String title, Proposition proposition, int width, int height) {
        super();
        this.proposition = proposition;
        isSelectMode = false;
        isPasteMode = false;
        isDcMode = false;

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
        canvasPanel.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (isDcMode && proposition.getLiterals().isEmpty()) {
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
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }
        });
    }

    /**
     * Repaints the diagram.
     */
    private void constructDiagram() {
        canvasPanel.removeAll();
        diagram = new PropositionDiagram(proposition);
        canvasPanel.add(diagram);
        canvasPanel.revalidate();
        canvasPanel.repaint();
    }

    /**
     * Refreshes the panel with a new proposition.
     * @param proposition the new proposition to draw.
     */
    public void refresh(Proposition proposition) {
        this.proposition = proposition;
        constructDiagram();
    }

    /**
     * Set the select mode of the panel.
     * @param mode the new select mode.
     */
    public void setSelectMode(boolean mode) {
        isSelectMode = mode;
        diagram.setSelectMode(mode);
    }

    /**
     * Set the paste mode of the panel.
     * @param mode the new paste mode.
     */
    public void setPasteMode(boolean mode) {
        isPasteMode = mode;
        diagram.setPasteMode(mode);
    }

    /**
     * Set the double cut select mode of the panel.
     * @param mode the new double cut mode.
     */
    public void setDcMode(boolean mode) {
        isDcMode = mode;
        diagram.setDcMode(mode);
    }

    /**
     * Retrieves the list of select literal diagrams.
     * @return the list of select literal diagrams.
     */
    public List<LiteralDiagram> getSelectedLiterals() {
        return diagram.getSelectedLiterals();
    }
}
