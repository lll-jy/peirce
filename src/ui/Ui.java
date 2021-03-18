package ui;

import logic.Logic;
import model.Proposition;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

/**
 * The UI component of the application.
 */
public class Ui {
    private final JFrame frame;

    /**
     * Initialize a UI frame of Peirce Alpha Proof Assistant.
     */
    public Ui() {
        frame = new JFrame("Peirce Alpha Proof Assistant");
    }

    /**
     * Constructs content of the UI.
     * @param logic the component that handles with logic.
     */
    public void construct(Logic logic) {
        // Frame creation
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Panel setup
        ProofPanel proofPanel = new ProofPanel(logic);
        JPanel panel = new JPanel();
        JMenuBar menuBar = new MenuBar();
        JScrollPane inputPanel = new InputPanel(logic,  () -> {
            panel.revalidate();
            panel.repaint();
            proofPanel.refresh();
        });

        //JPanel proofPanel = new JPanel();
        //proofPanel.add(new JLabel("test 2"));
        //panel.add(proofPanel);
        // TODO
        panel.add(proofPanel);

        // Frame building
        frame.setVisible(true);
        frame.setJMenuBar(menuBar);
        frame.getContentPane().add(panel);
        frame.pack();
        frame.setSize(900, 600);
        frame.getContentPane().add(BorderLayout.WEST, inputPanel);
    }
}
