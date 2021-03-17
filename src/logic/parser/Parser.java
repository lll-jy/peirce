package logic.parser;

import logic.Language;

import java.util.List;

/**
 * Parser that parses theorems.
 */
public abstract class Parser {
    /**
     * Tokenizes an input theorem.
     * @param theorem the input string of the theorem.
     * @return the tokens of the theorem.
     */
    protected abstract String[] tokenize(String theorem);

    /**
     * Creates a parser of the corresponding language.
     * @param language the language of the theorem written.
     * @return a parser of the langauge.
     */
    public static Parser createParser(Language language) {
        return switch (language) {
            case Coq -> new CoqParser();
            case LaTeX -> new LatexParser();
        };
    }

    // TODO: temporary
    public Object parse(String theorem) {
        return List.of(tokenize(theorem)).toString();
    }
}
