package logic.parser;

import logic.exceptions.TheoremParseException;

/**
 * Parser of LaTeX.
 */
public class LatexParser extends Parser {
    @Override
    public String[] tokenize(String theorem) throws TheoremParseException {
        return new String[0];
    }
}
