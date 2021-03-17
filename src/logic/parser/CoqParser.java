package logic.parser;

import logic.Language;
import logic.exceptions.TheoremParseException;
import model.Literal;
import model.Proposition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

/**
 * Parser of Coq.
 */
public class CoqParser extends Parser {
    public static String[] coqNotations = new String[]{"/\\", "\\/", "~", "<->", "->", "(", ")"};

    public CoqParser(List<String> variables) {
        super(variables);
    }

    @Override
    protected String[] tokenize(String theorem) throws TheoremParseException {
        List<String> tokens = new ArrayList<>();
        int length = theorem.length();
        int pointer = 0;
        while (pointer < length) {
            String substring = theorem.substring(pointer);
            if (startsWithBlank(substring)) {
                pointer++;
            } else {
                boolean startsWithNotation = false;
                for (String notation : coqNotations) {
                    if (substring.startsWith(notation)) {
                        startsWithNotation = true;
                        pointer += notation.length();
                        tokens.add(notation);
                        break;
                    }
                }
                if (!startsWithNotation) {
                    int varLength = substring.length();
                    if (substring.indexOf(' ') > 0) {
                        varLength = substring.indexOf(' ');
                    }
                    for (String notation : coqNotations) {
                        int index = substring.indexOf(notation);
                        if (index > 0 && index < varLength) {
                            varLength = index;
                        }
                    }
                    tokens.add(substring.substring(0, varLength));
                    pointer += varLength;
                }
            }
        }
        for (String t : tokens) {
            if (!variables.contains(t) && !Arrays.asList(coqNotations).contains(t)) {
                throw new TheoremParseException(INVALID_TOKENS_ERR_MSG);
            }
        }
        return tokens.toArray(new String[0]);
    }

    @Override
    protected Language languageUsed() {
        return Language.Coq;
    }

    @Override
    protected String parse(String[] tokens) {
        return null;
    }
}
