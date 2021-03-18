package ui;

import logic.Logic;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Objects;

/**
 * A card that displays a variable in the input panel.
 */
public class PremiseCard extends JPanel {
    private final Logic logic;
    private final String variableName;
    private boolean isEditable;
    private final JButton editVariableBtn;
    private final JButton deleteVariableBtn;

    /**
     * Initializes a variable card.
     * @param variableName the name of the variable.
     * @param variables the list of all variable cards (to help with edit and delete implementation).
     * @param inputField the variable input field (to help with edit implementation).
     * @param refresh the runnable procedure refreshes the input panel after update.
     */
    public PremiseCard(Logic logic, String variableName, List<PremiseCard> variables, JTextField inputField,
                       Runnable refresh) {
        super();
        this.logic = logic;
        this.variableName = variableName;
        setLayout(new FlowLayout(FlowLayout.LEFT,3,3));
        add(new JLabel("for all " + variableName + ", "));
        editVariableBtn = new JButton("Edit");
        editVariableBtn.addActionListener(e -> {
            variables.removeIf(v -> v.variableName.equals(variableName));
            logic.deleteVariable(variableName);
            inputField.setText(variableName);
            refresh.run();
        });
        add(editVariableBtn);
        deleteVariableBtn = new JButton("Delete");
        deleteVariableBtn.addActionListener(e -> {
            variables.removeIf(v -> v.variableName.equals(variableName));
            logic.deleteVariable(variableName);
            refresh.run();
        });
        add(deleteVariableBtn);
        this.isEditable = true;
    }

    /**
     * Switches editable mode of the card.
     */
    public void switchEditable() {
        isEditable = !isEditable;
        editVariableBtn.setEnabled(isEditable);
        deleteVariableBtn.setEnabled(isEditable);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PremiseCard)) return false;
        PremiseCard that = (PremiseCard) o;
        return Objects.equals(variableName, that.variableName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(variableName);
    }
}
