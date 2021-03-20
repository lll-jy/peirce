package ui;

import logic.Logic;
import storage.Storage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

/**
 * The UI component of the application.
 */
public class Ui {
    private static final String IMG_DIR = "images";
    private static final String PROLOG_DIR = "prolog";
    public static String DC_IMG = String.format("%s/%s", IMG_DIR, "double_cut.png");
    public static String RDC_IMG = String.format("%s/%s", IMG_DIR, "remove_double_cut.png");
    public static String SYNTAX_PL = String.format("%s/%s", PROLOG_DIR, "syntax.pl");
    public static String TOKEN_OUTPUT = String.format("%s/%s", PROLOG_DIR, "tokens.txt");
    public static String PARSE_OUTPUT = String.format("%s/%s", PROLOG_DIR, "peirce.txt");

    private static final String[] directories = new String[]{IMG_DIR, PROLOG_DIR};
    private static final String[] files = new String[]{
            DC_IMG, RDC_IMG, IMG_DIR, SYNTAX_PL,
            TOKEN_OUTPUT, PARSE_OUTPUT, PROLOG_DIR};

    private static final String prologContent = "read_file(Lang,Tokens):-\n" +
            "  open(\"prolog/tokens.txt\",read,S), \n" +
            "  read_line_to_string(S,Lang), read_tokens(S,Tokens).\n" +
            "\n" +
            "read_tokens(S,[]):-\n" +
            "  at_end_of_stream(S), !.\n" +
            "read_tokens(S,[H|T]):-\n" +
            "  read_line_to_string(S,H), read_tokens(S,T).\n" +
            "\n" +
            "coq_term(l(X)) --> [X], {string(X)}.\n" +
            "coq_term(l(X)) --> [\"(\"], coq_prop(X), [\")\"].\n" +
            "\n" +
            "coq_prop(X) --> coq_term(X).\n" +
            "coq_prop(and(X,Y)) --> coq_term(X), coq_and, coq_term(Y).\n" +
            "coq_prop(not(X)) --> coq_not, coq_term(X).\n" +
            "coq_prop(not(and(not(X),not(Y)))) --> coq_term(X), coq_or, coq_term(Y).\n" +
            "coq_prop(not(and(X,not(Y)))) --> coq_term(X), coq_imply, coq_term(Y).\n" +
            "coq_prop(and(not(and(X,not(Y))),not(and(Y,not(X))))) --> coq_term(X), coq_biconditional, coq_term(Y).\n" +
            "\n" +
            "coq_and --> [\"/\\\\\"].\n" +
            "coq_or --> [\"\\\\/\"].\n" +
            "coq_not --> [\"~\"].\n" +
            "coq_imply --> [\"->\"].\n" +
            "coq_biconditional --> [\"<->\"].\n" +
            "\n" +
            "latex_term(l(X)) --> [X], {string(X)}.\n" +
            "latex_term(l(X)) --> [\"(\"], latex_prop(X), [\")\"].\n" +
            "\n" +
            "latex_prop(X) --> latex_term(X).\n" +
            "latex_prop(and(X,Y)) --> latex_term(X), latex_and, latex_term(Y).\n" +
            "latex_prop(not(X)) --> latex_not, latex_term(X).\n" +
            "latex_prop(not(and(not(X),not(Y)))) --> latex_term(X), latex_or, latex_term(Y).\n" +
            "latex_prop(not(and(X,not(Y)))) --> latex_term(X), latex_imply, latex_term(Y).\n" +
            "latex_prop(and(not(and(X,not(Y))),not(and(Y,not(X))))) --> latex_term(X), latex_biconditional, latex_term(Y).\n" +
            "\n" +
            "latex_and --> [\"\\\\land\"].\n" +
            "latex_and --> [\"\\\\wedge\"].\n" +
            "latex_or --> [\"\\\\lor\"].\n" +
            "latex_or --> [\"\\\\vee\"].\n" +
            "latex_not --> [\"\\\\lnot\"].\n" +
            "latex_not --> [\"\\\\neg\"].\n" +
            "latex_not --> [\"\\\\sim\"].\n" +
            "latex_imply --> [\"\\\\Rightarrow\"].\n" +
            "latex_imply --> [\"\\\\to\"].\n" +
            "latex_imply --> [\"\\\\rightarrow\"].\n" +
            "latex_imply --> [\"\\\\supset\"].\n" +
            "latex_imply --> [\"\\\\implies\"].\n" +
            "latex_biconditional --> [\"\\\\Leftrightarrow\"].\n" +
            "latex_biconditional --> [\"\\\\equiv\"].\n" +
            "latex_biconditional --> [\"\\\\leftrightarrow\"].\n" +
            "latex_biconditional --> [\"\\\\iff\"].\n" +
            "\n" +
            "functor_to_peirce(X,[X]):-string(X),!.\n" +
            "functor_to_peirce(l(X),L):-!, functor_to_peirce(X,L).\n" +
            "functor_to_peirce(not(X),[frame(L)]):-!, functor_to_peirce(X,L).\n" +
            "functor_to_peirce(and(X,Y),L):-\n" +
            "  functor_to_peirce(X,XL), functor_to_peirce(Y,YL),\n" +
            "  append(XL,YL,L).\n" +
            "\n" +
            "write_to_stream([],_):-!.\n" +
            "write_to_stream([H|T],O):-\n" +
            "  write_literal(H,O), write_to_stream(T,O).\n" +
            "\n" +
            "write_literal(frame(X),O):-\n" +
            "  !, write(O,\"[ \"), write_to_stream(X,O), write(O,\"] \").\n" +
            "write_literal(X,O):-\n" +
            "  string(X), write(O,X), write(O,\" \").\n" +
            "\n" +
            "write_file(P):-\n" +
            "  open(\"prolog/peirce.txt\",write,O), write_to_stream(P,O), close(O).\n" +
            "\n" +
            "get_structure(S):-\n" +
            "  read_file(\"Coq\",Tokens), !, coq_prop(S,Tokens,[]).\n" +
            "get_structure(S):-\n" +
            "  read_file(\"LeTeX\",Tokens), latex_prop(S,Tokens,[]).\n" +
            "\n" +
            "execute:-\n" +
            "  open(\"prolog/peirce.txt\",write,O), write(O,\"!!ERROR\"), close(O),\n" +
            "  get_structure(S), functor_to_peirce(S,L), write_file(L).\n" +
            "\n" +
            ":-execute.\n" +
            "\n" +
            ":-halt.";

    private final JFrame frame;

    /**
     * Initialize a UI frame of Peirce Alpha Proof Assistant.
     */
    public Ui() {
        frame = new JFrame("Peirce Alpha Proof Assistant");
    }

    /**
     * Constructs content of the UI.
     * @param logic the component that handles with logic.
     */
    public void construct(Logic logic) {
        // File creation
        for (String s : directories) {
            try {
                Storage.createDirectory(String.format("%s/", s));
            } catch (Exception e) {
                assert false;
            }
        }
        getImage("https://i.ibb.co/7Xzf5Pd/double-cut.png", DC_IMG);
        getImage("https://i.ibb.co/qp4vmzW/remove-double-cut.png", RDC_IMG);
        try {
            FileWriter fw = new FileWriter(SYNTAX_PL);
            fw.write(prologContent);
            fw.close();
        } catch (IOException e) {
            assert false;
        }

        // Frame creation
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Panel setup
        ProofPanel proofPanel = new ProofPanel(logic);
        JPanel panel = new JPanel();
        InputPanel inputPanel = new InputPanel(logic);
        Runnable refresh = () -> {
            panel.revalidate();
            panel.repaint();
            proofPanel.refresh();
        };
        inputPanel.setRefresh(refresh);
        JMenuBar menuBar = new MenuBar(logic, () -> {
            inputPanel.refresh();
            refresh.run();
        });

        panel.add(proofPanel);

        // Frame building
        frame.setVisible(true);
        frame.setJMenuBar(menuBar);
        frame.getContentPane().add(panel);
        frame.pack();
        frame.setSize(900, 600);
        frame.getContentPane().add(BorderLayout.WEST, inputPanel);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosed(e);
                for (String s : files) {
                    File file = new File(s);
                    file.delete();
                }
            }
        });
    }

    // Reference: https://stackoverflow.com/questions/10292792/getting-image-from-url-java/26234404
    /**
     * Gets image from deterministic url.
     * @param urlStr the url of the image.
     * @param dest the destination file name.
     */
    private void getImage(String urlStr, String dest) {
        try {
            URL url = new URL(urlStr);
            InputStream is = url.openStream();
            OutputStream os = new FileOutputStream(dest);

            byte[] b = new byte[2048];
            int length;
            while ((length = is.read(b)) != -1) {
                os.write(b, 0, length);
            }
            is.close();
            os.close();
        } catch (Exception e) {
            assert false;
        }
    }
}
