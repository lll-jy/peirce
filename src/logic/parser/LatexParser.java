package logic.parser;

import java.util.ArrayList;
import java.util.List;

/**
 * Parser of LaTeX.
 */
public class LatexParser extends Parser {
    public static void main(String[] args) {
        System.out.println(List.of(new LatexParser().tokenize("p\\land q")));
        System.out.println(List.of(new LatexParser().tokenize("p\\landq")));
        System.out.println(List.of(new LatexParser().tokenize("p\\land(q\\equiv p)")));
    }

    public static String[] latexNotations = new String[]{
            "\\land", "\\wedge", // Conjunction
            "\\lor", "\\vee", // Disjunction
            "\\lnot", "\\neg", "\\sim", // Negation
            "\\Rightarrow", "\\to", "\\rightarrow", "\\supset", "\\implies", //Implication
            "\\Leftrightarrow", "\\equiv", "\\leftrightarrow", "\\iff" // Bi-conditional
    };

    @Override
    protected String[] tokenize(String theorem) {
        List<String> tokens = new ArrayList<>();
        int length = theorem.length();
        int pointer = 0;
        while (pointer < length) {
            String substring = theorem.substring(pointer);
            if (substring.startsWith(" ")) {
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
        return tokens.toArray(new String[0]);
    }

    private int getNextIndex(String substring) {
        int index = substring.length();
        String[] delimits = new String[]{"\\", " ", "(", ")"};
        for (String delimit : delimits) {
            int thisIndex = substring.indexOf(delimit);
            if (thisIndex > 0 && thisIndex < index) {
                index = thisIndex;
            }
        }
        return index;
    }
}
