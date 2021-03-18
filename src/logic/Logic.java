package logic;

import logic.exceptions.InvalidInferenceException;
import logic.exceptions.InvalidSelectionException;
import logic.exceptions.TheoremParseException;
import logic.exceptions.VariableNameException;
import logic.parser.Parser;
import model.CutLiteral;
import model.Literal;
import model.Model;
import model.Proposition;

import java.util.ArrayList;
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

    /**
     * Initializes a Logic component based on the model, and initially the default language is Coq, and
     * application is in DECLARATION mode.
     * @param model the model that this application is based on.
     */
    public Logic(Model model) {
        this.model = model;
        this.language = Language.Coq;
        this.mode = Mode.DECLARATION;
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
        model.setProposition(proposition);
    }

    /**
     * Gets the current proposition.
     * @return the proposition.
     */
    public Proposition getProposition() {
        return model.getProposition();
    }

    /**
     * Gets the token index of the cursor position, and the cursor must be in the gaps of tokens (including
     * start and end).
     * @param cursorPos the cursor position with respect to the theorem string.
     * @return the token index of the token directly after the cursor, and -1 if the position is inside some token.
     */
    public int getTokenIndex(int cursorPos) {
        if (cursorPos == 0) {
            return 0;
        }
        String theorem = getProposition().toString();
        if (cursorPos == theorem.length()) {
            return theorem.split(" ").length;
        }
        if (theorem.charAt(cursorPos) == ' ' || theorem.charAt(cursorPos - 1) == ' ') {
            return theorem.substring(0, cursorPos).split(" ").length;
        }
        return -1;
    }

    /**
     * Gets the list of selected literals.
     * @param s the start token index selected.
     * @param e the end token index selected (exclusive).
     * @return the list of literals contained in the range of [s,e) but no enclosing frames are within the range.
     * @throws InvalidSelectionException if the selected part is invalid.
     */
    public List<Literal> getSelected(int s, int e) throws InvalidSelectionException {
        if (s < 0 || e < 0) {
            throw new InvalidSelectionException("Some literals are selected but " +
                    "not selected completely. Please select again");
        }
        return getProposition().getSelectedLiterals(s, e);
    }

    /**
     * Removes double cut in the selected part if it is in the form "[ [ proposition ] ]".
     * @param s the start token index selected.
     * @param e the end token index selected (exclusive).
     * @throws InvalidSelectionException if the selected part is not a valid proposition.
     * @throws InvalidInferenceException if the selected part is not in the form "[ [ proposition ] ]".
     */
    public void removeDoubleCut(int s, int e) throws InvalidSelectionException, InvalidInferenceException {
        List<Literal> literals = getSelected(s, e);
        if (literals.size() != 1) {
            throw new InvalidInferenceException("Please select a single literal to remove double cuts.");
        }
        Literal original = literals.get(0);
        List<Literal> result = original.getAfterRemoveDoubleCut();
        // TODO: level change
        getCursorProp(s).replaceLiterals(literals, result);
    }

    /**
     * Gets the proposition that is the inner most environment the cursor is in.
     * @param pos the cursor position in terms of token index.
     * @return the inner most enclosing proposition of the cursor.
     */
    public Proposition getCursorProp(int pos) {
        return getProposition().getCursorProp(pos);
    }

    /**
     * Adds a double cut surrounding the selected part.
     * @param s the start index of the selected part.
     * @param e the end index of the selected part (exclusive).
     * @throws InvalidSelectionException if the selected part is not a valid proposition.
     */
    public void addDoubleCut(int s, int e) throws InvalidSelectionException {
        List<Literal> literals = getSelected(s, e);
        Proposition parent = getCursorProp(s);
        CutLiteral res = new CutLiteral(parent, null);
        Proposition outer = new Proposition(parent.getLevel() + 1, res);
        res.setContent(outer);
        CutLiteral innerCut = new CutLiteral(outer, null);
        outer.addLiteral(innerCut);
        Proposition inner = new Proposition(parent.getLevel() + 2, innerCut);
        innerCut.setContent(inner);
        List<Literal> newLiterals = new ArrayList<>();
        newLiterals.add(res);
        if (literals.isEmpty()) {
            parent.insertLiterals(s, newLiterals);
        } else {
            // TODO: level change
            inner.addLiterals(literals);
            parent.replaceLiterals(literals, newLiterals);
        }
    }

    /**
     * Performs cut action to remove some part in the theorem by some inference rule.
     * @param s the start token index to remove.
     * @param e the end token index to remove.
     * @throws InvalidSelectionException if the part selected is not of valid syntax to remove.
     * @throws InvalidInferenceException if the part selected is not valid to remove by any inference rule.
     */
    public void cut(int s, int e) throws InvalidSelectionException, InvalidInferenceException {
        List<Literal> literals = getSelected(s, e);
        Proposition parent = getCursorProp(s);
        for (Literal l : literals) {
            if (!l.canDelete()) {
                throw new InvalidInferenceException("Some literals selected cannot be removed.");
            }
        }
        parent.replaceLiterals(literals, new ArrayList<>());
    }

    /**
     * Performs paste action to insert some diagrams to the theorem by some inference rule.
     * @param pos the cursor position in terms of token index where the new diagram is to be inserted.
     * @param str the string representing the proposition to insert.
     * @throws TheoremParseException if the string (in the clipboard) is not a valid proposition.
     * @throws InvalidSelectionException if the cursor position is invalid to insert anything.
     * @throws InvalidInferenceException if the proposition cannot be inserted by any inference rule.
     */
    public void paste(int pos, String str) throws TheoremParseException,
            InvalidSelectionException, InvalidInferenceException {
        Proposition prop = parse(str);
        Proposition parent = getCursorProp(pos);
        for (Literal l : prop.getLiterals()) {
            if (!parent.canInsert(l)) {
                throw new InvalidInferenceException("This diagram is not insertable to this place.");
            }
        }
        // TODO: level change
        parent.insertLiterals(pos, prop.getLiterals());
    }
}
