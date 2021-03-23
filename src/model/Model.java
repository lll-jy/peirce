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
    private String theoremString;
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
     * Resets the variables in the model.
     * @param variables the list of variables to update.
     */
    public void setVariables(List<String> variables) {
        this.variables.clear();
        this.variables.addAll(variables);
    }

    /**
     * Gets the list of premises recognizable in this model.
     * @return the list of premises in the input string format.
     */
    public List<String> getPremisesStrings() {
        return premisesStrings;
    }

    /**
     * Gets the list of premises recognizable in this model.
     * @return the list of premises as Java Proposition instance.
     */
    public List<Proposition> getPremises() {
        return premises;
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
    public void setTheorem(String str, Proposition theorem) {
        this.theoremString = str;
        this.theorem = theorem;
        proposition = new Proposition();
        for (Proposition premise : premises) {
            for (Literal l : premise.getLiterals()) {
                l.setParent(proposition);
                proposition.addLiteral(l);
            }
        }
    }

    public void clear() {
        variables.clear();
        theorem = new Proposition();
        theoremString = "";
        premises.clear();
        premisesStrings.clear();
        proposition = new Proposition();
    }

    /**
     * Gets the string of the theorem to prove in the language.
     * @return the string.
     */
    public String getTheoremString() {
        return theoremString;
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

    public void resetProposition() {
        proposition = new Proposition();
        for (Proposition p : premises) {
            for (Literal l : p.getLiterals()) {
                proposition.addLiteral(l);
                l.setParent(proposition);
            }
        }
    }
}
