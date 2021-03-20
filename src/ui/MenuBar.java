package ui;

import logic.Logic;
import logic.exceptions.FilePathException;

import javax.swing.*;

/**
 * The menubar of the application.
 */
public class MenuBar extends JMenuBar {
    private final Logic logic;

    /**
     * Initializes the menubar.
     */
    public MenuBar(Logic logic) {
        super();
        this.logic = logic;

        JMenu fileMenu = new JMenu("File");
        JMenuItem fileSave = new JMenuItem("Save");
        JMenuItem fileSaveAs = new JMenuItem("Save as ...");
        fileMenu.add(fileSave);
        fileMenu.add(fileSaveAs);
        fileSave.addActionListener(e -> {
            logic.save();
        });
        fileSaveAs.addActionListener(e -> {
            boolean loopContinue = true;
            while (loopContinue) {
                String filePath = JOptionPane.showInputDialog("Please specify file path");
                try {
                    logic.setFilePath(filePath);
                    logic.save();
                    loopContinue = false;
                } catch (FilePathException fpe) {
                    JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), fpe.getMessage(),
                            "Invalid File Path Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        JMenuItem helpMenu = new JMenuItem("Help");

        add(fileMenu);
        add(helpMenu);
    }
}
