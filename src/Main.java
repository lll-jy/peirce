import model.Model;
import ui.Ui;

public class Main {
    public static void main(String args[]) {
        Model model = new Model();
        // new Ui().construct(model);
        reinitializeUi(model);
    }

    public static void reinitializeUi(Model model) {
        new Ui().construct(model);
    }
}
