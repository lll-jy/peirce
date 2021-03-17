package logic.parser;

import logic.Language;
import logic.exceptions.TheoremParseException;
import model.Proposition;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * Parser that parses theorems.
 */
public abstract class Parser {
    public static String INVALID_TOKENS_ERR_MSG = "Please check your theorem, some tokens are invalid.";
    public static String INVALID_SYNTAX_ERR_MSG = "Please check your theorem, the syntax seems incorrect.";

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
     * Checks whether the given string starts with some blank character.
     * @param s the string to check.
     * @return true if it starts with white space, new line, or tab.
     */
    protected boolean startsWithBlank(String s) {
        return s.startsWith(" ") || s.startsWith("\n") || s.startsWith("\t");
    }

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
    public Object parse(String theorem) throws TheoremParseException {
        String[] tokens = tokenize(theorem);
        writeTokensToFile(tokens);
        try {
            Runtime.getRuntime().exec("swipl -f prolog/syntax.pl");
        } catch (IOException e) {
            throw new TheoremParseException(INVALID_SYNTAX_ERR_MSG);
        }

        try {
            return List.of(tokenize(theorem)).toString();
        } catch (Exception e) {
            return null;
        }
    }

    abstract protected Language languageUsed();

    protected void writeTokensToFile(String[] tokens) {
        try {
            FileWriter fw = new FileWriter("prolog/tokens.txt");
            fw.write(languageUsed().toString());
            fw.write("\n");
            for (String t : tokens) {
                fw.write(t);
                fw.write("\n");
            }
            //fw.write("!!EOF\n");
            fw.close();
        } catch (IOException e) {
            assert false;
        }
    };

    abstract protected String parse(String[] tokens);
}
