import logic.Logic;
import model.Model;
import ui.Ui;

public class Main {
    public static void main(String args[]) {
        Model model = new Model();
        Logic logic = new Logic(model);
        new Ui().construct(logic);
    }
}
