package ui;

import model.Model;
import model.VariableNameException;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class InputPanel extends JScrollPane {
    private static JPanel panel = new JPanel();
    private final Model model;
    private final JPanel variableHeader;
    private final JTextField variableInput;
    private final List<VariableCard> variables;

    public InputPanel(Model model) {
        super(panel);
        this.model = model;

        setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        setPreferredSize(new Dimension(350, 570));
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
        addVariableBtn.addActionListener(e -> {
            try {
                String input = variableInput.getText();
                model.insertVariable(input);
                variables.add(new VariableCard(input, variables, variableInput, this::constructPanel));
                variableInput.setText("");
                constructPanel();
            } catch (VariableNameException vne) {
                // TODO
            }
        });

        for (String variableName : model.getVariables()) {
            variables.add(new VariableCard(variableName, variables, variableInput, this::constructPanel));
        }

        constructPanel();
        panel.add(new JLabel("\n"));
        for (int i = 0; i < 100; i++) {
            panel.add(new JLabel("test2 " + i + "\n"));
        }

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
    }
}
