package logic;

import model.Model;

import java.util.List;

public class Logic {
    private final Model model;

    public Logic(Model model) {
        this.model = model;
    }

    public Model getModel() {
        return model;
    }

    public void addVariable(String varName) throws VariableNameException {
        model.insertVariable(varName);
    }

    public List<String> getVariables() {
        return model.getVariables();
    }
}
