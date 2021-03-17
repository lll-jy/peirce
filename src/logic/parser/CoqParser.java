package logic.parser;

import logic.exceptions.TheoremParseException;

/**
 * Parser of Coq.
 */
public class CoqParser extends Parser {
    @Override
    public String[] tokenize(String theorem) throws TheoremParseException {
        return new String[0];
    }
}
