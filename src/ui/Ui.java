package ui;

import model.Model;

import javax.swing.*;
import java.awt.*;

/*
References:
https://www.guru99.com/java-swing-gui.html
 */
public class Ui {
    private final JFrame frame;

    public Ui() {
        frame = new JFrame("Peirce Alpha Proof Assistant");
    }

    public void construct(Model model) {
        // Frame creation
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Panel setup
        JMenuBar menuBar = new MenuBar();
        JScrollPane inputPanel = new InputPanel(model);

        JPanel panel = new JPanel();
        JPanel proofPanel = new JPanel();
        proofPanel.add(new JLabel("test 2"));
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
