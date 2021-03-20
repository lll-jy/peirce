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
    private Proposition theorem;
    private Proposition proposition;

    /**
     * Initializes a model component with empty variables, theorem, and history.
     */
    public Model() {
        variables = new ArrayList<>();
        premisesStrings = new ArrayList<>();
        premises = new ArrayList<>();
        theorem = new Proposition();
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
     * Gets the proposition currently in the the model.
     * @return the proposition.
     */
    public Proposition getProposition() {
        return proposition;
    }

    /**
     * Sets the proposition of the model to a new one.
     * @param prop the new proposition.
     */
    public void setProposition(Proposition prop) {
        proposition = prop;
    }

    /**
     * Gets the theorem of the model.
     * @return the theorem.
     */
    public Proposition getTheorem() {
        return theorem;
    }

    /**
     * Sets the theorem to the given new proposition.
     * @param theorem the new proposition.
     */
    public void setTheorem(Proposition theorem) {
        this.theorem = theorem;
        proposition = new Proposition();
        for (Proposition premise : premises) {
            for (Literal l : premise.getLiterals()) {
                l.setParent(proposition);
                proposition.addLiteral(l);
            }
        }
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
