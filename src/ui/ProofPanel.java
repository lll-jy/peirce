package ui;

import logic.Logic;
import logic.exceptions.InvalidInferenceException;
import logic.exceptions.InvalidSelectionException;
import logic.exceptions.RedoException;
import logic.exceptions.TheoremParseException;
import logic.exceptions.UndoException;
import model.Inference;
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
    private final JLabel resultDisplay;
    private final JPanel historyPanel;
    private final Clipboard clipboard;
    private final List<JLabel> historyLabels;
    private final List<JButton> buttons;

    /**
     * Constructs the proof panel.
     * @param logic the logic component that the application is built based on.
     */
    public ProofPanel(Logic logic) {
        super();
        this.logic = logic;
        this.clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        this.buttons = new ArrayList<>();

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JPanel goalPanel = new JPanel();
        goalDisplay = new JLabel(String.format("Goal: %s", logic.getTheorem()));
        goalPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 3, 3));
        goalPanel.add(goalDisplay);

        JPanel labelPanel = new JPanel();
        labelPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 3, 3));
        labelPanel.add(new JLabel("Current Proposition:"));

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
        theoremDisplay.setPreferredSize(new Dimension(500, 25));
        workPanel.add(theoremDisplay);
        JPanel btnPanel = new JPanel();
        btnPanel.setLayout(new FlowLayout(FlowLayout.LEFT,3,3));
        Icon dciTxtIcon = new ImageIcon(new ImageIcon(DC_IMG).getImage()
                        .getScaledInstance(25,25, Image.SCALE_SMOOTH));
        JButton addDoubleCutBtn = new JButton(dciTxtIcon);
        buttons.add(addDoubleCutBtn);
        btnPanel.add(addDoubleCutBtn);
        Icon dceTxtIcon = new ImageIcon(new ImageIcon(RDC_IMG).getImage()
                .getScaledInstance(25,25,Image.SCALE_SMOOTH));
        JButton removeDoubleCutBtn = new JButton(dceTxtIcon);
        buttons.add(removeDoubleCutBtn);
        btnPanel.add(removeDoubleCutBtn);
        workPanel.add(btnPanel);

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

        JPanel helperToolPanel = new JPanel();
        helperToolPanel.setLayout(new FlowLayout(FlowLayout.LEFT,3,3));
        historyPanel = new JPanel();
        historyPanel.setLayout(new BoxLayout(historyPanel, BoxLayout.Y_AXIS));
        JScrollPane historyPane = new JScrollPane(historyPanel);
        historyPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        historyPane.setPreferredSize(new Dimension(400, 355));
        helperToolPanel.add(historyPane);
        historyLabels = new ArrayList<>();
        JButton undoBtn = new JButton("Undo");
        undoBtn.addActionListener(e -> {
            try {
                logic.undo();
                historyPanel.removeAll();
                historyLabels.remove(historyLabels.size() - 1);
                for (JLabel l : historyLabels) {
                    historyPanel.add(l);
                }
                theoremDisplay.setText(logic.getProposition().toString());
                resultDisplay.setForeground(Color.BLACK);
                resultDisplay.setText("Undo");
                revalidate();
                repaint();
            } catch (UndoException err) {
                displayError(err);
            }
        });
        buttons.add(undoBtn);
        labelPanel.add(undoBtn);
        JButton redoBtn = new JButton("Redo");
        redoBtn.addActionListener(e -> {
            try {
                Inference inference = logic.redo();
                historyPanel.removeAll();;
                historyLabels.add(new JLabel(inference.userDisplay()));
                for (JLabel l : historyLabels) {
                    historyPanel.add(l);
                }
                theoremDisplay.setText(logic.getProposition().toString());
                resultDisplay.setForeground(Color.BLACK);
                resultDisplay.setText("Redo");
                revalidate();
                repaint();
            } catch (RedoException err) {
                displayError(err);
            }
        });
        buttons.add(redoBtn);
        labelPanel.add(redoBtn);
        JPanel draftPanel = new JPanel();
        draftPanel.setLayout(new BoxLayout(draftPanel, BoxLayout.Y_AXIS));
        JPanel draftLabelPanel = new JPanel();
        draftLabelPanel.setLayout(new FlowLayout(FlowLayout.LEFT,3,3));
        draftLabelPanel.add(new JLabel("<html>You may do some <br/>rough work here:</html>"));
        draftPanel.add(draftLabelPanel);
        JTextArea roughWorkArea = new JTextArea();
        roughWorkArea.setLineWrap(true);
        roughWorkArea.setAutoscrolls(true);
        roughWorkArea.setMargin(new Insets(5, 5, 5, 5));
        roughWorkArea.setPreferredSize(new Dimension(160, 300));
        draftPanel.add(roughWorkArea);
        helperToolPanel.add(draftPanel);

        JPanel drawHeader = new JPanel();
        drawHeader.setLayout(new FlowLayout(FlowLayout.LEFT,3,3));
        drawHeader.add(new JLabel("Graph: "));
        JButton copyBtn = new JButton("Copy");
        JButton cutBtn = new JButton("Cut");
        JButton pasteBtn = new JButton("Paste");
        Icon dciDiagramIcon = new ImageIcon(new ImageIcon(DC_IMG).getImage()
                .getScaledInstance(18,18, Image.SCALE_SMOOTH));
        Icon dceDiagramIcon = new ImageIcon(new ImageIcon(RDC_IMG).getImage()
                .getScaledInstance(18,18, Image.SCALE_SMOOTH));
        JButton dciBtn = new JButton(dciDiagramIcon);
        JButton dceBtn = new JButton(dceDiagramIcon);
        buttons.add(copyBtn);
        buttons.add(cutBtn);
        buttons.add(pasteBtn);
        buttons.add(dciBtn);
        buttons.add(dceBtn);
        drawHeader.add(copyBtn);
        drawHeader.add(cutBtn);
        drawHeader.add(pasteBtn);
        drawHeader.add(dciBtn);
        drawHeader.add(dceBtn);
        dciBtn.setToolTipText("Wrap with double cut");
        dceBtn.setToolTipText("Remove outer double cut");

        JPanel diagramPanel = new JPanel();
        diagramPanel.setLayout(new FlowLayout(FlowLayout.LEFT,3,3));

        add(goalPanel);
        add(labelPanel);
        add(workPanel);
        add(drawHeader);
        add(resultPanel);
        add(helperToolPanel);
        for (JButton b : buttons) {
            b.setEnabled(false);
        }
    }

    /**
     * Refresh this panel.
     */
    public void refresh() {
        revalidate();
        repaint();
        boolean proofEditable = !logic.canModifyDeclaration();
        for (JButton b : buttons) {
            b.setEnabled(proofEditable);
        }
        goalDisplay.setText(String.format("Goal: %s", logic.getTheorem()));
        theoremDisplay.setText(logic.getProposition().toString());
        resultDisplay.setText("");
        resultDisplay.setForeground(Color.BLACK);
        historyPanel.removeAll();
        historyLabels.clear();
        for (Inference i : logic.getHistory()) {
            JLabel l = new JLabel(i.userDisplay());
            historyLabels.add(l);
            historyPanel.add(l);
        }
        checkSuccess();
    }

    /**
     * Checks whether the proof succeeds and show a dialogue if yes.
     */
    private void checkSuccess() {
        if (logic.succeeds()) {
            for (JButton b : buttons) {
                b.setEnabled(false);
            }
            historyPanel.add(new JLabel("Q.E.D."));
            historyPanel.revalidate();
            historyPanel.repaint();
            JOptionPane.showMessageDialog(JOptionPane.getRootFrame(),
                    "Congratulations! Proof succeeded!",
                    "Success Notice",
                    JOptionPane.INFORMATION_MESSAGE);
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
