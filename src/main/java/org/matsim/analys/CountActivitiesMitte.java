package org.matsim.analys;

import org.locationtech.jts.geom.Geometry;
import org.matsim.api.core.v01.population.Activity;
import org.matsim.api.core.v01.population.Person;

import org.matsim.core.population.PopulationUtils;
import org.matsim.core.router.TripStructureUtils;
import org.matsim.core.utils.geometry.geotools.MGC;
import org.matsim.core.utils.geometry.transformations.TransformationFactory;
import org.matsim.core.utils.gis.ShapeFileReader;

import java.util.stream.Collectors;

public class CountActivitiesMitte {

    public static void main (String args[]){

var shapeFileName ="C:\\Users\\abd\\Desktop\\Bezirke_-_Berlin\\Berlin_Bezirke.shp";
var plansFileName="C:\\Users\\abd\\Desktop\\matsim-berlin-5.5.x\\output\\berlin-v5.5.3-1pct.output_plans.xml.gz";
var populationFile="C:\\Users\\abd\\Desktop\\matsim-berlin-5.5.x\\output\\berlin-v5.5.3-1pct.output_plans.xml.gz";
var transformation = TransformationFactory.getCoordinateTransformation("EPSG:31468","EPSG:3857");
var features=ShapeFileReader.getAllFeatures(shapeFileName);
        var geometries =features.stream()
        .filter(simpleFeature -> simpleFeature.getAttribute("Gemeinde_s").equals("001"))
        .map(simpleFeature ->(Geometry) simpleFeature.getDefaultGeometry())
               // .findAny()
                //.orElseThrow();
        .collect(Collectors.toList());

var mitteGeometry =geometries.get(0);
//var mitteGeometry =geometries;
var population= PopulationUtils.readPopulation(plansFileName);
var count=0;
for(Person person: population.getPersons().values()){
var plan=person.getSelectedPlan();
    var activities=TripStructureUtils.getActivities(plan,TripStructureUtils.StageActivityHandling.ExcludeStageActivities);
    for (Activity activity :activities){
        var coord=activity.getCoord();
        var transformedCoord =transformation.transform(coord);
        var geotoolsPoint= MGC.coord2Point(transformedCoord);
        if(mitteGeometry.contains(geotoolsPoint)){
count++;
        }
    }
}
System.out.println(count+" activities in Mitte");
    }
}
