package ui;

import logic.Logic;

import javax.swing.*;
import java.util.List;
import java.util.Objects;

/**
 * A card that displays a variable in the input panel.
 */
public class VariableCard extends Card {

    /**
     * Initializes a variable card.
     * @param variableName the name of the variable.
     * @param variables the list of all variable cards (to help with edit and delete implementation).
     * @param inputField the variable input field (to help with edit implementation).
     * @param refresh the runnable procedure refreshes the input panel after update.
     */
    public VariableCard(Logic logic, String variableName, List<VariableCard> variables, JTextField inputField,
                        Runnable refresh) {
        super(logic, variableName, "for all %s, ", variables, inputField, logic::deleteVariable, refresh);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VariableCard)) return false;
        VariableCard that = (VariableCard) o;
        return Objects.equals(content, that.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(content);
    }
}
