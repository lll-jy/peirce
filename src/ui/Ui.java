package ui;

import logic.Logic;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * The UI component of the application.
 */
public class Ui {
    private static String IMG_DIR = "img";
    private static String PROLOG_DIR = "prolog";
    public static String DC_IMG = String.format("%s/%s", IMG_DIR, "double_cut.png");
    public static String RDC_IMG = String.format("%s/%s", IMG_DIR, "remove_double_cut.png");

    private static String[] directories = new String[]{String.format("%s/", IMG_DIR), String.format("%s/", PROLOG_DIR)};
    private static String[] files = new String[]{DC_IMG, RDC_IMG, IMG_DIR};

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
        try {
            Path path = Paths.get(String.format("%s/", IMG_DIR));
            Files.createDirectories(path);
        } catch (IOException e) {
            assert false;
        }
        getImage("https://i.ibb.co/7Xzf5Pd/double-cut.png", DC_IMG);
        getImage("https://i.ibb.co/qp4vmzW/remove-double-cut.png", RDC_IMG);

        // Frame creation
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Panel setup
        ProofPanel proofPanel = new ProofPanel(logic);
        JPanel panel = new JPanel();
        JMenuBar menuBar = new MenuBar();
        JScrollPane inputPanel = new InputPanel(logic,  () -> {
            panel.revalidate();
            panel.repaint();
            proofPanel.refresh();
        });

        // TODO: other UI components
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
