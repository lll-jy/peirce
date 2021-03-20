package storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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
}
