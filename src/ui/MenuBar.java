package ui;

import logic.Logic;
import logic.exceptions.FilePathException;
import logic.exceptions.FileReadException;

import javax.swing.*;

/**
 * The menubar of the application.
 */
public class MenuBar extends JMenuBar {
    private final Logic logic;

    /**
     * Initializes the menubar.
     * @param logic the logic component to control.
     * @param refresh the runnable procedure that refreshes the UI.
     */
    public MenuBar(Logic logic, Runnable refresh) {
        super();
        this.logic = logic;

        JMenu fileMenu = new JMenu("File");
        JMenuItem fileSave = new JMenuItem("Save");
        JMenuItem fileSaveAs = new JMenuItem("Save as ...");
        JMenuItem fileOpen = new JMenuItem("Open ...");
        fileMenu.add(fileSave);
        fileMenu.add(fileSaveAs);
        fileMenu.add(fileOpen);
        fileSave.addActionListener(e -> {
            logic.save();
        });
        fileSaveAs.addActionListener(e -> {
            boolean loopContinue = true;
            while (loopContinue) {
                String filePath = JOptionPane.showInputDialog("Please specify file path");
                try {
                    if (filePath == null) {
                        loopContinue = false;
                    }
                    logic.preparePath(filePath);
                    logic.save();
                    loopContinue = false;
                } catch (FilePathException fpe) {
                    JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), fpe.getMessage(),
                            "Invalid File Path Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        fileOpen.addActionListener(e -> {
            boolean loopContinue = true;
            while (loopContinue) {
                String filePath = JOptionPane.showInputDialog("Please specify file path");
                try {
                    if (filePath == null) {
                        loopContinue = false;
                    }
                    logic.open(filePath);
                    refresh.run();
                    loopContinue = false;
                } catch (FilePathException | FileReadException err) {
                    JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), err.getMessage(),
                            "Invalid File Path Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        add(fileMenu);
    }
}
