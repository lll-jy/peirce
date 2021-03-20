package storage;

import logic.Language;
import logic.Logic;
import logic.exceptions.FilePathException;
import logic.exceptions.FileReadException;
import logic.exceptions.TheoremParseException;
import logic.parser.Parser;
import model.Inference;
import model.InferenceRule;
import model.Proposition;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.Stack;

public class Storage {
    /**
     * Creates the corresponding directory.
     * @param s the directory in the form of .../.../ (should ends with '/').
     */
    public static void createDirectory(String s) throws FilePathException {
        try {
            Path path = Paths.get(s);
            Files.createDirectories(path);
        } catch (IOException e) {
            throw new FilePathException(e.getMessage());
        }
    }

    /**
     * Saves the proof stored in logic to the file. The file format is:
     * "
     * [list of variables with white space as delimiters]
     * [language]
     * [number of premises]
     * [premises in the language chosen]*
     * [theorem to prove in the language chosen]
     * [number of steps]
     * [inference steps in the format of RULE&from&to where both from and to are canonical]*
     * "
     * @param filePath the file path to save.
     * @param logic the logic instance that holds the proof.
     */
    public static void saveProof(String filePath, Logic logic) {
        try {
            FileWriter fw = new FileWriter(filePath);
            for (String s : logic.getVariables()) {
                fw.write(s);
                fw.write(" ");
            }
            fw.write("\n");
            fw.write(logic.getLanguage().toString());
            fw.write("\n");
            List<String> premises = logic.getPremises();
            fw.write(String.format("%d", premises.size()));
            fw.write("\n");
            for (String p : premises) {
                fw.write(p);
                fw.write("\n");
            }
            fw.write(logic.getTheoremString());
            fw.write("\n");
            Stack<Inference> history = logic.getHistory();
            fw.write(String.format("%d", history.size()));
            fw.write("\n");
            for (Inference i : logic.getHistory()) {
                fw.write(i.fileSave());
                fw.write("\n");
            }
            fw.close();
        } catch (IOException e) {
            assert false;
        }
    }

    public static void loadFile(String filePath, Logic logic) throws FilePathException, FileReadException {
        File file = new File(filePath);
        Scanner sc;
        try {
            sc = new Scanner(file);
        } catch (FileNotFoundException e) {
            throw new FilePathException(e.getMessage());
        }
        if (!sc.hasNextLine()) {
            throw new FileReadException();
        }
        List<String> variables = Arrays.asList(sc.nextLine().split(" ").clone());
        logic.setVariables(variables);
        if (!sc.hasNextLine()) {
            throw new FileReadException();
        }
        logic.setLanguage(sc.nextLine());
        if (!sc.hasNextInt()) {
            throw new FileReadException();
        }
        int n = sc.nextInt();
        sc.nextLine();
        for (int i = 0; i < n; i++) {
            if (!sc.hasNextLine()) {
                throw new FileReadException();
            }
            String premise = sc.nextLine();
            try {
                logic.addPremise(premise);
            } catch (TheoremParseException e) {
                throw new FileReadException();
            }
        }
        if (!sc.hasNextLine()) {
            throw new FileReadException();
        }
        try {
            logic.setTheorem(sc.nextLine());
        } catch (TheoremParseException e) {
            throw new FileReadException();
        }
        if (!sc.hasNextInt()) {
            throw new FileReadException();
        }
        int m = sc.nextInt();
        sc.nextLine();
        logic.clearHistory();
        for (int i = 0; i < m; i++) {
            if (!sc.hasNextLine()) {
                throw new FileReadException();
            }
            String inference = sc.nextLine();
            String[] info = inference.split("&");
            if (info.length != 3) {
                throw new FileReadException();
            }
            InferenceRule rule = switch (info[0]) {
                case "ERASURE" -> InferenceRule.ERASURE;
                case "INSERTION" -> InferenceRule.INSERTION;
                case "DEITERATION" -> InferenceRule.DEITERATION;
                case "ITERATION" -> InferenceRule.ITERATION;
                case "DOUBLE_CUT_INTRO" -> InferenceRule.DOUBLE_CUT_INTRO;
                case "DOUBLE_CUT_ELIM" -> InferenceRule.DOUBLE_CUT_ELIM;
                case "REMOVE_BOTH" -> InferenceRule.REMOVE_BOTH;
                case "INSERT_BOTH" -> InferenceRule.INSERT_BOTH;
                default -> throw new FileReadException();
            };
            logic.insertHistory(new Inference(info[1], info[2], rule));
        }
    }
}
