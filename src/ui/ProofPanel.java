package ui;

import static javax.swing.JOptionPane.YES_OPTION;
import logic.Logic;
import logic.exceptions.InvalidInferenceException;
import logic.exceptions.InvalidSelectionException;
import logic.exceptions.RedoException;
import logic.exceptions.TheoremParseException;
import logic.exceptions.UndoException;
import model.Inference;
import model.Literal;
import model.Proposition;
import static ui.Ui.DC_IMG;
import static ui.Ui.RDC_IMG;
import ui.diagram.LiteralDiagram;

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
import java.util.function.Consumer;

/**
 * The panel that the user constructs the reasoning for proof.
 */
public class ProofPanel extends JPanel {
    private static final int resultDisplayLength = 55;
    public static Consumer<Proposition> paste;
    public static Consumer<Proposition> insertDoubleCut;

    private final Logic logic;
    private final JLabel goalDisplay;
    private final JTextArea theoremDisplay;
    private final JLabel resultDisplay;
    private final JPanel historyPanel;
    private final DiagramPanel currentDiagram;
    private final DiagramPanel draftDiagram;
    private final DiagramPanel goalDiagram;
    private final Clipboard clipboard;
    private final List<JLabel> historyLabels;
    private final List<JButton> buttons;
    private List<LiteralDiagram> copied;

    /**
     * Constructs the proof panel.
     * @param logic the logic component that the application is built based on.
     */
    public ProofPanel(Logic logic) {
        super();
        this.logic = logic;
        this.clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        this.buttons = new ArrayList<>();
        this.copied = new ArrayList<>();

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JPanel labelPanel = new JPanel();
        labelPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 3, 3));
        labelPanel.add(new JLabel("Current Proposition:"));

        JPanel workPanel = new JPanel();
        Icon dciTxtIcon = new ImageIcon(new ImageIcon(DC_IMG).getImage()
                .getScaledInstance(25,25, Image.SCALE_SMOOTH));
        JButton addDoubleCutBtn = new JButton(dciTxtIcon);
        Icon dceTxtIcon = new ImageIcon(new ImageIcon(RDC_IMG).getImage()
                .getScaledInstance(25,25,Image.SCALE_SMOOTH));
        JButton removeDoubleCutBtn = new JButton(dceTxtIcon);
        theoremDisplay = textProofSetup(workPanel, addDoubleCutBtn, removeDoubleCutBtn);

        JButton undoBtn = new JButton("Undo");
        JButton redoBtn = new JButton("Redo");
        historyPanel = new JPanel();
        historyLabels = new ArrayList<>();
        JScrollPane historyPane = historyPanelSetup(labelPanel, undoBtn, redoBtn);

        JButton copyBtn = new JButton("Copy");
        JButton cutBtn = new JButton("Cut");
        JButton pasteBtn = new JButton("Paste");
        Icon dciDiagramIcon = new ImageIcon(new ImageIcon(DC_IMG).getImage()
                .getScaledInstance(18,18, Image.SCALE_SMOOTH));
        Icon dceDiagramIcon = new ImageIcon(new ImageIcon(RDC_IMG).getImage()
                .getScaledInstance(18,18, Image.SCALE_SMOOTH));
        JButton dciBtn = new JButton(dciDiagramIcon);
        JButton dceBtn = new JButton(dceDiagramIcon);
        JPanel drawHeader = drawHeaderSetup(copyBtn, cutBtn, pasteBtn, dciBtn, dceBtn);

        JButton drawDraftBtn = new JButton("Draw");
        JPanel diagramPanel = new JPanel();
        JTextArea draftInput = new JTextArea();
        draftDiagram = new DiagramPanel("Draft Diagram: ", new Proposition(), 180, 140);
        currentDiagram = new DiagramPanel("Current Diagram: ",
                logic.getProposition(), 400, 220);
        diagramPanelSetup(logic, drawDraftBtn, diagramPanel, draftInput, currentDiagram, draftDiagram);

        JPanel infoPanel = new JPanel();
        goalDisplay = new JLabel(String.format("Goal: %s", logic.getTheorem()));
        goalDiagram = new DiagramPanel("Goal diagram: ", logic.getTheorem(), 280, 100);
        resultDisplay = new JLabel();
        infoPanelSetup(historyPane, infoPanel);

        actionListenersSetup(addDoubleCutBtn, removeDoubleCutBtn, undoBtn, redoBtn, drawDraftBtn,
                draftInput, copyBtn, cutBtn, pasteBtn, dciBtn, dceBtn);

        add(labelPanel);
        add(workPanel);
        add(drawHeader);
        add(diagramPanel);
        add(infoPanel);
        for (JButton b : buttons) {
            b.setEnabled(false);
        }
    }

    private void actionListenersSetup(
            JButton addDoubleCutBtn, JButton removeDoubleCutBtn,
            JButton undoBtn, JButton redoBtn, JButton drawDraftBtn, JTextArea draftInput,
            JButton copyBtn, JButton cutBtn, JButton pasteBtn, JButton dciBtn, JButton dceBtn) {
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
                currentDiagram.refresh(logic.getProposition());
                revalidate();
                repaint();
            } catch (UndoException err) {
                displayError(err);
            }
        });
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
                currentDiagram.refresh(logic.getProposition());
                revalidate();
                repaint();
            } catch (RedoException err) {
                displayError(err);
            }
        });
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
        drawDraftBtn.addActionListener(e -> {
            String diagramCanonical = draftInput.getText();
            try {
                Proposition draftProp = logic.parseFrame(diagramCanonical);
                draftDiagram.refresh(draftProp);
                draftDiagram.setSelectMode(true);
                resultDisplay.setText("");
            } catch (TheoremParseException tpe) {
                displayError(tpe);
            }
        });
        copyBtn.addActionListener(e -> {
            List<LiteralDiagram> propSelected = currentDiagram.getSelectedLiterals();
            List<LiteralDiagram> draftSelected = draftDiagram.getSelectedLiterals();
            if (!propSelected.isEmpty() && !draftSelected.isEmpty()) {
                Object[] options = {"From draft", "From proposition"};
                int fromDraft = JOptionPane.showOptionDialog(JOptionPane.getRootFrame(),
                        "Cannot copy from two different diagrams. \n Where do you want to copy from?",
                        "Copy Inquiry",
                        JOptionPane.YES_NO_CANCEL_OPTION,
                        JOptionPane.WARNING_MESSAGE,
                        null,
                        options,
                        options[1]);
                if (fromDraft == YES_OPTION) {
                    copied = draftSelected;
                } else {
                    copied = propSelected;
                }
            } else if (propSelected.isEmpty()) {
                copied = draftSelected;
            } else {
                copied = propSelected;
            }
        });
        cutBtn.addActionListener(e -> {
            List<LiteralDiagram> selectedLiteralDiagrams = currentDiagram.getSelectedLiterals();
            if (!selectedLiteralDiagrams.isEmpty()) {
                List<Literal> literals = new ArrayList<>();
                for (LiteralDiagram ld : selectedLiteralDiagrams) {
                    literals.add(ld.getLiteral());
                }
                Proposition parent = literals.get(0).getParent();
                try {
                    logic.cut(literals, parent);
                    updateResult();
                    checkSuccess();
                } catch (InvalidInferenceException err) {
                    displayError(err);
                }
            }
        });
        pasteBtn.addActionListener(e -> {
            if (pasteBtn.getText().equals("Paste")) {
                pasteBtn.setText("Exit paste");
                paste = (prop) -> {
                    if (!copied.isEmpty()) {
                        Proposition toInsert = new Proposition();
                        List<Literal> literals = new ArrayList<>();
                        for (LiteralDiagram ld : copied) {
                            literals.add(ld.getLiteral().copy());
                        }
                        toInsert.addLiterals(literals);
                        for (Literal l : literals) {
                            l.setParent(toInsert);
                        }
                        try {
                            logic.paste(toInsert, prop, prop.getStartIndex());
                            updateResult();
                            checkSuccess();
                        } catch (InvalidSelectionException | InvalidInferenceException err) {
                            displayError(err);
                        }
                    }
                    pasteBtn.setText("Paste");
                    checkSuccess();
                };
                currentDiagram.setPasteMode(true);
            } else {
                pasteBtn.setText("Paste");
                paste = (prop) -> {};
                currentDiagram.setPasteMode(false);
            }
        });
        dciBtn.addActionListener(e -> {
            List<LiteralDiagram> selectedLiteralDiagrams = currentDiagram.getSelectedLiterals();
            if (selectedLiteralDiagrams.isEmpty()) {
                currentDiagram.setDcMode(true);
                insertDoubleCut = (prop) -> {
                    try {
                        if (prop.isBaseProp()) {
                            logic.addDoubleCut(new ArrayList<>(), logic.getProposition(), 0);
                        } else {
                            logic.addDoubleCut(new ArrayList<>(), prop, prop.getStartIndex());
                        }
                        updateResult();
                        currentDiagram.setDcMode(false);
                        checkSuccess();
                    } catch (InvalidSelectionException err) {
                        displayError(err);
                    }
                };
            } else {
                List<Literal> literals = new ArrayList<>();
                for (LiteralDiagram ld : selectedLiteralDiagrams) {
                    literals.add(ld.getLiteral());
                }
                Proposition parent = literals.get(0).getParent();
                try {
                    logic.addDoubleCut(literals, parent, parent.getStartIndex());
                    updateResult();
                    checkSuccess();
                } catch (InvalidSelectionException err) {
                    displayError(err);
                }
            }
        });
        dceBtn.addActionListener(e -> {
            List<LiteralDiagram> selectedLiterals = currentDiagram.getSelectedLiterals();
            if (!selectedLiterals.isEmpty()) {
                List<Literal> literals = new ArrayList<>();
                for (LiteralDiagram ld : selectedLiterals) {
                    literals.add(ld.getLiteral());
                }
                Proposition parent = literals.get(0).getParent();
                try {
                    logic.removeDoubleCut(literals, parent);
                    updateResult();
                    checkSuccess();
                } catch (InvalidInferenceException err) {
                    displayError(err);
                }
            }
        });
    }

    private void infoPanelSetup(JScrollPane historyPane, JPanel infoPanel) {
        infoPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 3, 3));
        JPanel textInfoPanel = new JPanel();
        textInfoPanel.setLayout(new BoxLayout(textInfoPanel, BoxLayout.Y_AXIS));
        textInfoPanel.setPreferredSize(new Dimension(300, 170));
        JPanel goalPanel = new JPanel();
        goalPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 3, 3));
        goalPanel.add(goalDisplay);
        textInfoPanel.add(goalPanel);
        textInfoPanel.add(historyPane);
        JPanel graphInfoPanel = new JPanel();
        graphInfoPanel.setLayout(new BoxLayout(graphInfoPanel, BoxLayout.Y_AXIS));
        graphInfoPanel.setPreferredSize(new Dimension(280, 170));
        graphInfoPanel.add(goalDiagram);
        JPanel resultPanel = new JPanel();
        resultPanel.setLayout(new FlowLayout(FlowLayout.LEFT,3,3));
        resultPanel.add(resultDisplay);
        graphInfoPanel.add(resultPanel);
        infoPanel.add(textInfoPanel);
        infoPanel.add(graphInfoPanel);
    }

    private void diagramPanelSetup(Logic logic, JButton drawDraftBtn, JPanel diagramPanel, JTextArea draftInput,
                                   DiagramPanel currentDiagram, DiagramPanel draftDiagram) {
        diagramPanel.setLayout(new FlowLayout(FlowLayout.LEFT,3,3));
        diagramPanel.add(currentDiagram);
        JPanel draftPanel = new JPanel();
        draftPanel.setLayout(new BoxLayout(draftPanel, BoxLayout.Y_AXIS));
        JPanel draftHeaderPanel = new JPanel();
        draftHeaderPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 3, 3));
        draftHeaderPanel.add(new JLabel("Draft:"));
        buttons.add(drawDraftBtn);
        drawDraftBtn.setToolTipText("Convert the canonical diagram string to diagram");
        draftHeaderPanel.add(drawDraftBtn);
        draftPanel.add(draftHeaderPanel);
        draftInput.setLineWrap(true);
        draftInput.setAutoscrolls(true);
        draftInput.setMargin(new Insets(5, 5, 5, 5));
        draftInput.setPreferredSize(new Dimension(180, 45));
        draftPanel.add(draftInput);
        draftPanel.add(draftDiagram);
        diagramPanel.add(draftPanel);
    }

    private JPanel drawHeaderSetup(JButton copyBtn, JButton cutBtn, JButton pasteBtn, JButton dciBtn, JButton dceBtn) {
        buttons.add(copyBtn);
        buttons.add(cutBtn);
        buttons.add(pasteBtn);
        buttons.add(dciBtn);
        buttons.add(dceBtn);
        JPanel drawHeader = new JPanel();
        drawHeader.setLayout(new FlowLayout(FlowLayout.LEFT,3,3));
        drawHeader.add(new JLabel("Graph: "));
        drawHeader.add(copyBtn);
        drawHeader.add(cutBtn);
        drawHeader.add(pasteBtn);
        drawHeader.add(dciBtn);
        drawHeader.add(dceBtn);
        dciBtn.setToolTipText("Wrap with double cut");
        dceBtn.setToolTipText("Remove outer double cut");
        return drawHeader;
    }

    private JScrollPane historyPanelSetup(JPanel labelPanel, JButton undoBtn, JButton redoBtn) {
        historyPanel.setLayout(new BoxLayout(historyPanel, BoxLayout.Y_AXIS));
        JScrollPane historyPane = new JScrollPane(historyPanel);
        historyPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        historyPane.setPreferredSize(new Dimension(400, 255));
        buttons.add(undoBtn);
        labelPanel.add(undoBtn);
        buttons.add(redoBtn);
        labelPanel.add(redoBtn);
        return historyPane;
    }

    private JTextArea textProofSetup(JPanel workPanel, JButton addDoubleCutBtn, JButton removeDoubleCutBtn) {
        final JTextArea theoremDisplay;
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
        theoremDisplay.setPreferredSize(new Dimension(500, 25));
        workPanel.add(theoremDisplay);
        JPanel btnPanel = new JPanel();
        btnPanel.setLayout(new FlowLayout(FlowLayout.LEFT,3,3));
        buttons.add(addDoubleCutBtn);
        btnPanel.add(addDoubleCutBtn);
        buttons.add(removeDoubleCutBtn);
        btnPanel.add(removeDoubleCutBtn);
        workPanel.add(btnPanel);
        addDoubleCutBtn.setToolTipText("Double Cut Introduction");
        removeDoubleCutBtn.setToolTipText("Double Cut Elimination");
        return theoremDisplay;
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
        currentDiagram.refresh(logic.getProposition());
        currentDiagram.setSelectMode(true);
        draftDiagram.setSelectMode(true);
        goalDiagram.refresh(logic.getTheorem());
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
        String lastLog = logic.getLastLog();
        StringBuilder result = new StringBuilder();
        while (lastLog.length() > resultDisplayLength) {
            String substring = lastLog.substring(0, resultDisplayLength);
            result.append(substring);
            result.append("<br/>");
            lastLog = lastLog.substring(resultDisplayLength);
        }
        result.append(lastLog);
        resultDisplay.setText(String.format("<html>%s</html>", result));
        resultDisplay.setForeground(Color.BLACK);
        JLabel logResult = new JLabel(logic.getLastLog());
        historyLabels.add(logResult);
        historyPanel.add(logResult);
        historyPanel.revalidate();
        historyPanel.repaint();
        currentDiagram.refresh(logic.getProposition());
        currentDiagram.setSelectMode(true);
        draftDiagram.setSelectMode(true);
        goalDiagram.refresh(logic.getTheorem());
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
