package logic.exceptions;

public class InvalidSelectionException extends Exception {
    public static String ERR_MSG = "The part you selected to delete is not a valid proposition";

    public InvalidSelectionException() {
        super(ERR_MSG);
    }

    public InvalidSelectionException(String msg) {
        super(msg);
    }
}
