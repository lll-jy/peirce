package logic.exceptions;

public class UndoException extends Exception {
    public UndoException() {
        super("No previous steps. Unable to undo anymore.");
    }
}
