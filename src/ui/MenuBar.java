package ui;

import logic.Logic;

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

        JMenuItem helpMenu = new JMenuItem("Help");

        add(fileMenu);
        add(helpMenu);
    }
}
