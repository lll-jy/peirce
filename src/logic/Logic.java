package logic;

import model.Model;

import java.util.List;

public class Logic {
    public static String[] languages = List.of("Coq", "LaTeX").toArray(new String[0]);

    private final Model model;
    private Language language;
    private Mode mode;

    public Logic(Model model) {
        this.model = model;
        this.language = Language.Coq;
        this.mode = Mode.DECLARATION;
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

    public boolean canModifyDeclaration() {
        return mode == Mode.DECLARATION;
    }

    public void switchMode() {
        switch (mode) {
            case DECLARATION -> mode = Mode.PROOF;
            case PROOF -> mode = Mode.DECLARATION;
        }
    }
}
