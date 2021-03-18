package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Components that keeps track of any data or data structure used for application, including variables, theorem
 * to prove, and proof history.
 */
public class Model {
    private final List<String> variables;
    private final List<String> premisesStrings;
    private final List<Proposition> premises;
    // private final Proposition theorem; TODO: set theorem
    private Proposition proposition;

    /**
     * Initializes a model component with empty variables, theorem, and history.
     */
    public Model() {
        variables = new ArrayList<>();
        premisesStrings = new ArrayList<>();
        premises = new ArrayList<>();
        proposition = new Proposition();
    }

    /**
     * Gets the list of variables recognizable in this model.
     * @return the list of recognizable variables.
     */
    public List<String> getVariables() {
        return variables;
    }

    /**
     * Gets the list of premises recognizable in this model.
     * @return the list of premises.
     */
    public List<String> getPremisesStrings() {
        return premisesStrings;
    }

    /**
     * Gets the proposition of the model.
     * @return the proposition.
     */
    public Proposition getProposition() {
        return proposition;
    }

    /**
     * Sets the proposition the a given new proposition.
     * @param proposition the new proposition.
     */
    public void setProposition(Proposition proposition) {
        this.proposition = proposition;
    }

    /**
     * Adds a new variable to be recognized.
     * @param varName the name of the variable want to be added.
     */
    public void insertVariable(String varName) {
        variables.add(varName);
    }

    /**
     * Removes a particular variable.
     * @param varName the name of variable to be removed.
     */
    public void removeVariable(String varName) {
        variables.removeIf(x -> x.equals(varName));
    }

    /**
     * Adds a new premise.
     * @param str the premise string want to be added.
     * @param prop the constructed proposition corresponding to the premise.
     */
    public void insertPremise(String str, Proposition prop) {
        premisesStrings.add(str);
        premises.add(prop);
    }

    /**
     * Removes a particular premise.
     * @param premise the string of the premise to be removed.
     */
    public void removePremise(String premise) {
        int index = premisesStrings.indexOf(premise);
        premisesStrings.remove(index);
        premises.remove(index);
    }
}
