package ui;

import logic.Logic;
import logic.exceptions.InvalidInferenceException;
import logic.exceptions.InvalidSelectionException;
import logic.exceptions.TheoremParseException;
import logic.exceptions.UndoException;
import static ui.Ui.DC_IMG;
import static ui.Ui.RDC_IMG;

import javax.swing.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * The panel that the user constructs the reasoning for proof.
 */
public class ProofPanel extends JPanel {
    private final Logic logic;
    private final JLabel goalDisplay;
    private final JTextArea theoremDisplay;
    private final JButton addDoubleCutBtn;
    private final JButton removeDoubleCutBtn;
    private final JLabel resultDisplay;
    private final JPanel historyPanel;
    private final Clipboard clipboard;
    private final List<JLabel> historyLabels;

    /**
     * Constructs the proof panel.
     * @param logic the logic component that the application is built based on.
     */
    public ProofPanel(Logic logic) {
        super();
        this.logic = logic;
        this.clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JPanel goalPanel = new JPanel();
        goalDisplay = new JLabel(String.format("Goal: %s", logic.getTheorem()));
        goalPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 3, 3));
        goalPanel.add(goalDisplay);
        add(goalPanel);

        JPanel labelPanel = new JPanel();
        labelPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 3, 3));
        labelPanel.add(new JLabel("Current Proposition:"));
        add(labelPanel);

        JPanel workPanel = new JPanel();
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

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_X && e.isControlDown()) {
                    try {
                        int start = logic.getTokenIndex(theoremDisplay.getSelectionStart());
                        int end = logic.getTokenIndex(theoremDisplay.getSelectionEnd());
                        logic.cut(start, end);
                        updateResult();
                    } catch (InvalidSelectionException | InvalidInferenceException err) {
                        displayError(err);
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
                        updateResult();
                    } catch (InvalidSelectionException | InvalidInferenceException | TheoremParseException |
                            UnsupportedFlavorException | IOException err) {
                        displayError(err);
                    }
                }
                checkSuccess();
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }
        });
        theoremDisplay.setPreferredSize(new Dimension(530, 100));
        workPanel.add(theoremDisplay);
        JPanel btnPanel = new JPanel();
        btnPanel.setLayout(new BoxLayout(btnPanel, BoxLayout.Y_AXIS));;
        Icon icon1 = new ImageIcon(new ImageIcon(DC_IMG).getImage()
                        .getScaledInstance(25,25, Image.SCALE_SMOOTH));
        addDoubleCutBtn = new JButton(icon1);
        addDoubleCutBtn.setEnabled(false);
        btnPanel.add(addDoubleCutBtn);
        Icon icon2 = new ImageIcon(new ImageIcon(RDC_IMG).getImage()
                .getScaledInstance(25,25,Image.SCALE_SMOOTH));
        removeDoubleCutBtn = new JButton(icon2);
        removeDoubleCutBtn.setEnabled(false);
        btnPanel.add(removeDoubleCutBtn);
        workPanel.add(btnPanel);
        add(workPanel);

        JPanel resultPanel = new JPanel();
        resultPanel.setLayout(new FlowLayout(FlowLayout.LEFT,3,3));
        resultDisplay = new JLabel();
        resultPanel.add(resultDisplay);
        addDoubleCutBtn.addActionListener(e -> {
            try {
                int start = logic.getTokenIndex(theoremDisplay.getSelectionStart());
                int end = logic.getTokenIndex(theoremDisplay.getSelectionEnd());
                logic.addDoubleCut(start, end);
                updateResult();
                checkSuccess();
            } catch (InvalidSelectionException err) {
                displayError(err);
            }
        });
        removeDoubleCutBtn.addActionListener(e -> {
            try {
                int start = logic.getTokenIndex(theoremDisplay.getSelectionStart());
                int end = logic.getTokenIndex(theoremDisplay.getSelectionEnd());
                logic.removeDoubleCut(start, end);
                updateResult();
                checkSuccess();
            } catch (InvalidSelectionException | InvalidInferenceException err) {
                displayError(err);
            }
        });
        addDoubleCutBtn.setToolTipText("Double Cut Introduction");
        removeDoubleCutBtn.setToolTipText("Double Cut Elimination");
        add(resultPanel);

        JPanel helperToolPanel = new JPanel();
        helperToolPanel.setLayout(new FlowLayout(FlowLayout.LEFT,3,3));
        historyPanel = new JPanel();
        historyPanel.setLayout(new BoxLayout(historyPanel, BoxLayout.Y_AXIS));
        JScrollPane historyPane = new JScrollPane(historyPanel);
        historyPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        historyPane.setPreferredSize(new Dimension(400, 370));
        helperToolPanel.add(historyPane);
        historyLabels = new ArrayList<>();
        // TODO: draft panel
        JButton undo = new JButton("Undo");
        undo.addActionListener(e -> {
            try {
                logic.undo();
                historyPanel.removeAll();
                System.out.println(historyLabels);
                historyLabels.remove(historyLabels.size() - 1);
                System.out.println(historyLabels);
                for (JLabel l : historyLabels) {
                    historyPanel.add(l);
                }
                theoremDisplay.setText(logic.getProposition().toString());
                resultDisplay.setText("Undo");
                revalidate();
                repaint();
            } catch (UndoException err) {
                displayError(err);
            }
        });
        labelPanel.add(undo);
        //JPanel draftPanel = new JPanel();
        add(helperToolPanel);
    }

    /**
     * Refresh this panel.
     */
    public void refresh() {
        revalidate();
        repaint();
        addDoubleCutBtn.setEnabled(true);
        removeDoubleCutBtn.setEnabled(true);
        goalDisplay.setText(String.format("Goal: %s", logic.getTheorem()));
        theoremDisplay.setText(logic.getProposition().toString());
        resultDisplay.setText("");
        resultDisplay.setForeground(Color.BLACK);
        historyPanel.removeAll();
        historyLabels.clear();
        checkSuccess();
    }

    /**
     * Checks whether the proof succeeds and show a dialogue if yes.
     */
    private void checkSuccess() {
        if (logic.succeeds()) {
            JOptionPane.showMessageDialog(JOptionPane.getRootFrame(),
                    "Congratulations! Proof succeeded!",
                    "Success Notice",
                    JOptionPane.INFORMATION_MESSAGE);
            addDoubleCutBtn.setEnabled(false);
            removeDoubleCutBtn.setEnabled(false);
            historyPanel.add(new JLabel("Q.E.D."));
        }
    }

    /**
     * Update the result after each step of inference.
     */
    private void updateResult() {
        theoremDisplay.setText(logic.getProposition().toString());
        resultDisplay.setText(logic.getLastLog());
        resultDisplay.setForeground(Color.BLACK);
        JLabel logResult = new JLabel(logic.getLastLog());
        historyLabels.add(logResult);
        historyPanel.add(logResult);
    }

    /**
     * Updates the result display given the exception.
     * @param e the exception caught.
     */
    private void displayError(Exception e) {
        resultDisplay.setText(e.getMessage());
        resultDisplay.setForeground(Color.RED);
    }
}
