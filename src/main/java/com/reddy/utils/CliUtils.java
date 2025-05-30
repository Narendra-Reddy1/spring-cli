package com.reddy.utils;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.ActionListDialogBuilder;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

public class CliUtils {

    static Screen screen;
    static MultiWindowTextGUI gui;

    public static void createScreen() throws IOException {
        screen = new DefaultTerminalFactory().createScreen();
        screen.startScreen();
        gui = new MultiWindowTextGUI(screen);
    }

    public static void stopScreen() throws IOException {
        screen.stopScreen();
    }

    public static String selectOption(String label, String[] options) throws IOException {
        AtomicReference<String> result = new AtomicReference<>();
        ActionListDialogBuilder dialogBuilder = new ActionListDialogBuilder()
                .setTitle("Select " + label)
                .setDescription("Use arrow keys to navigate.");
        for (String option : options) {
            dialogBuilder.addAction(option, () -> result.set(option));
        }
        dialogBuilder.build().showDialog(gui);
        return result.get();
    }

    public static String takeInput(String label) {
        // Create a dialog with a text box for input
        AtomicReference<String> result = new AtomicReference<>();
        BasicWindow window = new BasicWindow("Enter " + label);

        // Create a panel to hold the components
        Panel panel = new Panel();
        panel.setLayoutManager(new LinearLayout(Direction.VERTICAL));

        // Add a label and a text box
        panel.addComponent(new Label("Enter " + label + ":"));
        TextBox textBox = new TextBox().setPreferredSize(new TerminalSize(40, 1));
        panel.addComponent(textBox);

        // Add OK and Cancel buttons
        Panel buttonPanel = new Panel();
        buttonPanel.setLayoutManager(new LinearLayout(Direction.HORIZONTAL));
        buttonPanel.addComponent(new Button("OK", () -> {
            result.set(textBox.getText());
            window.close();
        }));
        buttonPanel.addComponent(new Button("Cancel", () -> {
            result.set(null);
            window.close();
        }));
        panel.addComponent(buttonPanel);

        // Set the panel as the window's content and show the dialog
        window.setComponent(panel);
        gui.addWindowAndWait(window);

        return result.get();
    }
}