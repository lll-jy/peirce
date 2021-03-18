package ui;

import logic.Logic;
import logic.exceptions.TheoremParseException;
import logic.exceptions.VariableNameException;
import model.Proposition;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * The panel that handles variable and theorem declaration.
 */
public class InputPanel extends JScrollPane {
    private static JPanel panel = new JPanel();
    private static String DECLARATION_TO_PROOF_BTN_MSG = "Start proof";
    private static String PROOF_TO_DECLARATION_BTN_MSG = "Edit declaration";

    private final Logic logic;
    private final Runnable refreshParent;
    private final JPanel variableHeader;
    private final JTextField variableInput;
    private final List<VariableCard> variables;
    private final JPanel langSelectorPanel;
    private final JTextArea theoremInput;
    private final JPanel startProofBtnPanel;
    private final List<JButton> buttons;

    /**
     * Initializes an input panel.
     * @param logic the component that handles with the logic.
     * @param refreshParent a runnable component that refreshes parent panel.
     */
    public InputPanel(Logic logic, Runnable refreshParent) {
        super(panel);
        this.logic = logic;
        this.refreshParent = refreshParent;

        setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        setPreferredSize(new Dimension(300, 570));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        buttons = new ArrayList<>();

        variableHeader = new JPanel();
        variableHeader.setLayout(new FlowLayout(FlowLayout.LEFT,3,3));
        JLabel variableLabel = new JLabel("Add Variable: ");
        JButton addVariableBtn = new JButton("Add");
        addVariableBtn.setText("Add");
        variableHeader.add(variableLabel);
        variableHeader.add(addVariableBtn);
        buttons.add(addVariableBtn);

        variables = new ArrayList<>();
        variableInput = new JTextField();
        variableInput.setMaximumSize(new Dimension(290, 30));
        addVariableBtn.addActionListener(e -> {
            try {
                String input = variableInput.getText();
                logic.addVariable(input);
                variables.add(new VariableCard(logic, input, variables, variableInput, this::constructPanel));
                variableInput.setText("");
                constructPanel();
            } catch (VariableNameException vne) {
                JOptionPane.showMessageDialog(JOptionPane.getRootFrame(),
                        vne.getMessage(),
                        "Invalid Variable Name Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        for (String variableName : logic.getVariables()) {
            variables.add(new VariableCard(logic, variableName, variables, variableInput, this::constructPanel));
        }

        langSelectorPanel = new JPanel();
        JComboBox<String> langSelector = new JComboBox<>(Logic.languages);
        langSelectorPanel.add(new JLabel("Language: "));
        langSelectorPanel.add(langSelector);
        langSelectorPanel.setLayout(new FlowLayout(FlowLayout.LEFT,3,3));

        theoremInput = new JTextArea();
        theoremInput.setLineWrap(true);
        theoremInput.setMargin(new Insets(5, 5, 5, 5));

        startProofBtnPanel = new JPanel();
        JButton startProofBtn = new JButton(DECLARATION_TO_PROOF_BTN_MSG);
        startProofBtnPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 3, 3));
        startProofBtnPanel.add(startProofBtn);
        startProofBtn.addActionListener(e -> {
            if (logic.canModifyDeclaration()) {
                try {
                    Proposition result = logic.parse(theoremInput.getText());
                    logic.setProposition(result);
                    startProofBtn.setText(PROOF_TO_DECLARATION_BTN_MSG);
                    logic.setLanguage((String) langSelector.getSelectedItem());
                    for (JButton button : buttons) {
                        button.setEnabled(false);
                        variableInput.setEditable(false);
                        theoremInput.setEditable(false);
                    }
                    logic.switchMode();
                    for (VariableCard card : variables) {
                        card.switchEditable();
                    }
                    refreshParent.run();
                } catch (TheoremParseException tpe) {
                    JOptionPane.showMessageDialog(JOptionPane.getRootFrame(),
                            tpe.getMessage(),
                            "", JOptionPane.ERROR_MESSAGE
                    );
                }
            } else {
                startProofBtn.setText(DECLARATION_TO_PROOF_BTN_MSG);
                for (JButton button : buttons) {
                    button.setEnabled(true);
                    variableInput.setEditable(true);
                    theoremInput.setEditable(true);
                }
                JOptionPane.showMessageDialog(JOptionPane.getRootFrame(),
                        "Records in the proof panel will be clicked",
                        "Warning",
                        JOptionPane.WARNING_MESSAGE);
                refreshParent.run();
                logic.switchMode();
                for (VariableCard card : variables) {
                    card.switchEditable();
                }
            }
        });

        constructPanel();
    }

    /**
     * Refresh this panel.
     */
    private void constructPanel() {
        panel.removeAll();
        panel.revalidate();
        panel.repaint();
        panel.add(variableHeader);
        panel.add(variableInput);
        for (VariableCard variableCard : variables) {
            panel.add(variableCard);
        }
        panel.add(langSelectorPanel);
        panel.add(theoremInput);
        panel.add(startProofBtnPanel);
    }
}
