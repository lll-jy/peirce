package logic.parser;

import logic.Language;
import logic.exceptions.TheoremParseException;

/**
 * Parser that parses theorems.
 */
public abstract class Parser {
    /**
     * Tokenizes an input theorem.
     * @param theorem the input string of the theorem.
     * @return the tokens of the theorem.
     * @throws TheoremParseException if the theorem can be tokenized properly in the given language.
     */
    public abstract String[] tokenize(String theorem) throws TheoremParseException;

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
}
