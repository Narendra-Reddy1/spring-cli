package com.reddy;

import picocli.CommandLine;

@CommandLine.Command(name = "spring", subcommands = {InstallCommand.class}, description = "Spring boot CLI")
public class SpringCliApp implements Runnable {
    public static void main(String[] args) {
        CommandLine.run(new SpringCliApp(), args);
    }

    @Override
    public void run() {
        System.out.println("Use sub-command: spring install create-start-app");
    }
}