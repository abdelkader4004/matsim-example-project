package org.matsim.onetruck;

import java.net.MalformedURLException;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) throws MalformedURLException {
         RunOneTruckExample.run(Path.of("C:\\Users\\abd\\IdeaProjects\\matsim-example-project\\examples\\dvrp-grid\\one_truck_config.xml").toUri().toURL(),
                 "C:\\Users\\abd\\IdeaProjects\\matsim-example-project\\examples\\dvrp-grid\\one_truck_vehicles.xml",
                 false,1);
    }
}
