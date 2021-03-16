package ui;

import javax.swing.*;
import java.awt.*;

/*
References:
https://www.guru99.com/java-swing-gui.html
 */
public class Ui {
    public void construct() {
        // Frame creation
        JFrame frame = new JFrame("Peirce Alpha Proof Assistant");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Panel setup
        JMenuBar menuBar = new MenuBar();
        JScrollPane inputPanel = new InputPanel();

        JPanel panel = new JPanel();
        JPanel proofPanel = new JPanel();
        proofPanel.add(new JLabel("test 2"));
        //panel.add(inputPanel);
        panel.add(proofPanel);

        // Frame building
        //JButton button = new JButton("Test");
        frame.setVisible(true);
        frame.setJMenuBar(menuBar);
        frame.getContentPane().add(panel);
        frame.pack();
        frame.setSize(900, 600);
        frame.getContentPane().add(BorderLayout.WEST, inputPanel);
        //frame.getContentPane().add(BorderLayout.SOUTH, button);
    }
}
