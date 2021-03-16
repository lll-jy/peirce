package ui;

import javax.swing.*;

public class MenuBar extends JMenuBar {
    public MenuBar() {
        super();
        JMenu fileMenu = new JMenu("File");
        JMenuItem fileSave = new JMenuItem("Save");
        JMenuItem fileSaveAs = new JMenuItem("Save as ...");
        fileMenu.add(fileSave);
        fileMenu.add(fileSaveAs);

        JMenu editMenu = new JMenu("Edit");
        JMenuItem editClear = new JMenuItem("Clear");
        editMenu.add(editClear);

        JMenuItem helpMenu = new JMenuItem("Help");

        add(fileMenu);
        add(editMenu);
        add(helpMenu);
    }
}
