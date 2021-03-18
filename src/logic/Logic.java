package logic;

import logic.exceptions.TheoremParseException;
import logic.exceptions.VariableNameException;
import logic.parser.Parser;
import model.Model;
import model.Proposition;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Component in the application that handles with logic.
 */
public class Logic {
    public static String[] languages = List.of("Coq", "LaTeX").toArray(new String[0]);
    public static Pattern variableRegex = Pattern.compile("[a-zA-Z_][a-zA-Z0-9_]*");

    private final Model model;
    private Language language;
    private Mode mode;
    private Proposition proposition;

    /**
     * Initializes a Logic component based on the model, and initially the default language is Coq, and
     * application is in DECLARATION mode.
     * @param model the model that this application is based on.
     */
    public Logic(Model model) {
        this.model = model;
        this.language = Language.Coq;
        this.mode = Mode.DECLARATION;
        this.proposition = new Proposition();
    }

    /**
     * Gets the model of the application.
     * @return the model of the application.
     */
    public Model getModel() {
        return model;
    }

    /**
     * Adds a variable that should be able to recognized.
     * @param varName the name of the variable.
     * @throws VariableNameException if the variable name is empty, existed, or of invalid regex.
     */
    public void addVariable(String varName) throws VariableNameException {
        if (varName.equals("")) {
            throw new VariableNameException("Variable name cannot be empty");
        }
        if (getVariables().contains(varName)) {
            throw new VariableNameException("This variable name already exists");
        }
        if (!variableRegex.matcher(varName).matches()) {
            throw new VariableNameException("Please give a variable name consisting of alphanumerical characters " +
                    "or underscore with the first character being non-numerical");
        }
        model.insertVariable(varName);
    }

    /**
     * Invalidates a variable that is recognized.
     * @param varName the name of the variable want to be invalidated.
     */
    public void deleteVariable(String varName) {
        model.removeVariable(varName);
    }

    /**
     * Gets the list of variables that is recognized by the application at any point of time.
     * @return the list of variable names recognizable.
     */
    public List<String> getVariables() {
        return model.getVariables();
    }

    /**
     * Checks whether the input panel for declaration is modifiable.
     * @return true if the input panel is modifiable.
     */
    public boolean canModifyDeclaration() {
        return mode == Mode.DECLARATION;
    }

    /**
     * Switches application mode between DECLARATION and PROOF.
     */
    public void switchMode() {
        switch (mode) {
            case DECLARATION -> mode = Mode.PROOF;
            case PROOF -> mode = Mode.DECLARATION;
        }
    }

    /**
     * Gets the language for declaration that is currently used.
     * @return the language used in application at this point in time.
     */
    public Language getLanguage() {
        return language;
    }

    /**
     * Sets the language for declaration.
     * @param language the string representing the language.
     */
    public void setLanguage(String language) {
        switch (language) {
            case "Coq" -> this.language = Language.Coq;
            case "LaTeX" -> this.language = Language.LaTeX;
        }
    }

    /**
     * Parses a string of theorem to a proposition structure.
     * @param theorem the string of the theorem.
     * @return the Proposition corresponding to the theorem constructed.
     * @throws TheoremParseException if the input theorem is invalid.
     */
    public Proposition parse(String theorem) throws TheoremParseException {
        return Parser.createParser(getLanguage(), getVariables()).parse(theorem);
    }

    /**
     * Sets the proposition to be dealt with in this logic instance.
     * @param proposition the new proposition.
     */
    public void setProposition(Proposition proposition) {
        this.proposition = proposition;
    }

    /**
     * Gets the current proposition.
     * @return the proposition.
     */
    public Proposition getProposition() {
        return proposition;
    }
}
