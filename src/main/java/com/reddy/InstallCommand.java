package com.reddy;


import picocli.CommandLine;

@CommandLine.Command(name = "install", description = "install operation", subcommands = {CreateStarterApp.class})
public class InstallCommand implements Runnable {

    @Override
    public void run() {
        System.out.println("Install Command>>>>");
    }
}
