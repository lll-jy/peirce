package logic.parser;

import logic.Language;
import logic.exceptions.TheoremParseException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Parser of LaTeX.
 */
public class LatexParser extends Parser {
    public static String[] latexNotations = new String[]{
            "\\land", "\\wedge", // Conjunction
            "\\lor", "\\vee", // Disjunction
            "\\lnot", "\\neg", "\\sim", // Negation
            "\\Rightarrow", "\\to", "\\rightarrow", "\\supset", "\\implies", //Implication
            "\\Leftrightarrow", "\\equiv", "\\leftrightarrow", "\\iff", // Bi-conditional
            "(", ")"
    };

    public LatexParser(List<String> variables) {
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
            } else if (substring.startsWith("(")) {
                pointer++;
                tokens.add("(");
            } else if (substring.startsWith(")")) {
                pointer++;
                tokens.add(")");
            } else {
                int index = getNextIndex(substring);
                tokens.add(substring.substring(0, index));
                pointer += index;
            }
        }
        for (String t : tokens) {
            if (!variables.contains(t) && !Arrays.asList(latexNotations).contains(t)) {
                throw new TheoremParseException(INVALID_TOKENS_ERR_MSG);
            }
        }
        return tokens.toArray(new String[0]);
    }

    private int getNextIndex(String substring) {
        int index = substring.length();
        String[] delimits = new String[]{"\\", " ", "\n", "\t", "(", ")"};
        for (String delimit : delimits) {
            int thisIndex = substring.indexOf(delimit);
            if (thisIndex > 0 && thisIndex < index) {
                index = thisIndex;
            }
        }
        return index;
    }

    @Override
    protected Language languageUsed() {
        return Language.LaTeX;
    }
}
