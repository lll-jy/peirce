package ui;

import logic.Logic;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class ProofPanel extends JPanel {
    private final Logic logic;
    private final JPanel labelPanel;
    private final JPanel workPanel;
    private final JTextArea theoremDisplay;
    private final JButton addDoubleCutBtn;
    private final JButton removeDoubleCutBtn;
    private final JPanel resultPanel;
    private final JLabel resultDisplay;
    private final Clipboard clipboard;

    public ProofPanel(Logic logic) {
        super();
        this.logic = logic;
        this.clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        labelPanel = new JPanel();
        labelPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 3, 3));
        labelPanel.add(new JLabel("Current Goal:"));
        add(labelPanel);

        workPanel = new JPanel();
        workPanel.setLayout(new FlowLayout(FlowLayout.LEFT,3,3));
        theoremDisplay = new JTextArea();
        theoremDisplay.setLineWrap(true);
        theoremDisplay.setAutoscrolls(true);
        theoremDisplay.setMargin(new Insets(5, 5, 5, 5));
        theoremDisplay.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                super.focusGained(e);
                theoremDisplay.setEditable(false);
                theoremDisplay.getCaret().setVisible(true);
            }

            @Override
            public void focusLost(FocusEvent e) {
                super.focusLost(e);
            }
        });
        theoremDisplay.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            // TODO
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_X && e.isControlDown()) {
                    resultDisplay.setText("cut");
                } else if (e.getKeyCode() == KeyEvent.VK_V && e.isControlDown()) {
                    resultDisplay.setText("paste");
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }
        });
        theoremDisplay.setPreferredSize(new Dimension(530, 100));
        workPanel.add(theoremDisplay);
        JPanel btnPanel = new JPanel();
        btnPanel.setLayout(new BoxLayout(btnPanel, BoxLayout.Y_AXIS));;
        Icon icon1 = new ImageIcon(new ImageIcon("images/double_cut.png").getImage()
                .getScaledInstance(25,25,Image.SCALE_SMOOTH));
        addDoubleCutBtn = new JButton(icon1);
        btnPanel.add(addDoubleCutBtn);
        Icon icon2 = new ImageIcon(new ImageIcon("images/remove_double_cut.png").getImage()
                .getScaledInstance(25,25,Image.SCALE_SMOOTH));
        removeDoubleCutBtn = new JButton(icon2);
        btnPanel.add(removeDoubleCutBtn);
        workPanel.add(btnPanel);
        add(workPanel);

        resultPanel = new JPanel();
        resultPanel.setLayout(new FlowLayout(FlowLayout.LEFT,3,3));
        resultDisplay = new JLabel();
        resultPanel.add(resultDisplay);
        // TODO
        addDoubleCutBtn.addActionListener(e -> {
            try {
                resultDisplay.setText(theoremDisplay.getSelectedText() + "\n" +
                        clipboard.getData(DataFlavor.stringFlavor));
            } catch (Exception exception) {
                resultDisplay.setText(exception.getMessage());
            }
        });
        removeDoubleCutBtn.addActionListener(e -> {
            resultDisplay.setText("" + logic.getTokenIndex(theoremDisplay.getCaretPosition()));
        });
        add(resultPanel);
    }

    /**
     * Refresh this panel.
     */
    public void refresh() {
        revalidate();
        repaint();
        theoremDisplay.setText(logic.getProposition().toString());
        resultDisplay.setText("");
    }
}
