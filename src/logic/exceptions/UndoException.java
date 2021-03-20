package logic.exceptions;

/**
 * Exception for invalid undo action.
 */
public class UndoException extends Exception {
    public UndoException() {
        super("No previous steps. Unable to undo anymore.");
    }
}
