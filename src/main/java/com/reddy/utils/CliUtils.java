package com.reddy.utils;


import com.googlecode.lanterna.gui2.MultiWindowTextGUI;
import com.googlecode.lanterna.gui2.dialogs.ActionListDialogBuilder;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicReference;

public class CliUtils {

    static Screen screen;

    public static void createScreen() throws IOException {
        screen = new DefaultTerminalFactory().createScreen();
        screen.startScreen();
    }

    public static void stopScreen() throws IOException {
        screen.stopScreen();
    }

    public static String selectOption(String label, String[] options) throws IOException {
        AtomicReference<String> result = new AtomicReference<>();
        MultiWindowTextGUI gui = new MultiWindowTextGUI(screen);

        ActionListDialogBuilder dialogBuilder = new ActionListDialogBuilder().setTitle("Select " + label).setDescription("Use arrow keys to navigate.");
        for (String option : options) {
            dialogBuilder.addAction(option, () -> {
                result.set(option);
            });
        }
        dialogBuilder.build().showDialog(gui);
        return result.get();

    }

    static Scanner scanner = new Scanner(System.in);

    public static String takeInput(String label) {
        System.out.println("Enter " + label + ":");
        return scanner.nextLine();
    }
}
