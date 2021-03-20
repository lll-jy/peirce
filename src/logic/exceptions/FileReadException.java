package logic.exceptions;

/**
 * Exception for invalid file content.
 */
public class FileReadException extends Exception {
    public FileReadException() {
        super("The file is not of the correct format.");
    }
}
