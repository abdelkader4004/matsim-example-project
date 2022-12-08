package org.matsim.project;

import org.matsim.api.core.v01.network.Network;
import org.matsim.application.MATSimAppCommand;
import org.matsim.application.options.CrsOptions;
import org.matsim.application.options.ShpOptions;
import org.matsim.contrib.osm.networkReader.SupersonicOsmNetworkReader;
import org.matsim.core.network.NetworkUtils;
import picocli.CommandLine;

import java.nio.file.Path;
import java.util.List;

@CommandLine.Command(name="network_create",description = "Create a network from OSM")
public class CreateNetwork implements MATSimAppCommand {
    @CommandLine.Parameters(defaultValue = "brandenburg-latest.osm.pbf", description = "cdfdsddfvd")
    private List<Path> input;
    @CommandLine.Option(names = "--output",defaultValue = "network.xml.gz")
    private Path output;
    // --shp bezirksgrenzenShape/bezirksgrenzen.shp
    @CommandLine.Mixin
        private ShpOptions shp =new ShpOptions();
    @CommandLine.Mixin
    private CrsOptions crs=new CrsOptions("EPSG:4326","EPSG:25832");

    public static void main(String[] args) {
        new CreateNetwork().execute(args);
    }
    @Override
    public Integer call() throws Exception {
        ShpOptions.Index index= shp.createIndex("EPSG:25832","_");
        SupersonicOsmNetworkReader reader=new SupersonicOsmNetworkReader.Builder()
                .setIncludeLinkAtCoordWithHierarchy((coord,integer)->index.contains(coord))
                .setCoordinateTransformation(crs.getTransformation())
                .build();
        Network network=reader.read(input.get(0));
        NetworkUtils.writeNetwork(network,output.toString());
        return 0;
    }
}
