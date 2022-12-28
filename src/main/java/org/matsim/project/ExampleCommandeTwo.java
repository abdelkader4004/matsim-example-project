package org.matsim.project;

import org.matsim.application.MATSimAppCommand;
import org.matsim.application.MATSimApplication;
import picocli.CommandLine;

@CommandLine.Command(name = "example_command",description = "example command Two description")

public class ExampleCommandeTwo implements MATSimAppCommand {

    public static void main(String[] args) {
        new ExampleCommandeTwo().execute(args);
    }
    @Override
    public Integer call() throws Exception {

        System.out.println("السلام عليكم");

        return 0;
    }
}
