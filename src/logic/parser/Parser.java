package logic.parser;

import logic.Language;
import logic.exceptions.TheoremParseException;
import model.Proposition;

import java.util.List;
import java.util.Stack;

/**
 * Parser that parses theorems.
 */
public abstract class Parser {
    public static String INVALID_TOKENS_ERR_MSG = "Please check your theorem, some tokens are invalid.";

    protected final List<String> variables;

    public Parser(List<String> variables) {
        this.variables = variables;
    }

    /**
     * Tokenizes an input theorem.
     * @param theorem the input string of the theorem.
     * @return the tokens of the theorem.
     * @throws TheoremParseException if some tokens are invalid.
     */
    protected abstract String[] tokenize(String theorem) throws TheoremParseException;

    /**
     * Creates a parser of the corresponding language.
     * @param language the language of the theorem written.
     * @param variables the list of valid variable tokens.
     * @return a parser of the language.
     */
    public static Parser createParser(Language language, List<String> variables) {
        return switch (language) {
            case Coq -> new CoqParser(variables);
            case LaTeX -> new LatexParser(variables);
        };
    }

    // TODO: temporary
    public Object parse(String theorem) {
        try {
            return List.of(tokenize(theorem)).toString();
        } catch (Exception e) {
            return null;
        }
    }

    abstract protected String parse(String[] tokens);
}
