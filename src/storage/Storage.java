package storage;

import logic.Logic;
import model.Inference;
import model.Proposition;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class Storage {
    /**
     * Creates the corresponding directory.
     * @param s the directory in the form of .../.../ (should ends with '/').
     */
    public static void createDirectory(String s) {
        try {
            Path path = Paths.get(s);
            Files.createDirectories(path);
        } catch (IOException e) {
            assert false;
        }
    }

    /**
     * Saves the proof stored in logic to the file. The file format is:
     * "
     * [list of variables with white space as delimiters]
     * [number of premises]
     * [premises in canonical string representation of Peirce Alpha diagram]*
     * [theorem to prove in canonical string representation]
     * [inference steps in the format of RULE|from|to where both from and to are canonical]*
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
            List<Proposition> premises = logic.getPremisePropositions();
            fw.write(String.format("%d", premises.size()));
            fw.write("\n");
            for (Proposition p : premises) {
                fw.write(p.toString());
                fw.write("\n");
            }
            fw.write(logic.getTheorem().toString());
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
}
