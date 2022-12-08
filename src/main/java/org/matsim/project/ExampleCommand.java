package org.matsim.project;

import org.matsim.application.MATSimAppCommand;
import org.matsim.application.MATSimApplication;
import picocli.CommandLine;

@CommandLine.Command(name="Example_command",description = "My example")

public class ExampleCommand implements MATSimAppCommand {
    public static void main(String[] args) {
        new ExampleCommand().execute(args);
    }
    @Override
    public Integer call() throws Exception {
        System.out.println("Hello MATSim");
        return 0;
    }
}
