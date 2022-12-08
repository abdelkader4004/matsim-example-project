package org.matsim.project;

import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.network.NetworkWriter;
import org.matsim.application.MATSimAppCommand;
import org.matsim.contrib.osm.networkReader.SupersonicOsmNetworkReader;
import org.matsim.core.utils.geometry.CoordinateTransformation;
import org.matsim.core.utils.geometry.transformations.TransformationFactory;
import picocli.CommandLine;



@CommandLine.Command(name="Example_Command2",description = "My example")

public class ExampleCommand2 implements MATSimAppCommand {
    public static void main(String[] args) {
        //new ExampleCommand2().execute(args);
        System.exit(new CommandLine(new ExampleCommand2()).execute(args));

    }
    @Override
    public Integer call() throws Exception {
        System.out.println("Hello MATSim");


        String file = "./brandenburg-latest.osm.pbf";
        String outputFile = "Output-NetworkCreation/matsim-network.xml.gz";
// you may choose your own target coordinate system, but UTM32 is a good choice if you run a simulation in Germany
        CoordinateTransformation coordinateTransformation = TransformationFactory.getCoordinateTransformation(TransformationFactory.WGS84, "EPSG:25832");
        Network network = new SupersonicOsmNetworkReader.Builder()
                .setCoordinateTransformation(coordinateTransformation)
                .build()
                .read(file);

        new NetworkWriter(network).write(outputFile);
        return 0;
    }
}
