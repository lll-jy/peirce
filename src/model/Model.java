package model;

import java.util.ArrayList;
import java.util.List;

public class Model {
    private final List<String> variables;

    public Model() {
        variables = new ArrayList<>();
    }

    public List<String> getVariables() {
        return variables;
    }

    public void insertVariable(String varName) throws VariableNameException {
        variables.add(varName);
    }
}
