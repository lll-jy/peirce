package logic.exceptions;

/**
 * Exception for invalid redo action.
 */
public class RedoException extends Exception {
    public RedoException() {
        super("No step records. Unable to redo anymore.");
    }
}
