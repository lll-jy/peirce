package model;

import logic.Language;
import logic.exceptions.TheoremParseException;
import logic.parser.Parser;

import java.util.List;

/**
 * An inference step to keep track of history.
 */
public class Inference {
    private final String from;
    private final String to;
    private final InferenceRule rule;
    private final int s;
    private final int e;

    /**
     * Creates an inference instance.
     * @param from the initial proposition string in canonical representation of Peirce Alpha diagram.
     * @param to the resulting proposition string in canonical representation of Peirce Alpha diagram.
     * @param rule the inference rule applied.
     * @param s the start token index of this inference step.
     * @param e the end token index of this inference step.
     */
    public Inference(String from, String to, InferenceRule rule, int s, int e) {
        this.from = from;
        this.to = to;
        this.rule = rule;
        this.s = s;
        this.e = e;
    }

    public Proposition getsOriginalProposition(List<String> variables) {
        try {
            return Parser.createParser(Language.Coq, variables).parseFrame(from);
        } catch (TheoremParseException e) {
            assert false;
            return new Proposition();
        }
    }

    /**
     * Gets the display to users.
     * @return the string containing the resulting proposition and corresponding rule.
     */
    public String userDisplay() {
        StringBuilder sb = new StringBuilder();
        sb.append(to);
        sb.append(" by ");
        sb.append(rule.toString());
        return sb.toString();
    }
}
