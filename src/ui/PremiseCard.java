package ui;

import logic.Logic;

import javax.swing.*;
import java.util.List;

/**
 * A card that displays a premise in the input panel.
 */
public class PremiseCard extends Card {

    /**
     * Initializes a premise card.
     * @param logic the logic component of the application.
     * @param premise the name of the premise.
     * @param premises the list of all premise cards (to help with edit and delete implementation).
     * @param inputField the premise input field (to help with edit implementation).
     * @param refresh the runnable procedure refreshes the input panel after update.
     */
    public PremiseCard(Logic logic, String premise, List<Card> premises, JTextField inputField,
                       Runnable refresh) {
        super(logic, premise, "given %s, ", premises, inputField, logic::deletePremise, refresh);
    }
}
