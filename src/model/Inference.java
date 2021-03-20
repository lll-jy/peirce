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

    /**
     * Creates an inference instance.
     * @param from the initial proposition string in canonical representation of Peirce Alpha diagram.
     * @param to the resulting proposition string in canonical representation of Peirce Alpha diagram.
     * @param rule the inference rule applied.
     */
    public Inference(String from, String to, InferenceRule rule) {
        this.from = from;
        this.to = to;
        this.rule = rule;
    }

    /**
     * Gets the proposition as premise of the inference step.
     * @param variables the list of variables to help parsing.
     * @return the proposition Java instance.
     */
    public Proposition getOriginalProposition(List<String> variables) {
        return parseProposition(variables, from);
    }

    /**
     * Gets the proposition as conclusion of the inference step.
     * @param variables the list of variables to help parsing.
     * @return the proposition Java instance.
     */
    public Proposition getResultingProposition(List<String> variables) {
        return parseProposition(variables, to);
    }

    /**
     * Gets the proposition stored in the inference.
     * @param variables the list of variables to help parsing.
     * @param str the string to parse.
     * @return the proposition Java instance.
     */
    private Proposition parseProposition(List<String> variables, String str) {
        try {
            return Parser.createParser(Language.Coq, variables).parseFrame(str);
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
        sb.append(rule.printString());
        return sb.toString();
    }

    /**
     * Gets the string to save to file of this inference step.
     * @return the string to save to file with "|" as delimiter.
     */
    public String fileSave() {
        StringBuilder sb = new StringBuilder();
        sb.append(rule);
        sb.append("|");
        sb.append(from);
        sb.append("|");
        sb.append(to);
        return sb.toString();
    }
}
