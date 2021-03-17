package ui;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Objects;

public class VariableCard extends JPanel {
    private final String variableName;
    private boolean isEditable;
    private final JButton editVariableBtn;
    private final JButton deleteVariableBtn;

    public VariableCard(String variableName, List<VariableCard> variables, JTextField inputField,
                        Runnable refresh, boolean isEditable) {
        super();
        this.variableName = variableName;
        setLayout(new FlowLayout(FlowLayout.LEFT,3,3));
        add(new JLabel("for all " + variableName + ", "));
        editVariableBtn = new JButton("Edit");
        editVariableBtn.addActionListener(e -> {
            variables.removeIf(v -> v.variableName.equals(variableName));
            inputField.setText(variableName);
            refresh.run();
        });
        add(editVariableBtn);
        deleteVariableBtn = new JButton("Delete");
        deleteVariableBtn.addActionListener(e -> {
            variables.removeIf(v -> v.variableName.equals(variableName));
            refresh.run();
        });
        add(deleteVariableBtn);
        this.isEditable = isEditable;
    }

    public void switchEditable() {
        isEditable = !isEditable;
        editVariableBtn.setEnabled(isEditable);
        deleteVariableBtn.setEnabled(isEditable);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VariableCard)) return false;
        VariableCard that = (VariableCard) o;
        return Objects.equals(variableName, that.variableName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(variableName);
    }
}
