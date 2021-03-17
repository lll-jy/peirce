package logic.parser;

import java.util.ArrayList;
import java.util.List;

/**
 * Parser of Coq.
 */
public class CoqParser extends Parser {
    public static String[] coqNotations = new String[]{"/\\", "\\/", "~", "<->", "->", "(", ")"};

    @Override
    protected String[] tokenize(String theorem) {
        List<String> tokens = new ArrayList<>();
        int length = theorem.length();
        int pointer = 0;
        while (pointer < length) {
            String substring = theorem.substring(pointer);
            if (substring.startsWith(" ")) {
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
        return tokens.toArray(new String[0]);
    }
}
