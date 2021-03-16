package ui;

import javax.swing.*;
import java.awt.*;

public class InputPanel extends JScrollPane {
    private static JPanel panel = new JPanel();
    public InputPanel() {
        super(panel);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setPreferredSize(new Dimension(150, 570));
        for (int i = 0; i < 100; i++) {
            panel.add(new JLabel("test " + i + "\n"));
        }
    }
}
