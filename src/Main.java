import logic.Logic;
import model.Model;
import ui.Ui;

/**
 * Main app of the application.
 */
public class Main {
    public static void main(String args[]) {
        Model model = new Model();
        Logic logic = new Logic(model);
        new Ui().construct(logic);
    }
}
