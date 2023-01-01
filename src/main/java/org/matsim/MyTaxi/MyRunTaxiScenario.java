package org.matsim.MyTaxi;

import org.matsim.MyTaxi.taxi.run.RunTaxiScenario;
import org.matsim.core.utils.io.IOUtils;
import org.matsim.examples.ExamplesUtils;

import java.net.URL;

public class MyRunTaxiScenario {
    public static void main(String[] args) {
         RunTaxiScenario.run( IOUtils.extendUrl(ExamplesUtils.getTestScenarioURL( "mielec" ),
                 "mielec_taxi_config.xml" ),false);

    }
}
