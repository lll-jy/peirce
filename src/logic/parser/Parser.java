package logic.parser;

import logic.Language;
import logic.exceptions.TheoremParseException;
import model.CutLiteral;
import model.GroundLiteral;
import model.Proposition;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import java.util.Stack;

/**
 * Parser that parses theorems.
 */
public abstract class Parser {
    public static String INVALID_TOKENS_ERR_MSG = "Please check your theorem, some tokens are invalid.";
    public static String INVALID_SYNTAX_ERR_MSG = "Please check your theorem, the syntax seems incorrect.";
    public static String EMPTY_ERR_MSG = "Please enter your theorem to prove.";

    protected final List<String> variables;

    /**
     * Creates a parser instance.
     * @param variables the list of variables recognizable.
     */
    public Parser(List<String> variables) {
        this.variables = variables;
    }

    /**
     * Tokenizes an input theorem.
     * @param theorem the input string of the theorem.
     * @return the tokens of the theorem.
     * @throws TheoremParseException if some tokens are invalid.
     */
    protected abstract String[] tokenize(String theorem) throws TheoremParseException;

    /**
     * Checks whether the given string starts with some blank character.
     * @param s the string to check.
     * @return true if it starts with white space, new line, or tab.
     */
    protected boolean startsWithBlank(String s) {
        return s.startsWith(" ") || s.startsWith("\n") || s.startsWith("\t");
    }

    /**
     * Creates a parser of the corresponding language.
     * @param language the language of the theorem written.
     * @param variables the list of valid variable tokens.
     * @return a parser of the language.
     */
    public static Parser createParser(Language language, List<String> variables) {
        return switch (language) {
            case Coq -> new CoqParser(variables);
            case LaTeX -> new LatexParser(variables);
        };
    }

    /**
     * Parse a given theorem to Peirce proposition.
     * @param theorem the string of the theorem given.
     * @return the proposition constructed based on the input string.
     * @throws TheoremParseException if the input theorem to parse is invalid.
     */
    public Proposition parse(String theorem) throws TheoremParseException {
        if (theorem.equals("")) {
            throw new TheoremParseException(EMPTY_ERR_MSG);
        }
        String[] tokens = tokenize(theorem);
        writeTokensToFile(tokens);
        try {
            Runtime.getRuntime().exec("swipl -f prolog/syntax.pl").waitFor();
        } catch (Exception e) {
            throw new TheoremParseException(INVALID_SYNTAX_ERR_MSG);
        }
        File frameFile = new File("prolog/peirce.txt");
        Scanner sc = null;
        try {
            sc = new Scanner(frameFile);
        } catch (FileNotFoundException e) {
            assert false;
        }
        String[] peirceFrames = sc.nextLine().split(" ");
        int level = 0;
        Proposition proposition = new Proposition();
        Stack<CutLiteral> stack = new Stack<>();
        for(int i = 0; i < peirceFrames.length; i++) {
            if (peirceFrames[i].equals("[")) {
                level++;
                CutLiteral thisLiteral = new CutLiteral(proposition, null);
                stack.push(thisLiteral);
                proposition.addLiteral(thisLiteral);
                proposition = new Proposition(level, thisLiteral);
            } else if (peirceFrames[i].equals("]")) {
                level--;
                CutLiteral thisLiteral = stack.pop();
                thisLiteral.setContent(proposition);
                proposition = thisLiteral.getParent();
            } else {
                proposition.addLiteral(new GroundLiteral(proposition, peirceFrames[i]));
            }
        }
        return proposition;
    }

    /**
     * Gets the language used by the parse.
     * @return the language.
     */
    abstract protected Language languageUsed();

    /**
     * Writes tokens to the file at "prolog/tokens.txt".
     * @param tokens the array of tokens strings.
     */
    protected void writeTokensToFile(String[] tokens) {
        try {
            FileWriter fw = new FileWriter("prolog/tokens.txt");
            fw.write(languageUsed().toString());
            fw.write("\n");
            for (String t : tokens) {
                fw.write(t);
                fw.write("\n");
            }
            fw.close();
        } catch (IOException e) {
            assert false;
        }
    };
}
