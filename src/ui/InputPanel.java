package ui;

import logic.Logic;
import logic.exceptions.TheoremParseException;
import logic.exceptions.VariableNameException;
import model.Proposition;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

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
    private final List<Card> variables;
    private final JPanel premiseHeader;
    private final JTextField premiseInput;
    private final List<Card> premises;
    private final JPanel langSelectorPanel;
    private final JPanel theoremHeader;
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
        variables = new ArrayList<>();
        variableInput = new JTextField();

        constructListPanel("Variable", variableHeader, variables,
                variableInput, i -> {
                    try {
                        logic.addVariable(i);
                        return true;
                    } catch (VariableNameException e) {
                        JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), e.getMessage(),
                                "Invalid Variable Name Error", JOptionPane.ERROR_MESSAGE);
                        return false;
                    }
                }, logic::getVariables, VariableCard.class);

        langSelectorPanel = new JPanel();
        JComboBox<String> langSelector = new JComboBox<>(Logic.languages);
        langSelectorPanel.add(new JLabel("Language: "));
        langSelectorPanel.add(langSelector);
        langSelectorPanel.setLayout(new FlowLayout(FlowLayout.LEFT,3,3));

        premiseHeader = new JPanel();
        premises = new ArrayList<>();
        premiseInput = new JTextField();

        constructListPanel("Premise", premiseHeader, premises,
                premiseInput, i -> {
                    try {
                        logic.addPremise(i);
                        return true;
                    } catch (TheoremParseException e) {
                        JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), e.getMessage(),
                                "Invalid Premise Error", JOptionPane.ERROR_MESSAGE);
                        return false;
                    }
                }, logic::getPremises, PremiseCard.class);

        theoremHeader = new JPanel();
        theoremHeader.setLayout(new FlowLayout(FlowLayout.LEFT,3,3));
        JLabel theoremLabel = new JLabel("Want to prove: ");
        theoremHeader.add(theoremLabel);

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
                    logic.setTheorem(result);
                    startProofBtn.setText(PROOF_TO_DECLARATION_BTN_MSG);
                    logic.setLanguage((String) langSelector.getSelectedItem());
                    for (JButton button : buttons) {
                        button.setEnabled(false);
                        variableInput.setEditable(false);
                        premiseInput.setEditable(false);
                        theoremInput.setEditable(false);
                    }
                    logic.switchMode();
                    for (Card card : variables) {
                        card.switchEditable();
                    }
                    for (Card card : premises) {
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
                }
                variableInput.setEditable(true);
                premiseInput.setEditable(true);
                theoremInput.setEditable(true);
                JOptionPane.showMessageDialog(JOptionPane.getRootFrame(),
                        "Records in the proof panel will be clicked",
                        "Warning",
                        JOptionPane.WARNING_MESSAGE);
                refreshParent.run();
                logic.switchMode();
                for (Card card : variables) {
                    card.switchEditable();
                }
                for (Card card : premises) {
                    card.switchEditable();
                }
            }
        });

        constructPanel();
    }

    /**
     * Constructs a list panel of add, edit, and delete implementation
     * @param typeStr the string representing the type.
     * @param header the header JPanel.
     * @param cards the list of cards to display.
     * @param inputField the input field for add and edit.
     * @param add the add implementation function.
     * @param getContents the supplier of the contents of entire list.
     * @param type the class of the list.
     */
    private void constructListPanel(String typeStr, JPanel header, List<Card> cards,
                                    JTextField inputField, Predicate<String> add, Supplier<List<String>> getContents,
                                    Class<? extends Card> type) {
        header.setLayout(new FlowLayout(FlowLayout.LEFT,3,3));
        JLabel label = new JLabel(String.format("Add %s: ", typeStr));
        JButton addBtn = new JButton("Add");
        addBtn.setText("Add");
        header.add(label);
        header.add(addBtn);
        buttons.add(addBtn);

        inputField.setMaximumSize(new Dimension(290, 30));
        addBtn.addActionListener(e -> {
            try {
                String input = inputField.getText();
                boolean isValid = add.test(input);
                if (! isValid) {
                    return;
                }
                cards.add(type
                        .getConstructor(Logic.class, String.class, List.class, JTextField.class, Runnable.class)
                        .newInstance(logic, input, cards, inputField, (Runnable) this::constructPanel));
                inputField.setText("");
                constructPanel();
            } catch (Exception exception) {
                JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), exception.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        for (String content : getContents.get()) {
            cards.add(new VariableCard(logic, content, cards, inputField, this::constructPanel));
        }
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
        for (Card variableCard : variables) {
            panel.add(variableCard);
        }
        panel.add(langSelectorPanel);
        panel.add(premiseHeader);
        panel.add(premiseInput);
        for (Card premiseCard : premises) {
            panel.add(premiseCard);
        }
        panel.add(theoremHeader);
        panel.add(theoremInput);
        panel.add(startProofBtnPanel);
    }
}
