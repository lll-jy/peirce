package logic.exceptions;

public class RedoException extends Exception {
    public RedoException() {
        super("No step records. Unable to redo anymore.");
    }
}
