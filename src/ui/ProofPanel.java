package ui;

import logic.Logic;
import logic.exceptions.InvalidInferenceException;
import logic.exceptions.InvalidSelectionException;
import logic.exceptions.TheoremParseException;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;

public class ProofPanel extends JPanel {
    private final Logic logic;
    private final JPanel goalPanel;
    private final JLabel goalDisplay;
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

        goalPanel = new JPanel();
        goalDisplay = new JLabel(String.format("Goal: %s", logic.getTheorem()));
        goalPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 3, 3));
        goalPanel.add(goalDisplay);
        add(goalPanel);

        labelPanel = new JPanel();
        labelPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 3, 3));
        labelPanel.add(new JLabel("Current Proposition:"));
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

            // TODO: logging
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_X && e.isControlDown()) {
                    try {
                        int start = logic.getTokenIndex(theoremDisplay.getSelectionStart());
                        int end = logic.getTokenIndex(theoremDisplay.getSelectionEnd());
                        logic.cut(start, end);
                        theoremDisplay.setText(logic.getProposition().toString());
                        resultDisplay.setText(String.format("%d to %d removed", start, end));
                    } catch (InvalidSelectionException | InvalidInferenceException err) {
                        resultDisplay.setText(err.getMessage());
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_V && e.isControlDown()) {
                    try {
                        int pos = logic.getTokenIndex(theoremDisplay.getSelectionStart());
                        if (pos != logic.getTokenIndex(theoremDisplay.getSelectionEnd())) {
                            throw new InvalidSelectionException("Please place your cursor in a deterministic " +
                                    "position to insert new propositions.");
                        }
                        String toInsert = clipboard.getData(DataFlavor.stringFlavor).toString();
                        logic.paste(pos, toInsert);
                        theoremDisplay.setText(logic.getProposition().toString());
                        resultDisplay.setText(String.format("%s inserted to %d", toInsert, pos));
                    } catch (InvalidSelectionException | InvalidInferenceException | TheoremParseException |
                            UnsupportedFlavorException | IOException err) {
                        resultDisplay.setText(err.getMessage());
                    }
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
        addDoubleCutBtn.addActionListener(e -> {
            try {
                int start = logic.getTokenIndex(theoremDisplay.getSelectionStart());
                int end = logic.getTokenIndex(theoremDisplay.getSelectionEnd());
                logic.addDoubleCut(start, end);
                theoremDisplay.setText(logic.getProposition().toString());
                resultDisplay.setText(String.format("Add double cut from %d to %d", start, end));
            } catch (InvalidSelectionException err) {
                resultDisplay.setText(err.getMessage());
            }
        });
        removeDoubleCutBtn.addActionListener(e -> {
            try {
                int start = logic.getTokenIndex(theoremDisplay.getSelectionStart());
                int end = logic.getTokenIndex(theoremDisplay.getSelectionEnd());
                logic.removeDoubleCut(start, end);
                theoremDisplay.setText(logic.getProposition().toString());
                resultDisplay.setText(String.format("Double cut removed from %d to %d", start, end));
            } catch (InvalidSelectionException | InvalidInferenceException err) {
                resultDisplay.setText(err.getMessage());
            }
        });
        add(resultPanel);
    }

    /**
     * Refresh this panel.
     */
    public void refresh() {
        revalidate();
        repaint();
        goalDisplay.setText(String.format("Goal: %s", logic.getTheorem()));
        theoremDisplay.setText(logic.getProposition().toString());
        resultDisplay.setText("");
    }
}
