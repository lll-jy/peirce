package ui;

import logic.Logic;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.function.Consumer;

/**
 * A card that displays a card of some content with edit and delte button in the input panel.
 */
public class Card extends JPanel {
    protected final Logic logic;
    protected final String content;
    protected boolean isEditable;
    protected final JButton editBtn;
    protected final JButton deleteBtn;

    /**
     * Initializes a variable card.
     * @param content the name of the variable.
     * @param cards the list of all variable cards (to help with edit and delete implementation).
     * @param inputField the variable input field (to help with edit implementation).
     * @param refresh the runnable procedure refreshes the input panel after update.
     */
    public Card(Logic logic, String content, String formatStr, List<? extends Card> cards, JTextField inputField,
                Consumer<String> delete, Runnable refresh) {
        super();
        this.logic = logic;
        this.content = content;
        setLayout(new FlowLayout(FlowLayout.LEFT,3,3));
        add(new JLabel(String.format(formatStr, content)));
        editBtn = new JButton("Edit");
        editBtn.addActionListener(e -> {
            cards.removeIf(v -> v.content.equals(content));
            delete.accept(content);
            inputField.setText(content);
            refresh.run();
        });
        add(editBtn);
        deleteBtn = new JButton("Delete");
        deleteBtn.addActionListener(e -> {
            cards.removeIf(v -> v.content.equals(content));
            delete.accept(content);
            refresh.run();
        });
        add(deleteBtn);
        this.isEditable = true;
    }

    /**
     * Switches editable mode of the card.
     */
    public void switchEditable() {
        isEditable = !isEditable;
        editBtn.setEnabled(isEditable);
        deleteBtn.setEnabled(isEditable);
    }
}
