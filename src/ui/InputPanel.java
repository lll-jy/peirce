package ui;

import logic.Logic;
import logic.VariableNameException;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class InputPanel extends JScrollPane {
    private static JPanel panel = new JPanel();
    private final Logic logic;
    private final JPanel variableHeader;
    private final JTextField variableInput;
    private final List<VariableCard> variables;
    private final JPanel langSelectorPanel;
    private final JTextArea theoremInput;

    public InputPanel(Logic logic) {
        super(panel);
        this.logic = logic;

        setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        setPreferredSize(new Dimension(300, 570));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        variableHeader = new JPanel();
        variableHeader.setLayout(new FlowLayout(FlowLayout.LEFT,3,3));
        JLabel variableLabel = new JLabel("Add Variable: ");
        JButton addVariableBtn = new JButton("Add");
        addVariableBtn.setText("Add");
        variableHeader.add(variableLabel);
        variableHeader.add(addVariableBtn);

        variables = new ArrayList<>();
        variableInput = new JTextField();
        variableInput.setMaximumSize(new Dimension(290, 30));
        addVariableBtn.addActionListener(e -> {
            try {
                String input = variableInput.getText();
                logic.addVariable(input);
                variables.add(new VariableCard(input, variables, variableInput, this::constructPanel));
                variableInput.setText("");
                constructPanel();
            } catch (VariableNameException vne) {
                // TODO
            }
        });

        for (String variableName : logic.getVariables()) {
            variables.add(new VariableCard(variableName, variables, variableInput, this::constructPanel));
        }

        langSelectorPanel = new JPanel();
        String[] languages = List.of("Coq", "LaTeX").toArray(new String[0]);
        JComboBox<String> langSelector = new JComboBox<>(languages);
        langSelectorPanel.add(new JLabel("Language: "));
        langSelectorPanel.add(langSelector);
        langSelectorPanel.setLayout(new FlowLayout(FlowLayout.LEFT,3,3));

        theoremInput = new JTextArea();
        theoremInput.setLineWrap(true);
        theoremInput.setMargin(new Insets(5, 5, 5, 5));

        constructPanel();
    }

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
    }
}
