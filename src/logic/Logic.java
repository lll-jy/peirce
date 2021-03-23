package logic;

import logic.exceptions.FilePathException;
import logic.exceptions.FileReadException;
import logic.exceptions.InvalidInferenceException;
import logic.exceptions.InvalidSelectionException;
import logic.exceptions.RedoException;
import logic.exceptions.TheoremParseException;
import logic.exceptions.UndoException;
import logic.exceptions.VariableNameException;
import logic.parser.Parser;
import model.CutLiteral;
import model.Inference;
import model.InferenceRule;
import model.Literal;
import model.Model;
import model.Proposition;
import storage.Storage;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.regex.Pattern;

/**
 * Component in the application that handles with logic.
 */
public class Logic {
    public static String[] languages = List.of("Coq", "LaTeX").toArray(new String[0]);
    public static Pattern variableRegex = Pattern.compile("[a-zA-Z_][a-zA-Z0-9_]*");
    public static Pattern fileNameRegex = Pattern.compile(
            "(([a-zA-Z0-9_][a-zA-Z0-9_ ]*)[/])*[a-zA-Z0-9_][a-zA-Z0-9_ ]*.txt");

    private final Model model;
    private Language language;
    private Mode mode;
    private final Stack<Inference> history;
    private final Stack<Inference> reverseHistory;
    private String filePath;

    /**
     * Initializes a Logic component based on the model, and initially the default language is Coq, and
     * application is in DECLARATION mode.
     * @param model the model that this application is based on.
     */
    public Logic(Model model) {
        this.model = model;
        this.language = Language.Coq;
        this.mode = Mode.DECLARATION;
        this.history = new Stack<>();
        this.reverseHistory = new Stack<>();
        this.filePath = "theorem.txt";
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
     * Adds a premise to the model.
     * @param str the string representing the premise in the language chosen.
     * @throws TheoremParseException if the input string is an invalid proposition of the language.
     */
    public void addPremise(String str) throws TheoremParseException {
        Proposition prop = parse(str);
        model.insertPremise(str, prop);
    }

    /**
     * Invalidates a variable that is recognized.
     * @param varName the name of the variable want to be invalidated.
     */
    public void deleteVariable(String varName) {
        model.removeVariable(varName);
    }

    /**
     * Resets the variables in the model.
     * @param variables the list of variables to update.
     */
    public void setVariables(List<String> variables) {
        model.setVariables(variables);
    }

    /**
     * Removes a premise.
     * @param str the string representing the premise.
     */
    public void deletePremise(String str) {
        model.removePremise(str);
    }

    /**
     * Gets the list of variables that is recognized by the application at any point of time.
     * @return the list of variable names recognizable.
     */
    public List<String> getVariables() {
        return model.getVariables();
    }

    /**
     * Gets the list of premises that is recognized by the application at any point of time.
     * @return the list of premises.
     */
    public List<String> getPremises() {
        return model.getPremisesStrings();
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
            case PROOF -> {
                mode = Mode.DECLARATION;
                List<Proposition> premises = model.getPremises();
                premises.clear();
                List<String> premiseStrings = model.getPremisesStrings();
                for (String premise : premiseStrings) {
                    try {
                        premises.add(parse(premise));
                    } catch (TheoremParseException e) {
                        assert false;
                    }
                }
                model.resetProposition();
            }
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
    public void setLanguage(String language) throws FileReadException {
        switch (language) {
            case "Coq" -> this.language = Language.Coq;
            case "LaTeX" -> this.language = Language.LaTeX;
            default -> throw new FileReadException();
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
     * Parses a string of canonical diagram expression to a proposition structure.
     * @param diagram the canonical diagram string.
     * @return the Proposition corresponding to the diagram.
     * @throws TheoremParseException if the input diagram is invalid.
     */
    public Proposition parseFrame(String diagram) throws TheoremParseException {
        return Parser.createParser(getLanguage(), getVariables()).parseFrame(diagram);
    }

    /**
     * Sets the theorem to prove in this logic instance.
     * @param theorem the new proposition.
     * @throws TheoremParseException if the string representing the theorem is not parsable in the language.
     */
    public void setTheorem(String theorem) throws TheoremParseException {
        model.setTheorem(theorem, parse(theorem));
    }

    /**
     * Gets the theorem in the language chosen.
     * @return the string of the theorem written in the language chosen.
     */
    public String getTheoremString() {
        return model.getTheoremString();
    }

    /**
     * Gets the theorem of the logic instance.
     * @return the theorem.
     */
    public Proposition getTheorem() {
        return model.getTheorem();
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
     * Inserts an application of inference rule to the history.
     * @param inference the inference step to insert.
     */
    public void insertHistory(Inference inference) {
        history.push(inference);
        reverseHistory.removeAllElements();
    }

    /**
     * Clears history.
     */
    public void clearHistory() {
        history.clear();
        reverseHistory.clear();
    }

    public void clear() {
        clearHistory();
        model.clear();
    }

    /**
     * Updates the proposition in the model by the last step in history.
     */
    public void updateProposition() {
        if (history.isEmpty()) {
            model.setProposition(new Proposition());
        } else {
            model.setProposition(history.peek().getResultingProposition(getVariables()));
        }
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
        Proposition parent = getCursorProp(s);
        String from = getProposition().toString();
        if (literals.size() != 1) {
            throw new InvalidInferenceException("Please select a single literal to remove double cuts.");
        }
        Literal original = literals.get(0);
        List<Literal> result = original.getAfterRemoveDoubleCut();
        for (Literal l : result) {
            l.increaseLevelBy(-2);
            l.setParent(parent);
        }
        parent.replaceLiterals(literals, result);
        String to = getProposition().toString();
        insertHistory(new Inference(from, to, InferenceRule.DOUBLE_CUT_ELIM));
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
        String from = getProposition().toString();
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
            for (Literal l : literals) {
                l.increaseLevelBy(2);
                l.setParent(inner);
            }
            inner.addLiterals(literals);
            parent.replaceLiterals(literals, newLiterals);
        }
        String to = getProposition().toString();
        insertHistory(new Inference(from, to, InferenceRule.DOUBLE_CUT_INTRO));
    }

    /**
     * Performs cut action to remove some part in the theorem by some inference rule.
     * @param s the start token index to remove.
     * @param e the end token index to remove.
     * @throws InvalidSelectionException if the part selected is not of valid syntax to remove.
     * @throws InvalidInferenceException if the part selected is not valid to remove by any inference rule.
     */
    public void cut(int s, int e) throws InvalidSelectionException, InvalidInferenceException {
        String from = getProposition().toString();
        List<Literal> literals = getSelected(s, e);
        Proposition parent = getCursorProp(s);
        boolean erasureApplied = false;
        boolean deiterationApplied = false;
        for (Literal l : literals) {
            InferenceRule rule = l.getDeleteRule();
            switch (rule) {
                case ERASURE -> erasureApplied = true;
                case DEITERATION -> deiterationApplied = true;
            }
        }
        parent.replaceLiterals(literals, new ArrayList<>());
        String to = getProposition().toString();
        if (erasureApplied) {
            if (deiterationApplied) {
                insertHistory(new Inference(from, to, InferenceRule.REMOVE_BOTH));
            } else {
                insertHistory(new Inference(from, to, InferenceRule.ERASURE));
            }
        } else {
            if (deiterationApplied) {
                insertHistory(new Inference(from, to, InferenceRule.DEITERATION));
            }
        }
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
        String from = getProposition().toString();
        Proposition prop = parseFrame(str);
        Proposition parent = getCursorProp(pos);
        boolean insertionApplied = false;
        boolean iterationApplied = false;
        for (Literal l : prop.getLiterals()) {
            InferenceRule rule = parent.getInsertRule(l);
            switch (rule) {
                case INSERTION -> insertionApplied = true;
                case ITERATION -> iterationApplied = true;
            }
            l.setParent(parent);
        }
        prop.increaseLevelBy(parent.getLevel());
        parent.insertLiterals(pos, prop.getLiterals());
        String to = getProposition().toString();
        if (insertionApplied) {
            if (iterationApplied) {
                insertHistory(new Inference(from, to, InferenceRule.INSERT_BOTH));
            } else {
                insertHistory(new Inference(from, to, InferenceRule.INSERTION));
            }
        } else {
            if (iterationApplied) {
                insertHistory(new Inference(from, to, InferenceRule.ITERATION));
            }
        }
    }

    /**
     * Checks whether the proof is successful.
     * @return true if the proposition currently held by the model is the same as goal.
     */
    public boolean succeeds() {
        return getTheorem().hasSameLiterals(getProposition());
    }

    /**
     * Get the string for user display that is last logged in history.
     * @return the string as described.
     */
    public String getLastLog() {
        if (!history.isEmpty()) {
            return history.peek().userDisplay();
        } else {
            return "";
        }
    }

    /**
     * Undoes the last inference step.
     * @throws UndoException if there are no steps to undo.
     */
    public void undo() throws UndoException {
        if (history.isEmpty()) {
            throw new UndoException();
        }
        reverseHistory.push(history.pop());
        Proposition prop = reverseHistory.peek().getOriginalProposition(getVariables());
        model.setProposition(prop);
    }

    /**
     * Redoes the last step undone.
     * @return the inference step inserted back.
     * @throws RedoException if there are no steps to redo.
     */
    public Inference redo() throws RedoException {
        if (reverseHistory.isEmpty()) {
            throw new RedoException();
        }
        history.push(reverseHistory.pop());
        Proposition prop = history.peek().getResultingProposition(getVariables());
        model.setProposition(prop);
        return history.peek();
    }

    /**
     * Gets the proof step history that is currently in the application.
     * @return the history stack.
     */
    public Stack<Inference> getHistory() {
        return history;
    }

    public void dummy() {
        System.out.println("called here");
    }

    /**
     * Saves the current proof to file.
     */
    public void save() {
        Storage.saveProof(filePath, this);
    }

    /**
     * Prepares the file path to save file to.
     * @param path the path of the target file, including the file itself (i.e. .../.../...txt).
     * @throws FilePathException if the file path given is invalid.
     */
    public void preparePath(String path) throws FilePathException {
        if (!fileNameRegex.matcher(path).matches()) {
            throw new FilePathException("The file path seems not correct. Please make sure you use " +
                    "/ as delimiters and save the file as a .txt file.");
        }
        String[] paths = path.split("/");
        StringBuilder folder = new StringBuilder();
        for (int i = 0; i < paths.length - 1; i++) {
            folder.append(paths[i]);
            folder.append("/");
        }
        Storage.createDirectory(folder.toString());
        filePath = path;
    }

    public void open(String path) throws FilePathException, FileReadException {
        filePath = path;
        Storage.loadFile(path, this);
    }
}
