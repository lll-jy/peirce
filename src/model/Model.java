package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Components that keeps track of any data or data structure used for application, including variables, theorem
 * to prove, and proof history.
 */
public class Model {
    private final List<String> variables;

    /**
     * Initializes a model component with empty variables, theorem, and history.
     */
    public Model() {
        variables = new ArrayList<>();
    }

    /**
     * Gets the list of variables recognizable in this model.
     * @return the list of recognizable variables.
     */
    public List<String> getVariables() {
        return variables;
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
}
