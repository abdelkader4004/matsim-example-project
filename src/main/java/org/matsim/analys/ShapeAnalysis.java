package org.matsim.analys;

import org.locationtech.jts.geom.Geometry;
import org.matsim.api.core.v01.population.Activity;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.population.PopulationUtils;
import org.matsim.core.router.TripStructureUtils;
import org.matsim.core.utils.geometry.CoordinateTransformation;
import org.matsim.core.utils.geometry.geotools.MGC;
import org.matsim.core.utils.geometry.transformations.TransformationFactory;
import org.matsim.core.utils.gis.ShapeFileReader;

public class ShapeAnalysis {

    private static final String shapeFile = "C:\\Users\\abd\\Desktop\\Bezirke_-_Berlin\\Berlin_Bezirke.shp";
    private static final String populationPath = "C:\\Users\\abd\\Desktop\\matsim-berlin-5.5.x\\output\\berlin-v5.5.3-1pct.output_plans.xml.gz";
    private static final String networkPath = "C:\\Users\\abd\\Desktop\\matsim-berlin-5.5.x\\output\\berlin-v5.5.3-1pct.output_network.xml.gz";
    private static final CoordinateTransformation transformation = TransformationFactory.getCoordinateTransformation("EPSG:31468", "EPSG:3857");

    private static final String gemeinde_s = "001";

    public static void main(String[] args) {

        var features = ShapeFileReader.getAllFeatures(shapeFile);
        var network = NetworkUtils.readNetwork(networkPath);
        var population = PopulationUtils.readPopulation(populationPath);

        var geometry = features.stream()
                .filter(feature -> feature.getAttribute("Gemeinde_s").equals(gemeinde_s))
                .map(feature -> (Geometry)feature.getDefaultGeometry())
                .findAny()
                .orElseThrow();

        int counter = 0;

        for (var person : population.getPersons().values()) {

            var plan = person.getSelectedPlan();
            var activities = TripStructureUtils.getActivities(plan, TripStructureUtils.StageActivityHandling.ExcludeStageActivities);

            for (var activity : activities) {

                var coord = activity.getCoord();
                var transformedCoord = transformation.transform(coord);

                if (geometry.covers(MGC.coord2Point(transformedCoord))) {
                    counter++;
                }
            }
        }
        System.out.println(counter + " activities in Mitte");

    }
}