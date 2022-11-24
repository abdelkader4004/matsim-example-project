package org.matsim.analys;

import com.conveyal.gtfs.model.Trip;
import org.locationtech.jts.geom.Geometry;
import org.matsim.api.core.v01.population.Activity;
import org.matsim.api.core.v01.population.Person;
import org.matsim.core.population.PopulationUtils;
import org.matsim.core.router.TripStructureUtils;
import org.matsim.core.utils.geometry.geotools.MGC;
import org.matsim.core.utils.geometry.transformations.TransformationFactory;
import org.matsim.core.utils.gis.ShapeFileReader;
import java.util.stream.Collectors;


public class countTripsMitteAndFriedrichshainKreuzberg {


    public static void main (String args[]){

        var shapeFileName ="C:\\Users\\abd\\Desktop\\Bezirke_-_Berlin\\Berlin_Bezirke.shp";
        var plansFileName="C:\\Users\\abd\\Desktop\\matsim-berlin-5.5.x\\output\\berlin-v5.5.3-1pct.output_plans.xml.gz";
        var transformation = TransformationFactory.getCoordinateTransformation("EPSG:31468","EPSG:3857");
        var features= ShapeFileReader.getAllFeatures(shapeFileName);
        var geometries =features.stream()
                .filter(simpleFeature -> simpleFeature.getAttribute("Gemeinde_s").equals("004") ||simpleFeature.getAttribute("Gemeinde_s").equals("006"))
                .map(simpleFeature ->(Geometry) simpleFeature.getDefaultGeometry())
                // .findAny()
                //.orElseThrow();
                .collect(Collectors.toList());

        var geometry1 =geometries.get(0);
        var geometry2 =geometries.get(1);
//var mitteGeometry =geometries;
        var population= PopulationUtils.readPopulation(plansFileName);

        var count=0;
        for(Person person: population.getPersons().values()){
            var plan=person.getSelectedPlan();
            var activities= TripStructureUtils.getActivities(plan,TripStructureUtils.StageActivityHandling.ExcludeStageActivities);
            var trips= TripStructureUtils.getTrips(plan);

            for (TripStructureUtils.Trip trip :trips){

                var Origincoord=trip.getOriginActivity().getCoord();
                var Destincoord=trip.getDestinationActivity().getCoord();
                var transformedOrigincoord =transformation.transform(Origincoord);
                var transformedDestincoord =transformation.transform(Destincoord);
                var geotoolsPointOrigin= MGC.coord2Point(transformedOrigincoord);
                var geotoolsPointDestin= MGC.coord2Point(transformedDestincoord);
                if((geometry1.contains(geotoolsPointOrigin)&&geometry2.contains(geotoolsPointDestin))||(geometry2.contains(geotoolsPointOrigin)&&geometry1.contains(geotoolsPointDestin))){
                    count++;
                }
            }
        }
        System.out.println(count+" Trips Between Mitte And Friedrichshain-Kreuzberg");
    }
}
